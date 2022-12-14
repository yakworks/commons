/*
* Copyright 2022 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.commons.model

import groovy.transform.CompileStatic

/**
 * Simple Paged List that delegates to a list and has the totalCount.
 * Example would be for results from server where  this list hold 10 results with totalCount = 100
 * Intentionally simple with no tracking for what page its on etc..
 */
@CompileStatic
class SimplePagedList<E> implements TotalCount {
    @Delegate List<E> results

    SimplePagedList(List<E> results, int tot) {
        this.results = results
        setTotalCount(tot)
    }
}
