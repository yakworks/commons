/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.testing.pogos

import java.time.LocalDate

import groovy.transform.CompileStatic

@CompileStatic
class Thing implements Serializable {
    Long id
    // address fields
    String name

    LocalDate localDate

    static String someStatic = "foo"

    static Thing of(Long id, String name){
        return new Thing(id: id, name: name)
    }
}
