/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import spock.lang.Specification
import yakworks.commons.testing.pogos.Gadget

class MetaMapIncludesSpec extends Specification {

    Map testMap(){
        return [name:"Bart", age:45, other:"stuff", info: [ phone: "1234", email: "jo@jo.com" ]]
    }

    void "test equals"() {
        when:
        def mmi1 = new MetaMapIncludes(Gadget)
        def mmi2 = new MetaMapIncludes(Gadget)

        then:
        mmi1 == mmi2
    }

    void "test toBasicMap"(){
        when:
        //simple
        def includes = ['id', 'name', 'thing.name']

        MetaMapIncludes mmi = MetaMapIncludesBuilder.build(Gadget, includes)
        Map basicMap = mmi.toMap()

        then:
        basicMap.size() == 3
        basicMap.keySet() == ['id', 'name', 'thing'] as Set
        basicMap.thing.size() == 1
        basicMap.thing.keySet() == ['name'] as Set
    }

    void "test flatten"(){
        when:
        //simple
        def includes = ['id', 'name', 'thing.name']
        def expectedIncludes = includes
        MetaMapIncludes mmi = MetaMapIncludesBuilder.build(Gadget, includes)
        def flatMap = mmi.flatten()

        then:
        //in this case it should equal the passes in includes
        flatMap.keySet() == expectedIncludes as Set
        flatMap['id'] instanceof MetaProp
        flatMap['id'].classType == Long
    }

    void "test flattenProps"(){
        when:
        //simple
        def includes = ['id', 'name', 'thing.name']
        def expectedIncludes = includes
        MetaMapIncludes mmi = MetaMapIncludesBuilder.build(Gadget, includes)
        Set props = mmi.flattenProps()

        then:
        //in this case it should equal the passes in includes
        props == expectedIncludes as Set
    }

}
