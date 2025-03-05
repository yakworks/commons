/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import java.time.LocalDate

import org.springframework.util.SerializationUtils

import spock.lang.Specification
import yakworks.commons.beans.PropertyTools
import yakworks.commons.testing.pogos.Gadget
import yakworks.commons.testing.pogos.Thing

class MetaMapHydrateSerializeSpec extends Specification {

    Map testMap(){
        return [name:"Bart", age:45, other:"stuff", info: [ phone: "1234", email: "jo@jo.com" ]]
    }
    //serailzes and then deserializes the metaMap
    MetaMap serializePipe(MetaMap metamap){
        //metamap.hydrate()
        def serialMetamap = SerializationUtils.serialize(metamap)
        assert serialMetamap
        MetaMap deserialMetamap = SerializationUtils.deserialize(serialMetamap) as MetaMap
        assert deserialMetamap
        return deserialMetamap
    }

    PogoBean pogoBean(){
        new PogoBean(
            name:"Bart", age: 45, other:"stuff",
            info: [ phone: "1234", email: "jo@jo.com" ],
            nested: new NestedBean( prop1: 'foo')
        )
    }

    void 'test default get includes'() {

        when:
        Map tobj = testMap()
        def map = serializePipe( new MetaMap(tobj) )

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
        def metamap = serializePipe( new MetaMap(gadget, ment) )

        when:
        Set<String> incl = metamap.getIncludes()

        then:
        incl == ["id", "name", "localDate", "thing"] as Set<String>
    }

    void "test simple get"() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )
        then:
        map['name'] == 'Bart'
    }

    void testSelectSubMap() {

        when:
        def map = serializePipe( new MetaMap(testMap()) )

        def submap = map['name', 'age']

        then:
        2 == submap.size()
        "Bart" == submap.name
        45 == submap.age
    }

    void testIsEmpty() {
        expect:
        def map = serializePipe( new MetaMap(testMap()) )
        !map.isEmpty()

        def emptyMap = [:]
        emptyMap.isEmpty()
        def mapEmpty = new MetaMap(emptyMap)
        mapEmpty.isEmpty()
    }

    void testContainsKey() {
        expect:
        def map = serializePipe( new MetaMap(testMap()) )
        map.containsKey("name")
        map.containsKey("age")
        !map.containsKey("fo")
    }

    void testContainsValue() {
        when:
        def map = serializePipe( new MetaMap([name:"Homer", age:45]) )

        then:
        map == [name:"Homer", age:45]
        map.containsValue("Homer")
        map.containsValue(45)
        !map.containsValue("fo")
    }

    void testGet() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )

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
        def map = serializePipe(  new MetaMap(testMap()) )

        then:
        map.info instanceof MetaMap

    }

    void "put test"() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )
        def old = map.put("name", "lisa")

        then:
        "Bart" == old
        "lisa" == map.name
    }

    void testKeySet() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )
        def keys = map.keySet()

        then:
        keys.size() == 4
        keys.contains("name")
        keys.contains("age")
    }

    void testValues() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )
        def values = map.values()

        then:
        values.contains("Bart")
        values.contains(45)
    }

    void "test entrySet"() {
        when:
        def map = serializePipe( new MetaMap(testMap()) )
        def entset = map.entrySet()

        then:
        entset.size() == 4
        for(entry in map.entrySet()) {
            map.getIncludes().contains(entry.key)
        }
    }

    void "smoke test serialize Gadget"() {
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
        when:
        def includes = ['id', 'name', 'localDate', 'thing.name']
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)

        Gadget gadget = new Gadget(id:1, name:"test", localDate: LocalDate.now(), thing:new Thing(name:"test"))
        MetaMap mmap1 = new MetaMap(gadget, ment)
        MetaMap metamap = serializePipe( mmap1)

        then:
        noExceptionThrown()
        metamap == mmap1
        //XXX Can;t set the overriden equals, wont call it
        metamap.equals((Object)mmap1)
        //metamap.getEntityClass() == mmap1.getEntityClass()

    }

    void 'pogo test default get includes'() {

        when:
        def mmap = new MetaMap(pogoBean())
        def map = serializePipe( mmap )

        def includes = map.getIncludes()

        then:
        5 == map.size()
        5 == includes.size()
        ['name', 'age', 'other', 'info', 'nested'].containsAll(includes)
    }

    void "pogo testIsEmpty"() {
        expect:
        def map = serializePipe( new MetaMap(new PogoBean()) )
        !map.isEmpty()
    }

    void "pogo testContainsKey"() {
        expect:
        def map = serializePipe( new MetaMap(new PogoBean()) )
        map.containsKey("name")
        map.containsKey("age")
        !map.containsKey("fo")
    }

    void "pogo testContainsValue"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )

        then:
        map.containsValue("Bart")
        map.containsValue(45)
        !map.containsValue("fo")
    }

    void "pogo testGet"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )

        then:
        "Bart" == map.get("name")
        "Bart" == map.name
        "Bart" == map['name']

        45 == map.get("age")
        45 == map.age
        45 == map['age']

        map.foo == null
        map['foo'] == null
        map.get('foo') == null
    }

    void "pogo put test"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )
        def old = map.put("name", "lisa")

        then:
        "Bart" == old
        "lisa" == map.name
    }

    void "pogo testKeySet"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )
        def keys = map.keySet()

        then:
        keys.size() == 5
        keys.contains("name")
        keys.contains("age")
    }

    void "pogo testValues"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )
        def values = map.values()

        then:
        values.contains("Bart")
        values.contains(45)
    }

    void "pogo test entrySet"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )
        def entset = map.entrySet()

        then:
        entset.size() == 5
        for(entry in map.entrySet()) {
            map.getIncludes().contains(entry.key)
        }

    }

    void "pogo test nested pogo"() {
        when:
        def map = serializePipe( new MetaMap(pogoBean()) )

        then:
        map.info instanceof MetaMap
        // NOTE: pogos dont get wrapped unless they are refed in includes
        map.nested instanceof NestedBean
    }
}
