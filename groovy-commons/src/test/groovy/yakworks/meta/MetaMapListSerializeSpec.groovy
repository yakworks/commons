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

class MetaMapListSerializeSpec extends Specification {

    Map testMap(String name = 'Bart', Integer age = 45){
        return [name: name, age:age, other:"stuff", info: [ phone: "1234", email: "jo@jo.com" ]]
    }

    void "smoke test serialize Gadget list"() {
        when:
        List list = []
        (1L..100L).each { Long n ->
            Gadget gadget = new Gadget(id:n, name:"test${n}", localDate: LocalDate.now(), thing:new Thing(name:"test"))
            list.add(gadget)
        }
        def includes = ['id', 'name', 'localDate', 'thing.name']
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)
        def mlist = new MetaMapList(list, ment)

        then:
        mlist.totalCount == 100

        when:
        def serialList = SerializationUtils.serialize(mlist)
        assert serialList
        MetaMapList deserialList = SerializationUtils.deserialize(serialList) as MetaMapList
        assert deserialList

        then:
        deserialList.totalCount == 100
        deserialList == mlist
        //order maintained
        for (int i = 0; i < deserialList.size(); i++) {
            assert deserialList[i] == mlist[i]
        }
    }

    void "smoke test serialize Map list"() {
        when:
        List list = []
        (0..99).each { int n ->
            list.add(testMap("test-${n}", n))
        }
        //def includes = ['name', 'age', 'info.phone']
        //MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)
        def mlist = new MetaMapList(list)

        then:
        mlist.totalCount == 100
        mlist[0] == testMap("test-${0}", 0)

        when:
        def serialList = SerializationUtils.serialize(mlist)
        assert serialList
        MetaMapList deserialList = SerializationUtils.deserialize(serialList) as MetaMapList
        assert deserialList

        then:
        deserialList.totalCount == 100
        //make sure they didn't get nulled out
        deserialList[0] == testMap("test-${0}", 0)
        deserialList == mlist
        //order maintained
        for (int i = 0; i < deserialList.size(); i++) {
            assert deserialList[i] == mlist[i]
            assert deserialList[i].name == mlist[i].name
            assert deserialList[i].age == i
        }
    }

}
