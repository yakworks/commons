/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.beans

import java.lang.reflect.Type

import spock.lang.Specification
import yakworks.commons.testing.pogos.Gadget

class PropertyToolsSpec extends Specification{

    static trait ThingTrait {
        //test overriding getter works
        List<Object> traitList
    }

    static class Thing implements ThingTrait{
        List<Map> mapList
        List<String> stringList

        List<Object> objectList
        List simpleList

        @Override //make it more specific here to make sure the findGenericForCollection picks up right one
        List<String> getTraitList(){
            stringList
        }
    }

    void "smoke test property parsing"() {
        when:
        String undscrore = "me_"
        String otherProperty = "foo_.bar.baz"
        int dotIdx = otherProperty.indexOf(".")
        String rootObj = dotIdx > -1 ? otherProperty.substring(0, dotIdx) : otherProperty
        String restOfPath = dotIdx > -1 ? otherProperty.substring(dotIdx+1) : ""

        then:
        rootObj == "foo_"
        restOfPath == "bar.baz"
        //remove last character
        undscrore[0..-2] == "me"
    }

    void "getPropertyValue for object"() {
        setup:
        def obj = Gadget.create(1)
        obj.nested = Gadget.create(2)

        expect:
        exp == PropertyTools.getProperty(obj, path)

        where:
        exp              | path
        'Gadget1'        | 'name'
        'Gadget2'        | 'nested.name'
        ['rand', 'galt'] | 'nested.stringList'
        'thingy2'        | 'nested.thing.name'
        'foo2'           | 'nested.mapData.foo'
        null             | 'nested.thisDoesNotExist'
    }

    void "getPropertyValue for map"() {
        setup:
        Map obj = [
            name: 'Gadget1',
            nested: [
                name: 'Gadget2',
                stringList: ['rand', 'galt'],
                thing: [name: 'thingy2']
            ]
        ]

        expect:
        exp == PropertyTools.getProperty(obj, path)

        where:
        exp              | path
        'Gadget1'        | 'name'
        'Gadget2'        | 'nested.name'
        ['rand', 'galt'] | 'nested.stringList'
        'thingy2'        | 'nested.thing.name'
        null             | 'nested.thisDoesNotExist'
    }

    void "getProperty with type"() {
        setup:
        def obj = Gadget.create(1)
        BigDecimal val = PropertyTools.getProperty(obj, 'bigDecimal', BigDecimal)

        expect:
        val == 100.99

    }

    void "getProperty with default"() {
        setup:
        def obj = Gadget.create(1)
        BigDecimal val = PropertyTools.getProperty(obj, 'thisPropNoExist', BigDecimal, 9.0)

        expect:
        val == 9.0

    }

    void "findGenericForCollection"(String prop, String genericClass) {
        expect:
        genericClass == PropertyTools.findGenericForCollection(Thing, prop)

        where:
        prop         | genericClass
        'mapList'    | 'java.util.Map'
        'stringList' | 'java.lang.String'
        'objectList' | 'java.lang.Object'
        'simpleList' | 'java.lang.Object'
        'traitList'  | 'java.lang.String'
    }

    void "findGenericTypeForCollection"(String prop, Type genericClass) {
        expect:
        genericClass == PropertyTools.findGenericTypeForCollection(Thing, prop)

        where:
        prop         | genericClass
        'mapList'    | java.util.Map
        'stringList' | java.lang.String
        'objectList' | java.lang.Object
        'simpleList' | java.lang.Object
        'traitList'  | java.lang.String
    }

    void "set setValue with path"() {
        when:
        def user = new BeanToolsSpec.AdminUser()
        user.thing = new BeanToolsSpec.AdminUser.Thing()
        PropertyTools.setValue(user, 'thing.name', "foo")
        //PropertyTools.setFieldValue(user, 'thing.name', "foo")

        then:
        user.thing.name == "foo"
    }

}
