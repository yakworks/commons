/*
* Copyright 2019 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.api.problem.ThrowableProblem

/**
 * fillInStackTrace is overriden to show nothing
 * so it will be faster and consume less memory when thrown.
 *
 * @author Joshua Burnett (@basejump)
 * @since 6.1
 */
@CompileStatic
class NotFoundProblem implements DataProblemTrait<NotFoundProblem>  {

    String defaultCode = 'error.notFound'
    ApiStatus status = HttpStatus.NOT_FOUND

    // the look up key, mostly will be the id, but could be code or map with sourceId combos
    Serializable key

    // the name of the entity that was being looked up
    String name

    NotFoundProblem name(String nm){
        this.name = nm
        args.putIfAbsent('name', nm)
        return this
    }

    NotFoundProblem lookupKey(Serializable k){
        this.key = (k instanceof Map ? k : [id: k]) as Serializable
        args.putIfAbsent('key', key)
        return this
    }

    static NotFoundProblem of(Serializable key, String entityName) {
        def p = new NotFoundProblem().lookupKey(key).name(entityName)
        p.detail("$entityName lookup failed using key ${p.key}")
    }


    @Override
    ThrowableProblem toException(){
        return new NotFoundProblem.Exception().problem(this)
    }

    static class Exception extends DataProblemException {

        //Override it for performance improvement, because filling in the stack trace is quit expensive
        @Override
        synchronized Throwable fillInStackTrace() { return this }
    }
}
