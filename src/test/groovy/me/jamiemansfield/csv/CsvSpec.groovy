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
