/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.beans

import java.time.LocalDate

import groovy.transform.CompileStatic

import spock.lang.Specification
import yakworks.commons.testing.pogos.Gadget
import yakworks.commons.testing.pogos.JavaThing
import yakworks.commons.testing.pogos.Thing

class BeanToolsSpec extends Specification{

    @CompileStatic
    static class GadgetDTO {
        Long id = 9
        String name = 'Galt'
        BigDecimal bigDecimal = 99.99
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

    Map gadgetData(){
        return [
            id: 1,
            name: 'Galts',
            inactive: true,
            enabled: true,
            localDate: "2023-01-02",
            thing: [name: 'thing1'],
            thingList: [[name: 'thing2'], [name: 'thing3']],
            mapData: [ key1: [name: 'mapObj1'],  key2: [name: 'mapObj2']],
            stringList: ['string1', 'string2'],
            kind: "PARENT"
        ]
    }

    void "simple setProps"() {
        when:
        def au = BeanTools.setProps(new Gadget(), new GadgetDTO())

        then:
        au.id == 9
        au.name == 'Galt'
        au.bigDecimal == 99.99
    }

    void "simple setProps java"() {
        when:

        def jt = BeanTools.setProps(new JavaThing(), [name: "bill"])

        then:
        jt.name == 'bill'
    }

    void "JavaThing props"() {
        when:
        def gt = new Thing(name: 'bill')
        def jt = new JavaThing()
        jt.name = "bill"

        then:
        gt.properties["name"] == 'bill'
        jt.properties["name"] == 'bill'
    }

    void "merge with nested, BROKEN LIMITATIONS"() {
        when:
        Map dta = gadgetData()
        //PROBLEM1, remove the LocalDate as it wont bind
        dta.remove("localDate")
        Gadget au = BeanTools.merge(new Gadget(), dta)

        then:
        au.name == 'Galts'
        au.thing instanceof Thing
        au.thing.name == 'thing1'

        au.thingList.size() == 2
        au.mapData.size() == 2
        //NOTE: THIS IS WHERE IT BREAKS DOWN, PUTS MAP IN AS THINGS
        // au.things[0] instanceof AdminUser.Thing
        // au.things[0].name == 'thing2'

    }

    void "bind with nested objects"() {
        when:
        def thing1 = new Thing(name: "thing1")
        def thing2 = new Thing(name: "thing2")

        Gadget au = BeanTools.bind(new Gadget(), [
            name: 'Galts',
            thing: thing1,
            thingList: [thing1, thing2],
        ])

        then:
        au.name == 'Galts'
        au.thing instanceof Thing
        au.thing.name == 'thing1'
        au.thingList.size() == 2
        //bind converts to map basically, so thing1 get bound in as 2 different objects.
        au.thing != au.thingList[0]
        // //NOTE: THIS IS WHERE IT BREAKS DOWN, PUTS MAP IN AS THINGS
        // au.things[0] instanceof AdminUser.Thing
        // au.things[0].name == 'thing2'

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
        au.things.size() == 2
        au.mapObj.size() == 2
        au.mapThings.size() == 2
        //smoke test
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

    void "bind new Gadget with underlyng jackson"(){
        when:
        Gadget gadget = BeanTools.bind(gadgetData(), Gadget);

        then:
        gadget.name == 'Galts'
        gadget.inactive
        gadget.enabled
        gadget.localDate == LocalDate.parse("2023-01-02")
        gadget.stringList == ['string1', 'string2']
        gadget.kind == Gadget.Kind.PARENT
        gadget.thing instanceof Thing
        gadget.thing.name == 'thing1'
        gadget.thingList.size() == 2
        //smoke test
        gadget.thingList[0] instanceof Thing
        gadget.thingList[0].name == 'thing2'
        gadget.mapData.size() == 2
    }

    void "bind instance Gadget"(){
        when:
        Gadget gadget = BeanTools.bind(new Gadget(), gadgetData());

        then:
        gadget.name == 'Galts'
        gadget.inactive
        gadget.enabled
        gadget.localDate == LocalDate.parse("2023-01-02")
        gadget.stringList == ['string1', 'string2']
        gadget.kind == Gadget.Kind.PARENT
        gadget.thing instanceof Thing
        gadget.thing.name == 'thing1'
        gadget.thingList.size() == 2
        //smoke test
        gadget.thingList[0] instanceof Thing
        gadget.thingList[0].name == 'thing2'
        gadget.mapData.size() == 2
    }
}
