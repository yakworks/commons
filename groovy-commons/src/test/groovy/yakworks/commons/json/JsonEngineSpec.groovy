/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.json

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime

import groovy.json.StreamingJsonBuilder

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import spock.lang.Specification
import yakworks.commons.lang.IsoDateUtil
import yakworks.commons.testing.pogos.Thing
import yakworks.commons.util.BuildSupport

/**
 * sanity checks for streaming to a file
 */
class JsonEngineSpec extends Specification {

    Map generateData(Long id) {
        return [
            num: "$id",
            inactive: (id % 2 == 0),
            amount: (id - 1) * 1.25,
            localDate   : LocalDate.parse('2021-02-01'),
            localDateTime  : LocalDateTime.parse("2017-10-19T11:40:00"),
            ext:[ name: "bill"],
            list: ['foo', 'bar'],
            currency: Currency.getInstance("USD"),
            someNull: null, //should not show by default
            thing: Thing.of(1, 'joe')
        ]
    }

    void "sanity check toJson"() {
        when:
        String res = JsonEngine.toJson([foo: 1, bar: 'buzz'])

        then:
        res == '{"foo":1,"bar":"buzz"}'
    }

    void "full toJson"() {
        when:
        String res = JsonEngine.toJson(generateData(1))

        then:
        def expected = '{"num":"1","inactive":false,"amount":0.00,"localDate":"2021-02-01",' +
            '"localDateTime":"2017-10-19T11:40:00","ext":{"name":"bill"},"list":["foo","bar"],"currency":"USD",' +
            '"thing":{"id":1,"name":"joe"}}'
        res == expected
    }

}
