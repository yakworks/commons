package yakworks.message

import java.text.Format
import java.util.*

/**
 * An arg wrapper that allows the args to be an array, List or Map
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
class MsgArgs {
    // will either be a list or map
    private var _value: Any? = null

    /**
     * similiar to Optional.
     * @return returns the value of args
     * @throws NoSuchElementException if args is null
     */
    fun get(): Any? {
        if (_value == null) {
            throw NoSuchElementException("No value present, call args() to set first")
        }
        return _value
    }
    // public boolean isPresent() {
    //     return value != null;
    // }
    /**
     * sets the args, if array converts to List.
     * @param args array list or map
     */
    fun setArgs(args: Any?) {
        var args = args
        if (isEmpty(args)) {
            args = LinkedHashMap<Any, Any>()
        } else if (isArray(args)) {
            val argsray = args as Array<Any>?
            //if first item is map the use that otherwise make array list
            args = if (isFirstItemMap(*argsray!!)) argsray[0] as Map<*, *> else Arrays.asList(*argsray)
        }
        if (args is Map<*, *> || args is List<*>) {
            _value = args
        } else {
            throw IllegalArgumentException("Message arguments must be a Map, List or Object array")
        }
    }

    /**
     * sets args
     * @see .setArgs
     */
    fun args(args: Any?): MsgArgs {
        setArgs(args)
        return this
    }

    /**
     * true if empty false if it has something
     */
    val isEmpty: Boolean
        get() = isEmpty(_value)

    /**
     * true if map
     */
    val isMap: Boolean
        get() = _value is Map<*, *>

    /**
     * calls messageFormat.format on the passed in messageFormat.
     * if the args is a list then it tranforms it to an array if map then passed it straight in.
     */
    fun formatWith(messageFormat: Format): String {
        //its either a list or a map
        return if (isMap) messageFormat.format(_value as Map<*, *>?) else messageFormat.format(toArray())
    }

    /**
     * converts the args list to array
     */
    fun toArray(): Array<Any?> {
        return if (_value == null) arrayOfNulls(0) else (_value as List<*>).toTypedArray()
    }

    /**
     * if args is null or empty then this initializes it to a map for names args
     * should check that return map as null means it didnt succeed
     * @return the initialized Map reference, null if its a list arg
     */
    @Suppress("UNCHECKED_CAST")
    fun asMap(): MutableMap<String, Any?>? {
        return if (isMap) _value as MutableMap<String, Any?>? else null
    }

    /**
     * adds an enrty to the msg arg if its a map
     * returns the map or null if its list/array based args
     */
    fun putIfAbsent(key: String, v: Any?): Map<*, *>? {
        val argsMap = asMap()
        argsMap?.putIfAbsent(key, v)
        return argsMap
    }

    /**
     * adds an arg to the map, see getArgMap, will set one up
     * @return the args as map
     */
    fun put(key: String, v: Any?): Map<*, *>? {
        val argMap = asMap()
        if (argMap != null) argMap[key] = v
        return argMap
    }

    /**
     * static helper to lookup the fallback in the args if its a map.
     * Looks for fallbackMessage or defaultMessage key, in that order
     */
    val fallbackMessage: String?
        get() {
            if (isMap) {
                val argMap: Map<*, *>? = asMap()
                if (!argMap!!.isEmpty()) {
                    if (argMap.containsKey("fallbackMessage")) return argMap["fallbackMessage"] as String?
                    if (argMap.containsKey("defaultMessage")) return argMap["defaultMessage"] as String?
                }
            }
            return null
        }

    companion object {
        @JvmStatic
        fun of(args: Any?): MsgArgs {
            return MsgArgs().args(args)
        }

        /**
         * gets an instance initialzed with and empty map
         */
        @JvmStatic
        fun empty(): MsgArgs {
            return MsgArgs().args(null)
        }

        /**
         * Checks if args is Array or List and if the first item is a map,
         * if so then it should use that map for the args and ignores the rest. Used for compatibility with Spring tempaltes
         * where is can only pass arrays for args
         */
        fun isFirstItemMap(vararg args: Any?): Boolean {
            return args.size == 1 && args[0] is Map<*, *>
        }

        @JvmStatic
        fun isEmpty(obj: Any?): Boolean {
            if (obj == null) return true
            if (obj.javaClass.isArray) return java.lang.reflect.Array.getLength(obj) == 0
            if (obj is Collection<*>) return obj.isEmpty()
            return if (obj is Map<*, *>) obj.isEmpty() else false
            // else
        }

        @JvmStatic
        fun isArray(obj: Any?): Boolean {
            return obj != null && obj.javaClass.isArray
        }
    }
}
