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

        when:
        def rows = read(input)

        then:
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

        when:
        def rows = read(input)

        then:
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

    def 'broken file'() {
        given:
        def input = """name,surname,country
jamie,mansfield,,hy
"""

        when:
        def rows = read(input)

        then:
        def e = thrown(CsvParsingException)
        e.lineNum == 2
        e.line == "jamie,mansfield,,hy"
    }

    def 'quoted entries file'() {
        given:
        def input = """name,surname,country,description
jamie,mansfield,"United Kingdom of Great Britain and Northern Ireland","Programmer, and more"
"""

        when:
        def rows = read(input)

        then:
        rows[0].getValue("name").isPresent()
        rows[0].getValue("name").get() == 'jamie'
        rows[0].getValue("surname").isPresent()
        rows[0].getValue("surname").get() == 'mansfield'
        rows[0].getValue("country").isPresent()
        rows[0].getValue("country").get() == 'United Kingdom of Great Britain and Northern Ireland'
        rows[0].getValue("description").isPresent()
        rows[0].getValue("description").get() == 'Programmer, and more'
    }

    def 'embedded quotes file'() {
        given:
        def input = """a,b
beep,"test ""testing"" test"
"""

        when:
        def rows = read(input)

        then:
        rows[0].getValue("a").isPresent()
        rows[0].getValue("a").get() == 'beep'
        rows[0].getValue("b").isPresent()
        rows[0].getValue("b").get() == 'test "testing" test'
    }

    def 'broken embedded quotes file'() {
        given:
        def input = """a,b
beep,"test ""testing"
"""

        when:
        def rows = read(input)

        then:
        def e = thrown(CsvParsingException)
        e.lineNum == 2
        e.line == 'beep,"test ""testing"'
    }

    static def read(final String input) {
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(input.bytes);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(bais));
             final CsvParser parser = new CsvParser(reader)) {
            return parser.parse();
        }
    }

}
