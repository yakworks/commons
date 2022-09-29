/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.commons.lang

import java.lang.reflect.Type

import spock.lang.Specification
import yakworks.commons.lang.PropertyTools
import yakworks.commons.testing.pogos.Gadget

class PogoSpec extends Specification{

    static class User {
        String name = 'Galt'
        String city = 'Gulch'
        Integer age = 22
        String onlyUser = "foo"
    }

    static class AdminUser {
        String name
        String city
        Integer age
        String onlyAdmin = "foo"
    }


    void "simple setProps"() {
        when:
        def au = Pogo.setProps(new AdminUser(), new User())

        then:
        au.name == 'Galt'
        au.city == 'Gulch'
        au.age == 22
    }

}
