/*
 * Copyright 2002-2017 the original author or authors.
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
/*
 * Copyright 2002-2017 the original author or authors.
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

/**
 * From SPRING
 * Helper class for implementing exception classes which are capable of
 * holding nested exceptions. Necessary because we can't share a base
 * class among different exception types.
 *
 *
 * Mainly for use within the framework.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
object NestedExceptionUtils {
    /**
     * Build a message for the given base message and root cause.
     * @param message the base message
     * @param cause the root cause
     * @return the full exception message
     */
    @JvmStatic
    fun buildMessage(message: String?, cause: Throwable?): String? {
        if (cause == null) {
            return message
        }
        val sb = StringBuilder(64)
        if (message != null) {
            sb.append(message).append("; ")
        }
        sb.append("nested exception is ").append(cause)
        return sb.toString()
    }

    /**
     * Retrieve the innermost cause of the given exception, if any.
     * @param original the original exception to introspect
     * @return the innermost exception, or `null` if none
     * @since 4.3.9
     */
    @JvmStatic
    fun getRootCause(original: Throwable?): Throwable? {
        if (original == null) {
            return null
        }
        var rootCause: Throwable? = null
        var cause = original.cause
        while (cause != null && cause !== rootCause) {
            rootCause = cause
            cause = cause.cause
        }
        return rootCause
    }

    /**
     * Retrieve the most specific cause of the given exception, that is,
     * either the innermost cause (root cause) or the exception itself.
     *
     * Differs from [.getRootCause] in that it falls back
     * to the original exception if there is no root cause.
     * @param original the original exception to introspect
     * @return the most specific cause (never `null`)
     * @since 4.3.9
     */
    @JvmStatic
    fun getMostSpecificCause(original: Throwable): Throwable {
        val rootCause = getRootCause(original)
        return rootCause ?: original
    }
}
