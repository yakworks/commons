package yakworks.commons.lang

import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * similiar to org.apache.commons.lang3.Validate but throws IllegalArgumentException instead of
 * a NullPointer
 *
 * @author Joshua Burnett (@basejump)
 */
object Validate {
    private const val DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false"

    /**
     * Validate that the specified argument is no `null`
     *
     * @param obj     the object to validate
     * @param message the message to use to populate default message,
     *                if the string is wrapped in [ ] then it builds the default
     *                message with the descriptor
     * @return the validated obj (never `null` method for chaining)
     * @throws IllegalArgumentException
     */
    @JvmStatic
    fun <T> notNull(obj: T?, message: String): T {
        var message = message
        if (obj == null) {
            if (message.startsWith("[")) message = "$message must not be null"
            throw IllegalArgumentException(message)
        }
        return obj
    }

    /**
     * Validate that the specified argument is no `null`
     *
     * @param obj     the object to validate
     * @return the validated obj (never `null` method for chaining)
     * @throws IllegalArgumentException
     */
    @JvmStatic
    fun <T> notNull(obj: T): T {
        return notNull<T>(obj, "The validated object must not be null")
    }

    /**
     * Validate that the specified argument is no `null`
     *
     *
     * For performance reasons, the Object... values is passed as a separate parameter and
     * appended to the exception message only in the case of an error.
     *
     * @param obj the object to check
     * @param message    the [String.format] exception message if invalid, not null
     * @param msgArgs    the optional message args for the formatted exception message, null array not recommended
     * @throws IllegalArgumentException if expression is `false`
     */
    @JvmStatic
    fun <T> notNull(obj: T?, message: String?, vararg msgArgs: Any?): T {
        requireNotNull(obj) { String.format(message!!, *msgArgs) }
        return obj
    }

    /**
     * Validate that the specified argument is
     * neither `null` nor a length of zero (no characters) nor an empty collection
     * otherwise throwing an IllegalArgumentException with the specified message.
     *
     * @param obj           the object to validate
     * @param objDescriptor the descriptor to use for default message
     * @return the validated obj (never `null` method for chaining)
     * @throws IllegalArgumentException
     */
    @JvmStatic
    fun <T> notEmpty(obj: T, objDescriptor: String): T {
        require(DefaultGroovyMethods.asBoolean(obj)) { "The $objDescriptor must not be blank or empty" }
        return obj
    }

    /**
     * Validate that the specified argument is
     * neither `null` nor a length of zero (no characters) nor an empty collection
     * otherwise throwing an IllegalArgumentException with the specified message.
     *
     * @param obj           the object to validate
     * @return the validated obj (never `null` method for chaining)
     * @throws IllegalArgumentException
     */
    @JvmStatic
    fun <T> notEmpty(obj: T): T {
        return notEmpty(obj, "validated object")
    }

    /**
     *
     * Validate that the argument condition is `true`; otherwise
     * throwing an exception with the specified message. This method is useful when
     * validating according to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.
     *
     * <pre>
     * Validate.isTrue(i &gt;= min &amp;&amp; i &lt;= max, "The value must be between &#37;d and &#37;d", min, max);
     * Validate.isTrue(myObject.isOk(), "The object is not okay");</pre>
     *
     *
     * For performance reasons, the Object... values is passed as a separate parameter and
     * appended to the exception message only in the case of an error.
     *
     * @param expression the boolean expression to check
     * @param message    the [String.format] exception message if invalid, not null
     * @param values     the optional values for the formatted exception message, null array not recommended
     * @throws IllegalArgumentException if expression is `false`
     */
    @JvmStatic
    fun isTrue(expression: Boolean, message: String?, vararg values: Any?) {
        require(expression) { String.format(message!!, *values) }
    }

    /**
     *
     * Validate that the argument condition is `true`; otherwise
     * throwing an exception. This method is useful when validating according
     * to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.
     *
     * <pre>
     * Validate.isTrue(i &gt; 0);
     * Validate.isTrue(myObject.isOk());</pre>
     *
     *
     * The message of the exception is &quot;The validated expression is
     * false&quot;.
     *
     * @param expression the boolean expression to check
     * @throws IllegalArgumentException if expression is `false`
     * @see .isTrue
     */
    @JvmStatic
    fun isTrue(expression: Boolean) {
        require(expression) { DEFAULT_IS_TRUE_EX_MESSAGE }
    }

}
