/*
 * Copyright 2002-2021 the original author or authors.
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
package yakworks.util

import jakarta.annotation.Nullable
import java.util.*

/**
 * Miscellaneous object utility methods.
 *
 *
 * Mainly for internal use within the framework.
 *
 *
 * Thanks to Alex Ruiz for contributing several enhancements to this class!
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Chris Beams
 * @author Sam Brannen
 * @since 19.03.2004
 * @see ClassUtils
 *
 * @see CollectionUtils
 *
 * @see StringUtils
 */
object ObjectUtils {
    private const val INITIAL_HASH = 7
    private const val MULTIPLIER = 31
    private const val EMPTY_STRING = ""
    private const val NULL_STRING = "null"
    private const val ARRAY_START = "{"
    private const val ARRAY_END = "}"
    private const val EMPTY_ARRAY = ARRAY_START + ARRAY_END
    private const val ARRAY_ELEMENT_SEPARATOR = ", "
    private val EMPTY_OBJECT_ARRAY = arrayOfNulls<Any>(0)

    /**
     * Return whether the given throwable is a checked exception:
     * that is, neither a RuntimeException nor an Error.
     * @param ex the throwable to check
     * @return whether the throwable is a checked exception
     * @see java.lang.Exception
     *
     * @see java.lang.RuntimeException
     *
     * @see java.lang.Error
     */
    @JvmStatic
    fun isCheckedException(ex: Throwable?): Boolean {
        return !(ex is RuntimeException || ex is Error)
    }

