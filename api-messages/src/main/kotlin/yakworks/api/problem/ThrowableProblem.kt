/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import yakworks.api.problem.exception.NestedExceptionUtils
import yakworks.api.problem.exception.NestedRuntimeException

/**
 * wraps a problem as a RuntimeException
 */
open class ThrowableProblem : NestedRuntimeException, ProblemDecorator<ThrowableProblem> {

    // generics on exceptions are too restrictive in kotlin and java so we can't use the delegate
    // features in kotlin and we manually implement whats needed.
    override lateinit var problem: GenericProblem<*>

    constructor() : super()
    constructor(problem: GenericProblem<*>) : super(){ this.problem = problem }
    //constructor(message: String, ex: Exception): super(message, ex)
    //constructor(message: String): super(message)
    constructor(ex: Throwable): super(ex)

    //constructor(
    //    message: String?, cause: Throwable?,
    //    enableSuppression: Boolean,
    //    writableStackTrace: Boolean
    //): super(message, cause, enableSuppression, writableStackTrace){}

    fun problem(v: GenericProblem<*>): ThrowableProblem = apply { problem = v }

    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    override val message: String
        get() = ProblemUtils.buildMessage(problem)

    override fun toString(): String {
        return ProblemUtils.problemToString(problem)
    }

    /**
     * uses the cause in the exception
     */
    override val cause: Throwable?
        get() = super<NestedRuntimeException>.cause


    companion object {

        @JvmStatic
        fun of(problemCause: Throwable): ThrowableProblem {
            val dap = ThrowableProblem(problemCause).detailFromCause()
            return dap //.detail(NestedExceptionUtils.getMostSpecificCause(problemCause)?.message)
        }
    }
}
