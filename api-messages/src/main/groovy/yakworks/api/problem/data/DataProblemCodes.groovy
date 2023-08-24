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

    //generic data problem
    DataProblem('error.data.problem'),
    NotFound('error.notFound', 'Lookup failed'),
    OptimisticLocking('error.data.optimisticLocking', 'persist failed as version changed since last retrieval'),
    ReferenceKey('error.data.reference', 'Reference or foriegn key error and this cant be updated or deleted'),
    UniqueConstraint('error.data.uniqueConstraintViolation', "Primary or unique key violation"),
    Empty('error.data.empty', 'Data cannot be empty'), //empty or null data where its required
    EmptyPayload('error.data.emptyPayload', 'Payload cannot be empty'), //specific for empty payloads

    final String code
    //May be replaced by message.props code message
    final String title = "Data Problem"
    //default detail message
    final String detail

    DataProblemCodes(String code) {
        this.code = code
    }

    DataProblemCodes(String code, String detail ) {
        this.code = code
        this.detail = detail
    }

    DataProblem get(){
        new DataProblem().msg(code).title(title).detail(detail)
    }

    DataProblem withArgs(Map args){
        def dp = new DataProblem().msg(code, args).title(title).detail(detail)
        return dp
    }

    DataProblem of(Throwable cause){
        return DataProblem.of(cause).msg(code)
    }

}
