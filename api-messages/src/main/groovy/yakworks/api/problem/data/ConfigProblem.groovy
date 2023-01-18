/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.message.Msg

/**
 * Concrete problem for configuration or setup errors or inconsistencies
 */
@CompileStatic
class ConfigProblem implements DataProblemTrait<ConfigProblem> {
    public static String DEFAULT_CODE = 'error.configuration.problem'
    String defaultCode = DEFAULT_CODE
    ApiStatus status = HttpStatus.INTERNAL_SERVER_ERROR

    /**
     * helper for legacy to throw a DataProblemException with a message
     */
    static DataProblemException ex(String message){
        def cp = new ConfigProblem()
        cp.msg = Msg.key(DEFAULT_CODE)
        return (DataProblemException) cp.title(message).toException()
    }

}
