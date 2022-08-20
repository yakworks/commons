/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta

import spock.lang.Specification
import yakworks.commons.testing.pogos.Gadget

class MetaEntitySpec extends Specification {

    Map testMap(){
        return [name:"Bart", age:45, other:"stuff", info: [ phone: "1234", email: "jo@jo.com" ]]
    }

    void "test equals"() {
        when:
        def ment1 = new MetaEntity(Gadget)
        def ment2 = new MetaEntity(Gadget)

        then:
        ment1 == ment2
    }

    void "test toBasicMap"(){
        when:
        //simple
        def includes = ['id', 'name', 'localDate', 'thing.name', ]

        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)
        Map basicMap = ment.toMap()

        then:
        basicMap.size() == 4
        basicMap.keySet() == ['id', 'name', 'localDate', 'thing'] as Set
        basicMap.thing.size() == 1
        basicMap.thing.keySet() == ['name'] as Set
    }

    void "test title"(){
        when:
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, ['id', 'name', 'localDate' ])

        then:
        !ment.metaProps.id.hasTitle()
        ment.metaProps.id.title == 'Id'
        ment.metaProps.id.hasTitle()

        ment.metaProps.name.title == 'Name'
        ment.metaProps.localDate.title == 'Local Date'
    }

    void "test flatten"(){
        when:
        //simple
        def includes = ['id', 'name', 'thing.name']
        def expectedIncludes = includes
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)
        def flatMap = ment.flatten()

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
        MetaEntity ment = BasicMetaEntityBuilder.build(Gadget, includes)
        Set props = ment.flattenProps()

        then:
        //in this case it should equal the passes in includes
        props == expectedIncludes as Set
    }

}
