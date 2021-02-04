/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Jamie Mansfield <https://jamiemansfield.me/>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.jamiemansfield.csv;

import me.jamiemansfield.string.StringReader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A parser for CSV files.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class CsvParser implements Closeable {

    private final BufferedReader reader;

    public CsvParser(final BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Parses the file.
     *
     * @return A list of the columns
     */
    public List<CsvRow> parse() throws IOException {
        final String headerLine = this.reader.readLine();
        final List<String> headers = Arrays.asList(headerLine.toLowerCase().split(","));
        final List<CsvRow> rows = new ArrayList<>();

        // Start from 1 to account for the header line.
        final AtomicInteger lineCount = new AtomicInteger(1);
        this.reader.lines().forEach(line -> {
            final int lineNum = lineCount.incrementAndGet();
            try {
                rows.add(CsvParser.this.parseLine(line, lineNum, headers));
            }
            catch (final CsvParsingException ex) {
                throw ex;
            }
            catch (final Throwable ex) {
                throw new CsvParsingException("Failed to read", line, lineNum, ex);
            }
        });

        this.reader.close();
        return rows;
    }

    private CsvRow parseLine(final String line, final int lineNum, final List<String> headers) {
        final List<String> values = new ArrayList<>();
        final StringReader reader = new StringReader(line);

        while (reader.available()) {
            final boolean isQuotedEntry = reader.peek() == '"';

            final int start = reader.index() + (isQuotedEntry ? 1 : 0);
            if (isQuotedEntry) {
                reader.advance();
                while (reader.available() && reader.peek() != '"') {
                    reader.advance();
                }

                // Unterminated quoted entry
                if (!reader.available()) {
                    throw new CsvParsingException("Unterminated quoted entry", line, lineNum);
                }
                reader.advance();
            }
            else {
                while (reader.available() && reader.peek() != ',')  {
                    reader.advance();
                }
            }
            final int end = reader.index() - (isQuotedEntry ? 1 : 0);
            values.add(reader.substring(start, end));

            if (reader.available()) {
                reader.advance();
                // Special case , at end of line
                if (!reader.available()) {
                    values.add("");
                }
            }
        }

        final Map<String, String> studentMap = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            studentMap.put(headers.get(i).trim(), values.get(i).trim());
        }
        return new CsvRow(studentMap);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}
