/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import org.springframework.util.SerializationUtils

import spock.lang.Specification
import yakworks.commons.beans.PropertyTools
import yakworks.commons.testing.pogos.Gadget
import yakworks.commons.testing.pogos.Thing

import java.time.LocalDate

class MetaMapSpec extends Specification {

    Map testMap(){
        return [
            name:"Bart",
            age:45,
            other:"stuff",
            info: [
                phone: "1234", email: "jo@jo.com",
                nested: [x:'x', y:'y'],
                infoTags:[[id:1, code: "tag1"], [id:2, code: "tag2"]]
            ],
            things:[
                [id:1, name: "thing1", tags:[[id:1, code: "tag1"], [id:2, code: "tag2"]]],
                [id:2, name: "thing2", tags:[[id:2, code: "tag2"], [id:3, code: "tag3"]]],
                [id:3, name: "thing3", tags:[]]
            ],
            'class': 'wtf',
            'errors': [1, 2, 3]

        ]
    }

    void 'test default get includes'() {
        when:
        Map tobj = testMap()
        def map = new MetaMap(tobj)

        def includes = map.getIncludes()

        then:
        7 == map.size()
        7 == includes.size()
        ['name', 'age', 'other', 'info', 'things', 'errors', 'class'].sort() == includes.sort() as List
    }

    void 'test with MetaEntity'() {
        when:
        Map tobj = testMap()
        def incs = ["name", "info.phone", "errors"]
        MetaEntity ment = BasicMetaEntityBuilder.build(Object, incs)
        //MetaEntity ment = MetaEntity.of(["name", "info.phone"])
        def map = new MetaMap(tobj, ment)

        def includes = map.getIncludes()

        then:
        ['name', 'errors', 'info'] == includes as List
        map == [
            name:"Bart",
            info: [
                phone: "1234"
            ],
            errors:[1, 2, 3]
        ]
    }

    void "test includes for object type"() {
        setup:
        def includes = ['id', 'name', 'localDate', 'thing.name', ]
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)

        Gadget gadget = new Gadget(id:1, name:"test", localDate: LocalDate.now(), thing:new Thing(name:"test"))
        def metamap = new MetaMap(gadget, ment)

        when:
        Set<String> incl = metamap.getIncludes()

        then:
        incl == ["id", "name", "localDate", "thing"] as Set<String>
    }

    void testSelectSubMap() {

        when:
        def map = new MetaMap(testMap())

        def submap = map['name', 'age']

        then:
        2 == submap.size()
        "Bart" == submap.name
        45 == submap.age
    }

    void testIsEmpty() {
        expect:
        def map = new MetaMap(testMap())
        !map.isEmpty()

        def emptyMap = [:]
        emptyMap.isEmpty()
        def mapEmpty = new MetaMap(emptyMap)
        mapEmpty.isEmpty()
    }

    void testContainsKey() {
        expect:
        def map = new MetaMap(testMap())
        map.containsKey("name")
        map.containsKey("age")
        !map.containsKey("fo")
    }

    void testContainsValue() {
        when:
        def map = new MetaMap([name:"Homer", age:45])

        then:
        map == [name:"Homer", age:45]
        map.containsValue("Homer")
        map.containsValue(45)
        !map.containsValue("fo")
    }

    void testGet() {
        when:
        def map = new MetaMap(testMap())

        then:
        "Bart" == map.get("name")
        "Bart" == map.name
        "Bart" == map['name']

        45 == map.get("age")
        45 == map.age
        45 == map['age']

        PropertyTools.getProperty(map, "info.phone") == "1234"

        map.foo == null
        map['foo'] == null
        map.get('foo') == null


    }

    void "get nested"() {
        when:
        def map = new MetaMap(testMap())

        then:
        map.info instanceof MetaMap

    }

    void "put test"() {
        when:
        def map = new MetaMap(testMap())
        def old = map.put("name", "lisa")

        then:
        "Bart" == old
        "lisa" == map.name
    }

    void testKeySet() {
        when:
        def map = new MetaMap(testMap())
        def keys = map.keySet()

        then:
        keys.size() == 7
        keys.contains("name")
        keys.contains("age")
    }

    void testValues() {
        when:
        def map = new MetaMap(testMap())
        def values = map.values()

        then:
        values.contains("Bart")
        values.contains(45)
    }

    void "test entrySet"() {
        when:
        def map = new MetaMap(testMap())
        def entset = map.entrySet()

        then:
        entset.size() == 7
        for(entry in map.entrySet()) {
            assert map.getIncludes().contains(entry.key)
        }
    }

}
