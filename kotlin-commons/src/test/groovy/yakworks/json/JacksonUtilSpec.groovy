/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.json

import java.time.LocalDate
import java.time.LocalDateTime

import groovy.transform.CompileStatic

import spock.lang.Specification
import yakworks.commons.testing.pogos.Thing

/**
 * sanity checks for streaming to a file
 */
class JacksonUtilSpec extends Specification {

    @CompileStatic
    static class AdminUser {
        String name
        String city
        Integer age
        String onlyAdmin = "foo"
        Thing thing
        List<Thing> things = [] as List<Thing>

        @CompileStatic
        static class Thing {
            String name
        }

        static String someStatic = "some val"
    }

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

    void "mapper sanity"() {
        when:
        def mapper = JacksonUtil.objectMapper

        then:
        mapper
    }

    void "sanity check toJson"() {
        when:
        String res = JacksonUtil.toJson([foo: 1, bar: 'buzz'])

        then:
        res == '{"foo":1,"bar":"buzz"}'
    }

    void "full toJson"() {
        when:
        String res = JacksonUtil.toJson(generateData(1))

        then:
        def expected = '{"num":"1","inactive":false,"amount":0.00,"localDate":"2021-02-01",' +
            '"localDateTime":"2017-10-19T11:40:00","ext":{"name":"bill"},"list":["foo","bar"],"currency":"USD",' +
            '"thing":{"id":1,"name":"joe"}}'
        res == expected
    }

    void "AdminUser toJson"() {
        when:
        String res = JacksonUtil.toJson(new AdminUser(name: "Bob"))

        then:
        def expected = '{"name":"Bob","onlyAdmin":"foo","things":[]}'
        res == expected
    }

    void "parseJson"() {
        when:
        def jsonString = '{"num":"1","inactive":false,"amount":0.00,"localDate":"2021-02-01"}'
        Map obj = JacksonUtil.parseJson(jsonString, Map)

        then:
        obj == [num: '1', inactive: false, amount: 0.00, localDate: "2021-02-01"]
    }

    void "using jackson to bind"(){
        when:
        // Create ObjectMapper instance
        // Converting POJO to Map
        // Map<String, Object> map = mapper.convertValue(foo, new TypeReference<Map<String, Object>>() {});
        // Convert Map to POJO
        def map = [
            name: 'Galts',
            city: 'Gulch',
            age: 22,
            thing: [name: 'thing1'],
            things: [[name: 'thing2'], [name: 'thing3']],
            someStatic: "no bind"
        ]
        AdminUser au = JacksonUtil.bind(map, AdminUser)

        then:
        au.name == 'Galts'
        au.city == 'Gulch'
        au.age == 22
        au.thing instanceof AdminUser.Thing
        au.thing.name == 'thing1'
        au.things[0] instanceof AdminUser.Thing
        au.things[0].name == 'thing2'
        //should not have bind the static
        au.someStatic == "some val"

        when:
        AdminUser au2 = JacksonUtil.bindUpdate(new AdminUser(), map);

        then:
        au2.name == 'Galts'
        au2.city == 'Gulch'
        au2.age == 22
        au2.thing instanceof AdminUser.Thing
        au2.thing.name == 'thing1'
        au2.things[0] instanceof AdminUser.Thing
        au2.things[0].name == 'thing2'
    }

}
