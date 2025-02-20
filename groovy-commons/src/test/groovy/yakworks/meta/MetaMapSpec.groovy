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
        return [name:"Bart", age:45, other:"stuff", info: [ phone: "1234", email: "jo@jo.com" ]]
    }

    void 'test default get includes'() {

        when:
        Map tobj = testMap()
        def map = new MetaMap(tobj)

        def includes = map.getIncludes()

        then:
        4 == map.size()
        4 == includes.size()
        ['name', 'age', 'other', 'info'].containsAll(includes)
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
        keys.size() == 4
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
        entset.size() == 4
        for(entry in map.entrySet()) {
            map.getIncludes().contains(entry.key)
        }
    }

    void "test serialize Gadget"() {
        setup:
        Gadget gadget = new Gadget(id:1, name:"test", localDate: LocalDate.now(), thing:new Thing(name:"test"))

        when:
        def serialGadget = SerializationUtils.serialize(gadget)
        assert serialGadget
        def deserialGadget = SerializationUtils.deserialize(serialGadget)
        assert deserialGadget

        then:
        deserialGadget.id == gadget.id
    }

    void "test serialize"() {
        setup:
        def includes = ['id', 'name', 'localDate', 'thing.name']
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)

        Gadget gadget = new Gadget(id:1, name:"test", localDate: LocalDate.now(), thing:new Thing(name:"test"))
        def metamap = new MetaMap(gadget, ment)

        when:
        def serialMetamap = SerializationUtils.serialize(metamap)
        assert serialMetamap
        MetaMap deserialMetamap = SerializationUtils.deserialize(serialMetamap) as MetaMap
        assert deserialMetamap

        then:
        noExceptionThrown()
        deserialMetamap == metamap
        metamap.getEntityClass() == deserialMetamap.getEntityClass()
        //this will fail as the Gadget is not same instance and equals is not implemented
        metamap.getEntity() == deserialMetamap.getEntity()

    }

    void "test serialize when Map entity"() {
        when:
        Map tobj = testMap()
        def metamap = new MetaMap(tobj)

        def includes = metamap.getIncludes()

        then:
        4 == metamap.size()
        4 == includes.size()
        ['name', 'age', 'other', 'info'].containsAll(includes)

        when:
        def serialMetamap = SerializationUtils.serialize(metamap)
        assert serialMetamap
        MetaMap deserialMetamap = SerializationUtils.deserialize(serialMetamap) as MetaMap
        assert deserialMetamap

        then:
        noExceptionThrown()
        deserialMetamap == metamap
        metamap.getEntityClass() == deserialMetamap.getEntityClass()
        //this will fail as the Gadget is not same instance and equals is not implemented
        metamap.getEntity() == deserialMetamap.getEntity()
        def includes2 = deserialMetamap.getIncludes()
        includes2 == includes
    }

}
