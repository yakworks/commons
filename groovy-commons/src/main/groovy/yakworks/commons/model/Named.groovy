/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.model

import groovy.transform.CompileStatic

/**
 * A marker for an entity that is has a name.
 */
@CompileStatic
trait Named {

    String name

}
