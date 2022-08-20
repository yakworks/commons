/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.testing.pogos

import groovy.transform.CompileStatic

@CompileStatic
class Thing {
    Long id
    // address fields
    String name

    static String someStatic = "foo"

    static Thing of(Long id, String name){
        return new Thing(id: id, name: name)
    }
}
