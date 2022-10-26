/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.beans

import groovy.transform.CompileStatic

import spock.lang.Specification

class BeanToolsSpec extends Specification{

    @CompileStatic
    static class User {
        String name = 'Galt'
        String city = 'Gulch'
        Integer age = 22
        String onlyUser = "foo"
    }

    @CompileStatic
    static class AdminUser {
        String name
        String city
        Integer age
        String onlyAdmin = "foo"
        Thing thing
        List<Thing> things = [] as List<Thing>
        Map<String,Object> mapObj = [:] as Map<String,Object>
        Map<String,Thing> mapThings = [:] as Map<String,Thing>

        @CompileStatic
        static class Thing {
            String name
        }
    }


    void "simple setProps"() {
        when:
        def au = BeanTools.setProps(new AdminUser(), new User())

        then:
        au.name == 'Galt'
        au.city == 'Gulch'
        au.age == 22
    }

    void "merge with nested"() {
        when:
        def au = BeanTools.merge(new AdminUser(), [
            name: 'Galts',
            city: 'Gulch',
            age: 22,
            thing: [name: 'thing1'],
            things: [[name: 'thing2'], [name: 'thing3']],
            mapObj: [
                key1: [name: 'mapThing1'],
                key2: [name: 'mapThing2']
            ],
            mapThings: [
                key1: [name: 'mapThing1'],
                key2: [name: 'mapThing2']
            ]
        ])

        then:
        au.name == 'Galts'
        au.city == 'Gulch'
        au.age == 22
        au.thing instanceof AdminUser.Thing
        au.thing.name == 'thing1'
        //NOTE: THIS IS WHERE IT BREAKS DOWN, SEE
    }

    Map getUserData(){
        return [
            name: 'Galts',
            city: 'Gulch',
            age: 22,
            thing: [name: 'thing1'],
            things: [[name: 'thing2'], [name: 'thing3']]
        ]
    }

    void "bind new or existing with underlyng jackson"(){
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
            mapObj: [
                key1: [name: 'mapObj1'],
                key2: [name: 'mapObj2']
            ],
            mapThings: [
                key1: [name: 'mapThing1'],
                key2: [name: 'mapThing2']
            ],
            foo: 'bar' //make sure non existing wont error
        ]
        AdminUser au = BeanTools.bind(map, AdminUser);

        then:
        au.name == 'Galts'
        au.city == 'Gulch'
        au.age == 22
        au.thing instanceof AdminUser.Thing
        au.thing.name == 'thing1'
        au.things[0] instanceof AdminUser.Thing
        au.things[0].name == 'thing2'

        when:
        AdminUser au2 = BeanTools.bind(new AdminUser(), map);

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
