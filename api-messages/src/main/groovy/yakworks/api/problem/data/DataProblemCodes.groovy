/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

/**
 * Enum helper for codes
 */
@CompileStatic
enum DataProblemCodes {

    NotFound('error.notFound'),
    OptimisticLocking('error.data.optimisticLocking'),
    ReferenceKey('error.data.reference'),
    UniqueConstraint('error.data.uniqueConstraintViolation')

    final String code

    DataProblemCodes(String code) {
        this.code = code
    }

    DataProblem get(){
        new DataProblem().msg(code)
    }

    DataProblem withArgs(Map args){
        new DataProblem().msg(code, args)
    }

    DataProblem of(Throwable cause){
        return DataProblem.of(cause).msg(code)
    }

}
