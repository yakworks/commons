/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem.data

import groovy.transform.CompileStatic

import yakworks.api.ResultSupport
import yakworks.api.problem.ProblemTrait
import yakworks.api.problem.ThrowableProblem
import yakworks.api.problem.exception.NestedExceptionUtils

/**
 * Trait implementation for the Problem that has setters and builders
 * The payload is always the entity intance here.
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
@CompileStatic
trait DataProblemTrait<E extends DataProblemTrait<E>> extends ProblemTrait<E> {

    /**
     * convienience alias for payload so its clearer in the code
     * This is the entity that has problems, can be the id or the object
     */
    Object getEntity(){ return getPayload() }

    /**
     * builder method for entity that will add common args
     * such as name, id and stamp to the MsgKey
     */
    E entity(Object v) {
        if(v != null) {
            this.payload = v
            ResultSupport.addCommonArgs(args.asMap(), v)
        }
        return (E)this;
    }

    // overrides payload to call entity
    @Override
    E payload(Object v) {
        entity(v)
    }

    @Override
    ThrowableProblem toException(){
        return getCause() ? new DataProblemException(getCause()).problem(this) : new DataProblemException().problem(this)
    }

    // static E of(final Throwable problemCause) {
    //     def dap = this.newInstance().cause(problemCause)
    //     (E) dap.detail(NestedExceptionUtils.getMostSpecificCause(problemCause).message)
    // }

}
