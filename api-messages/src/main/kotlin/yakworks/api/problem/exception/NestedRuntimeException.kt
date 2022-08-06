/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yakworks.api.problem.exception

import yakworks.api.problem.exception.NestedExceptionUtils.buildMessage
import yakworks.api.problem.exception.NestedExceptionUtils.getRootCause

/**
 * FROM SPRING
 * Handy class for wrapping runtime `Exceptions` with a root cause.
 *
 *
 * This class is `abstract` to force the programmer to extend
 * the class. `getMessage` will include nested exception
 * information; `printStackTrace` and other like methods will
 * delegate to the wrapped exception, if any.
 *
 *
 * The similarity between this class and the [NestedCheckedException]
 * class is unavoidable, as Java forces these two classes to have different
 * superclasses (ah, the inflexibility of concrete inheritance!).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
abstract class NestedRuntimeException : RuntimeException {
    constructor() : super() {}
    /**
     * Construct a `NestedRuntimeException` with the specified detail message.
     * @param msg the detail message
     */
    constructor(msg: String?) : super(msg) {}

    /**
     * Construct a `NestedRuntimeException` with the specified detail message
     * and nested exception.
     * @param msg the detail message
     * @param cause the nested exception
     */
    constructor(msg: String?, cause: Throwable?) : super(msg, cause) {}
    constructor(cause: Throwable) : super(cause) {}
    constructor(
        message: String?, cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ): super(message, cause, enableSuppression, writableStackTrace){}

    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    override val message: String
        get() = buildMessage(super.message, cause)!!

    /**
     * Retrieve the innermost cause of this exception, if any.
     * @return the innermost exception, or `null` if none
     * @since 2.0
     */
    val rootCause: Throwable?
        get() = getRootCause(this)

    /**
     * Retrieve the most specific cause of this exception, that is,
     * either the innermost cause (root cause) or this exception itself.
     *
     * Differs from [.getRootCause] in that it falls back
     * to the present exception if there is no root cause.
     * @return the most specific cause (never `null`)
     * @since 2.0.3
     */
    val mostSpecificCause: Throwable
        get() {
            val rootCause = rootCause
            return rootCause ?: this
        }

    /**
     * Check whether this exception contains an exception of the given type:
     * either it is of the given class itself or it contains a nested cause
     * of the given type.
     * @param exType the exception type to look for
     * @return whether there is a nested exception of the specified type
     */
    operator fun contains(exType: Class<*>?): Boolean {
        if (exType == null) {
            return false
        }
        if (exType.isInstance(this)) {
            return true
        }
        var cause = cause
        if (cause === this) {
            return false
        }
        return if (cause is NestedRuntimeException) {
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
}
