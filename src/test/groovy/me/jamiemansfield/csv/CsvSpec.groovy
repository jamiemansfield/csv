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

package me.jamiemansfield.csv

import spock.lang.Specification

class CsvSpec extends Specification {

    def "simple file"() {
        given:
        def input = """name,surname,country
jamie,mansfield,uk
john,doe,canada
"""
        def rows = new CsvParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.bytes)))).withCloseable {
            return it.parse()
        }

        expect:
        rows[0].getValue("name").isPresent()
        rows[0].getValue("name").get() == 'jamie'
        rows[0].getValue("surname").isPresent()
        rows[0].getValue("surname").get() == 'mansfield'
        rows[0].getValue("country").isPresent()
        rows[0].getValue("country").get() == 'uk'

        rows[1].getValue("name").isPresent()
        rows[1].getValue("name").get() == 'john'
        rows[1].getValue("surname").isPresent()
        rows[1].getValue("surname").get() == 'doe'
        rows[1].getValue("country").isPresent()
        rows[1].getValue("country").get() == 'canada'
    }

    def "complex file"() {
        given:
        def input = """name,surname,country
jamie,mansfield,
john,,canada
"""
        def rows = new CsvParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.bytes)))).withCloseable {
            return it.parse()
        }

        expect:
        rows[0].getValue("name").isPresent()
        rows[0].getValue("name").get() == 'jamie'
        rows[0].getValue("surname").isPresent()
        rows[0].getValue("surname").get() == 'mansfield'
        rows[0].getValue("country").isPresent()
        rows[0].getValue("country").get() == ''

        rows[1].getValue("name").isPresent()
        rows[1].getValue("name").get() == 'john'
        rows[1].getValue("surname").isPresent()
        rows[1].getValue("surname").get() == ''
        rows[1].getValue("country").isPresent()
        rows[1].getValue("country").get() == 'canada'
    }

}
