/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.meta


import spock.lang.Specification
import yakworks.commons.testing.pogos.Thing

class MetaUtilsSpec extends Specification {

    void 'groovy default getProperties'() {

        when:
        def thing = Thing.of(1, "thing")
        def props = thing.properties

        then:
        //groovy's default returns class and staticProp.
        props
        //has the static in it.
        props.someStatic
    }

    void "test getMetaProperties"() {
        when:
        List<MetaProperty> metaProps = MetaUtils.getMetaProperties(Thing)

        then:
        metaProps.size() == 3
        metaProps.find { it.name == 'id' }
        metaProps.find { it.name == 'name' }
        metaProps.find { it.name == 'localDate' }

    }

    void "test getProperties"() {
        when:
        Map thing = MetaUtils.getProperties(Thing.of(1, "thing"))

        then:
        thing == [id:1, name: 'thing', localDate:null]
    }

}
