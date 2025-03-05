/*
* Copyright 2020 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.model

import groovy.transform.CompileStatic

/**
 * Indicates class has hydrate method
 *
 * @author Joshua Burnett (@basejump)
 * @since 3.16
 */
@CompileStatic
interface Hydratable {
    public Object hydrate()
}
