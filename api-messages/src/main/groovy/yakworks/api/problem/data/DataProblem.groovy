/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

/**
 * generic problem
 */
@CompileStatic
class DataProblem implements DataProblemTrait<DataProblem> {
    String defaultCode = 'error.data.problem'

    /**
     * helper for legacy to throw a DataProblemException with a
     * Title will blank so it can come from the code on render and detail will have the custom message
     */
    static DataProblemException ex(String detailMessage){
        def dpe = new DataProblem()
        dpe.detail = detailMessage
        return (DataProblemException) dpe.toException()
    }

}
