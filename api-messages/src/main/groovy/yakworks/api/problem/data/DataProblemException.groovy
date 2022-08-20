/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

import yakworks.api.problem.ThrowableProblem

/**
 * generic problem
 */
@SuppressWarnings('ConfusingClassNamedException')
@CompileStatic
class DataProblemException extends ThrowableProblem {

    DataProblemException() {super()}
    DataProblemException(Throwable cause) {super(cause)}

    Object getEntity(){ (problem as DataProblemTrait).getEntity() }
}
