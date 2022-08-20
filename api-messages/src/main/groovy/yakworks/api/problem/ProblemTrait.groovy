/*
* Copyright 2021 original authors
* SPDX-License-Identifier: Apache-2.0
*/
package yakworks.api.problem

import java.lang.reflect.Constructor

import groovy.transform.CompileStatic

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.api.ResultTrait
import yakworks.message.MsgKey

/**
 * Trait implementation for the Problem that has setters and builders
 *
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
@CompileStatic
trait ProblemTrait<E extends GenericProblem<E>> extends ResultTrait<E> implements GenericProblem<E> {
    // result overrides, always false
    Boolean getOk(){ false } //always false
    //status default to 400
    ApiStatus status = HttpStatus.BAD_REQUEST

    // Problem impls
    URI type //= Problem.DEFAULT_TYPE
    //the extra detail for this message
    String detail

    //if there is a cause we want to retian when we convert to exception
    Throwable problemCause

    // URI instance
    List<Violation> violations = [] as List<Violation> //Collections.emptyList();

    @Override
    String toString() {
        return ProblemUtils.problemToString(this)
    }

    static GenericProblem<ProblemResult> createProblem(){
        throw new UnsupportedOperationException("Use createInstance() when using the trait")
    }

    //static builders
    //overrides the Result/MsgKey builders
    static E create(){
        return (E)(this.getDeclaredConstructor() as Constructor<E>).newInstance()
    }

    static E of(String code){ return (E)create().msg(code) }

    static E of(String code, Object args){ return (E)create().msg(code, args) }

    static E of(MsgKey mkey){ return (E)create().msg(mkey) }

    static E of(Throwable ex ){
        E prob = (E)create().cause(ex)
        return prob.detailFromCause()
    }

    static E ofPayload(Object payload) { create().payload(payload) }

}
