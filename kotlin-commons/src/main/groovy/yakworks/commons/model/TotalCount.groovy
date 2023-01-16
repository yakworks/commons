/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.model

import groovy.transform.CompileStatic

/**
 * A marker for an entity that is has a total count field.
 * Useful for PagedList
 */
@CompileStatic
trait TotalCount {
    int totalCount
}
