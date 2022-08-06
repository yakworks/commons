/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import jakarta.annotation.Nullable

/**
 * wraps a problem as a RuntimeException
 */
open class ThrowableProblem : RuntimeException, ProblemDecorator<ThrowableProblem> {

    //generics on exceptions are too restrictive in kotlin and java so we can't use the delegate
    // features in kotlin and we manually implement whats needed.
    override lateinit var problem: GenericProblem<*>

    constructor() : super()
    constructor(problem: GenericProblem<*>) : super(){ this.problem = problem }
    //constructor(message: String, ex: Exception): super(message, ex)
    //constructor(message: String): super(message)
    constructor(ex: Throwable): super(ex)

    constructor(
        message: String?, cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ): super(message, cause, enableSuppression, writableStackTrace){}

    fun problem(v: GenericProblem<*>): ThrowableProblem = apply { problem = v }

    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    override val message: String
        get() = ProblemUtils2.buildMessage(problem)

    /**
     * Retrieve the most specific cause of this exception, that is,
     * either the innermost cause (root cause) or this exception itself.
     * <p>Differs from {@link #getRootCause()} in that it falls back
     * to the present exception if there is no root cause.
     * @return the most specific cause (never {@code null})
     */
    val rootCause: Throwable?
        get() = ProblemUtils2.getRootCause(this)


    override fun toString(): String {
        return ProblemUtils2.problemToString(problem)
    }

    /**
     * uses the cause in the exception
     */
    override val cause: Throwable?
        get() = super<RuntimeException>.cause

    /**
     * Check whether this exception contains an exception of the given type:
     * either it is of the given class itself or it contains a nested cause
     * of the given type.
     * @param exType the exception type to look for
     * @return whether there is a nested exception of the specified type
     */
    open fun contains(@Nullable exType: Class<*>?): Boolean {
        if (exType == null) {
            return false
        }
        if (exType.isInstance(this)) {
            return true
        }
        var cause: Throwable? = this.cause
        if (cause === this) {
            return false
        }
        return if (cause is ThrowableProblem) {
            cause.contains(exType)
        } else {
            while (cause != null) {
                if (exType.isInstance(cause)) {
                    return true
                }
                if (cause.cause === cause) {
                    break
                }
                cause = cause.cause
            }
            false
        }
    }

    companion object {

        @JvmStatic
        fun ofCause(problemCause: Throwable): ThrowableProblem {
            val dap = ThrowableProblem(problemCause)
            return dap.detail(ProblemUtils2.getRootCause(problemCause)?.message)
        }
    }
}
