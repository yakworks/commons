/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.testing

import groovy.transform.CompileStatic

import spock.lang.Specification
import yakworks.commons.beans.BeanTools

class ThingSpec extends Specification {

    void "test thing"(){
        when:
        def thing = new Thing()
        //assert thing.properties == [id: 1, name: 'one']
        def thing2 = Thing.of(1, 'one')
        def thing3 = new Thing(id: 2, name: 'two')
        def thing4 = new Thing()
        BeanTools.copy(thing4, thing3)

        then:
        thing
        thing2.id == 1
        thing2.name == 'one'
        thing3.id == 2
        thing3.name == 'two'
        thing4.id == 2
        thing4.name == 'two'
    }

    @CompileStatic
    Thing createThing(){
        return new Thing(id: 1, name: 'one')
    }

}
