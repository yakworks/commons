/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem

import groovy.transform.CompileStatic

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus

/**
 * Concrete problem for unexpected exceptions or untrapped that can be called as a flow through
 * These can get special andling and alerts in logging as , well , they should not have happened
 * and deserve attention as it means code is fubarred.
 */
@CompileStatic
class UnexpectedProblem implements ProblemTrait<UnexpectedProblem> {
    public static String DEFAULT_CODE = 'error.unexpected'
    String defaultCode = DEFAULT_CODE
    ApiStatus status = HttpStatus.INTERNAL_SERVER_ERROR

    static ThrowableProblem ex(String message){
        return Problem.of(DEFAULT_CODE).detail(message).toException() as ThrowableProblem
    }
}