    /**
     * Check whether the given exception is compatible with the specified
     * exception types, as declared in a throws clause.
     * @param ex the exception to check
     * @param declaredExceptions the exception types declared in the throws clause
     * @return whether the given exception is compatible
     */
    @JvmStatic
    fun isCompatibleWithThrowsClause(ex: Throwable?, vararg declaredExceptions: Class<*>): Boolean {
        if (!isCheckedException(ex)) {
            return true
        }
        if (declaredExceptions != null) {
            for (declaredException in declaredExceptions) {
                if (declaredException.isInstance(ex)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Determine whether the given object is an array:
     * either an Object array or a primitive array.
     * @param obj the object to check
     */
    fun isArray(obj: Any?): Boolean {
        return obj != null && obj.javaClass.isArray
    }

    /**
     * Determine whether the given array is empty:
     * i.e. `null` or of zero length.
     * @param array the array to check
     * @see .isEmpty
     */
    @JvmStatic
    fun isEmpty(array: Array<Any?>?): Boolean {
        return array == null || array.size == 0
    }

    /**
     * Determine whether the given object is empty.
     *
     * This method supports the following object types.
     *
     *  * `Optional`: considered empty if not [Optional.isPresent]
     *  * `Array`: considered empty if its length is zero
     *  * [CharSequence]: considered empty if its length is zero
     *  * [Collection]: delegates to [Collection.isEmpty]
     *  * [Map]: delegates to [Map.isEmpty]
     *
     *
     * If the given object is non-null and not one of the aforementioned
     * supported types, this method returns `false`.
     * @param obj the object to check
     * @return `true` if the object is `null` or *empty*
     * @since 4.2
     * @see Optional.isPresent
     * @see ObjectUtils.isEmpty
     * @see StringUtils.hasLength
     * @see CollectionUtils.isEmpty
     * @see CollectionUtils.isEmpty
     */
    fun isEmpty(obj: Any?): Boolean {
        if (obj == null) {
            return true
        }
        if (obj is Optional<*>) {
            return !obj.isPresent
        }
        if (obj is CharSequence) {
            return obj.length == 0
        }
        if (obj.javaClass.isArray) {
            return java.lang.reflect.Array.getLength(obj) == 0
        }
        if (obj is Collection<*>) {
            return obj.isEmpty()
        }
        return if (obj is Map<*, *>) {
            obj.isEmpty()
        } else false

        // else
    }

    /**
     * Unwrap the given object which is potentially a [java.util.Optional].
     * @param obj the candidate object
     * @return either the value held within the `Optional`, `null`
     * if the `Optional` is empty, or simply the given object as-is
     * @since 5.0
     */
    @Nullable
    fun unwrapOptional(obj: Any?): Any? {
        if (obj is Optional<*>) {
            val optional = obj
            if (!optional.isPresent) {
                return null
            }
            val result = optional.get()
            Assert.isTrue(result !is Optional<*>, "Multi-level Optional usage not supported")
            return result
        }
        return obj
    }

    /**
     * Check whether the given array contains the given element.
     * @param array the array to check (may be `null`,
     * in which case the return value will always be `false`)
     * @param element the element to check for
     * @return whether the element has been found in the given array
     */
    @JvmStatic
    fun containsElement(array: Array<Any?>?, element: Any?): Boolean {
        if (array == null) {
            return false
        }
        for (arrayEle in array) {
            if (nullSafeEquals(arrayEle, element)) {
                return true
            }
        }
        return false
    }
    /**
     * Check whether the given array of enum constants contains a constant with the given name.
     * @param enumValues the enum values to check, typically obtained via `MyEnum.values()`
     * @param constant the constant name to find (must not be null or empty string)
     * @param caseSensitive whether case is significant in determining a match
     * @return whether the constant has been found in the given array
     */
    /**
     * Check whether the given array of enum constants contains a constant with the given name,
     * ignoring case when determining a match.
     * @param enumValues the enum values to check, typically obtained via `MyEnum.values()`
     * @param constant the constant name to find (must not be null or empty string)
     * @return whether the constant has been found in the given array
     */
    @JvmStatic
    @JvmOverloads
    fun containsConstant(enumValues: Array<Enum<*>>, constant: String, caseSensitive: Boolean = false): Boolean {
        for (candidate in enumValues) {
            if (if (caseSensitive) candidate.toString() == constant else candidate.toString()
                    .equals(constant, ignoreCase = true)
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Case insensitive alternative to [Enum.valueOf].
     * @param <E> the concrete Enum type
     * @param enumValues the array of all Enum constants in question, usually per `Enum.values()`
     * @param constant the constant to get the enum value of
     * @throws IllegalArgumentException if the given constant is not found in the given array
     * of enum values. Use [.containsConstant] as a guard to avoid this exception.
    </E> */
    @JvmStatic
    fun <E : Enum<*>?> caseInsensitiveValueOf(enumValues: Array<E>, constant: String): E {
        for (candidate in enumValues) {
            if (candidate.toString().equals(constant, ignoreCase = true)) {
                return candidate
            }
        }
        throw IllegalArgumentException(
            "Constant [" + constant + "] does not exist in enum type " +
                    enumValues.javaClass.componentType.name
        )
    }

    /**
     * Append the given object to the given array, returning a new array
     * consisting of the input array contents plus the given object.
     * @param array the array to append to (can be `null`)
     * @param obj the object to append
     * @return the new array (of the same component type; never `null`)
     */
    @JvmStatic
    fun <A, O : A?> addObjectToArray(array: Array<A>?, obj: O?): Array<A?> {
        var compType: Class<*>? = Any::class.java
        if (array != null) {
            compType = array.javaClass.componentType
        } else if (obj != null) {
            compType = obj.javaClass
        }
        val newArrLength = if (array != null) array.size + 1 else 1
        val newArr = java.lang.reflect.Array.newInstance(compType, newArrLength) as Array<A?>
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.size)
        }
        newArr[newArr.size - 1] = obj
        return newArr
    }

    /**
     * Convert the given array (which may be a primitive array) to an
     * object array (if necessary of primitive wrapper objects).
     *
     * A `null` source value will be converted to an
     * empty Object array.
     * @param source the (potentially primitive) array
     * @return the corresponding object array (never `null`)
     * @throws IllegalArgumentException if the parameter is not an array
     */
    @JvmStatic
    fun toObjectArray(source: Any?): Array<Any?> {
        if (source is Array<*>) {
            return source as Array<Any?>
        }
        if (source == null) {
            return EMPTY_OBJECT_ARRAY
        }
        require(source.javaClass.isArray) { "Source is not an array: $source" }
        val length = java.lang.reflect.Array.getLength(source)
        if (length == 0) {
            return EMPTY_OBJECT_ARRAY
        }
        val wrapperType: Class<*> = java.lang.reflect.Array.get(source, 0).javaClass
        val newArray = java.lang.reflect.Array.newInstance(wrapperType, length) as Array<Any?>
        for (i in 0 until length) {
            newArray[i] = java.lang.reflect.Array.get(source, i)
        }
        return newArray
    }
    //---------------------------------------------------------------------
    // Convenience methods for content-based equality/hash-code handling
    //---------------------------------------------------------------------
    /**
     * Determine if the given objects are equal, returning `true` if
     * both are `null` or `false` if only one is `null`.
     *
     * Compares arrays with `Arrays.equals`, performing an equality
     * check based on the array elements rather than the array reference.
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see Object.equals
     * @see java.util.Arrays.equals
     */
    @JvmStatic
    fun nullSafeEquals(o1: Any?, o2: Any?): Boolean {
        if (o1 === o2) {
            return true
        }
        if (o1 == null || o2 == null) {
            return false
        }
        if (o1 == o2) {
            return true
        }
        return if (o1.javaClass.isArray && o2.javaClass.isArray) {
            arrayEquals(o1, o2)
        } else false
    }

    /**
     * Compare the given arrays with `Arrays.equals`, performing an equality
     * check based on the array elements rather than the array reference.
     * @param o1 first array to compare
     * @param o2 second array to compare
     * @return whether the given objects are equal
     * @see .nullSafeEquals
     * @see java.util.Arrays.equals
     */
    private fun arrayEquals(o1: Any, o2: Any): Boolean {
        if (o1 is Array<*> && o2 is Array<*>) {
            return Arrays.equals(o1 as Array<Any?>, o2 as Array<Any?>)
        }
        if (o1 is BooleanArray && o2 is BooleanArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is ByteArray && o2 is ByteArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is CharArray && o2 is CharArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is DoubleArray && o2 is DoubleArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is FloatArray && o2 is FloatArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is IntArray && o2 is IntArray) {
            return Arrays.equals(o1, o2)
        }
        if (o1 is LongArray && o2 is LongArray) {
            return Arrays.equals(o1, o2)
        }
        return if (o1 is ShortArray && o2 is ShortArray) {
            Arrays.equals(o1, o2)
        } else false
    }

    /**
     * Return as hash code for the given object; typically the value of
     * `Object#hashCode()`}. If the object is an array,
     * this method will delegate to any of the `nullSafeHashCode`
     * methods for arrays in this class. If the object is `null`,
     * this method returns 0.
     * @see Object.hashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     * @see .nullSafeHashCode
     */
    @JvmStatic
    fun nullSafeHashCode(obj: Any?): Int {
        if (obj == null) {
            return 0
        }
        if (obj.javaClass.isArray) {
            if (obj is Array<*>) {
                return nullSafeHashCode(obj as Array<Any?>?)
            }
            if (obj is BooleanArray) {
                return nullSafeHashCode(obj as BooleanArray?)
            }
            if (obj is ByteArray) {
                return nullSafeHashCode(obj as ByteArray?)
            }
            if (obj is CharArray) {
                return nullSafeHashCode(obj as CharArray?)
            }
            if (obj is DoubleArray) {
                return nullSafeHashCode(obj as DoubleArray?)
            }
            if (obj is FloatArray) {
                return nullSafeHashCode(obj as FloatArray?)
            }
            if (obj is IntArray) {
                return nullSafeHashCode(obj as IntArray?)
            }
            if (obj is LongArray) {
                return nullSafeHashCode(obj as LongArray?)
            }
            if (obj is ShortArray) {
                return nullSafeHashCode(obj as ShortArray?)
            }
        }
        return obj.hashCode()
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: Array<Any?>?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + nullSafeHashCode(element)
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: BooleanArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + java.lang.Boolean.hashCode(element)
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: ByteArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + element
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: CharArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + element.code
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: DoubleArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + java.lang.Double.hashCode(element)
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: FloatArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + java.lang.Float.hashCode(element)
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: IntArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + element
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: LongArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + java.lang.Long.hashCode(element)
        }
        return hash
    }

    /**
     * Return a hash code based on the contents of the specified array.
     * If `array` is `null`, this method returns 0.
     */
    @JvmStatic
    fun nullSafeHashCode(array: ShortArray?): Int {
        if (array == null) {
            return 0
        }
        var hash = INITIAL_HASH
        for (element in array) {
            hash = MULTIPLIER * hash + element
        }
        return hash
    }
    //---------------------------------------------------------------------
    // Convenience methods for toString output
    //---------------------------------------------------------------------
    /**
     * Return a String representation of an object's overall identity.
     * @param obj the object (may be `null`)
     * @return the object's identity as String representation,
     * or an empty String if the object was `null`
     */
    @JvmStatic
    fun identityToString(obj: Any?): String {
        return if (obj == null) {
            EMPTY_STRING
        } else obj.javaClass.name + "@" + getIdentityHexString(obj)
    }

    /**
     * Return a hex String form of an object's identity hash code.
     * @param obj the object
     * @return the object's identity code in hex notation
     */
    @JvmStatic
    fun getIdentityHexString(obj: Any?): String {
        return Integer.toHexString(System.identityHashCode(obj))
    }

    /**
     * Return a content-based String representation if `obj` is
     * not `null`; otherwise returns an empty String.
     *
     * Differs from [.nullSafeToString] in that it returns
     * an empty String rather than "null" for a `null` value.
     * @param obj the object to build a display String for
     * @return a display String representation of `obj`
     * @see .nullSafeToString
     */
    fun getDisplayString(obj: Any?): String {
        return if (obj == null) {
            EMPTY_STRING
        } else nullSafeToString(obj)
    }

    /**
     * Determine the class name for the given object.
     *
     * Returns a `"null"` String if `obj` is `null`.
     * @param obj the object to introspect (may be `null`)
     * @return the corresponding class name
     */
    fun nullSafeClassName(obj: Any?): String {
        return if (obj != null) obj.javaClass.name else NULL_STRING
    }

    /**
     * Return a String representation of the specified Object.
     *
     * Builds a String representation of the contents in case of an array.
     * Returns a `"null"` String if `obj` is `null`.
     * @param obj the object to build a String representation for
     * @return a String representation of `obj`
     */
    @JvmStatic
    fun nullSafeToString(obj: Any?): String {
        if (obj == null) {
            return NULL_STRING
        }
        if (obj is String) {
            return obj
        }
        if (obj is Array<*>) {
            return nullSafeToString(obj as Array<Any>?)
        }
        if (obj is BooleanArray) {
            return nullSafeToString(obj as BooleanArray?)
        }
        if (obj is ByteArray) {
            return nullSafeToString(obj as ByteArray?)
        }
        if (obj is CharArray) {
            return nullSafeToString(obj as CharArray?)
        }
        if (obj is DoubleArray) {
            return nullSafeToString(obj as DoubleArray?)
        }
        if (obj is FloatArray) {
            return nullSafeToString(obj as FloatArray?)
        }
        if (obj is IntArray) {
            return nullSafeToString(obj as IntArray?)
        }
        if (obj is LongArray) {
            return nullSafeToString(obj as LongArray?)
        }
        if (obj is ShortArray) {
            return nullSafeToString(obj as ShortArray?)
        }
        val str = obj.toString()
        return str ?: EMPTY_STRING
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: Array<Any>?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (o in array) {
            stringJoiner.add(o.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: BooleanArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (b in array) {
            stringJoiner.add(b.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: ByteArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (b in array) {
            stringJoiner.add(b.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: CharArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (c in array) {
            stringJoiner.add("'$c'")
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: DoubleArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (d in array) {
            stringJoiner.add(d.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: FloatArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (f in array) {
            stringJoiner.add(f.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: IntArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (i in array) {
            stringJoiner.add(i.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: LongArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (l in array) {
            stringJoiner.add(l.toString())
        }
        return stringJoiner.toString()
    }

    /**
     * Return a String representation of the contents of the specified array.
     *
     * The String representation consists of a list of the array's elements,
     * enclosed in curly braces (`"{}"`). Adjacent elements are separated
     * by the characters `", "` (a comma followed by a space).
     * Returns a `"null"` String if `array` is `null`.
     * @param array the array to build a String representation for
     * @return a String representation of `array`
     */
    @JvmStatic
    fun nullSafeToString(array: ShortArray?): String {
        if (array == null) {
            return NULL_STRING
        }
        val length = array.size
        if (length == 0) {
            return EMPTY_ARRAY
        }
        val stringJoiner = StringJoiner(ARRAY_ELEMENT_SEPARATOR, ARRAY_START, ARRAY_END)
        for (s in array) {
            stringJoiner.add(s.toString())
        }
        return stringJoiner.toString()
    }
}
