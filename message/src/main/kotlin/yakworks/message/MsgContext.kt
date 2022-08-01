package yakworks.message

import java.util.*
import java.util.function.Function

/**
 * A MsgKey but with a Locale. Also has a transform helper to do lookup when args are also 'message keys'  that should
 * be looked up first before being passed as args for main message.
 * Context and Holder for arguments so its easier to keep compatibility between named map based palceholders like icu4j
 * and ordinal array based placeholder like spring and java.text.messageFormat
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
class MsgContext : MsgKey {
    var locale: Locale? = null

    /**
     * transforms the arguments with the transformation Function and returns a new MsgContext.
     * Used when the args are also message keys (such as a MessageSourceResolvable) and they need
     * to inturn be looked up message.properties before being passed as args for the primary message
     */
    fun transform(transformation: (Any?) -> String): MsgContext {
        return if (args!!.isMap) transformMap(transformation) else transformList(transformation)
    }

    /**
     * called from transform when the args are mapped based
     */
    fun transformMap(transformation: (Any?) -> String): MsgContext {
        val curArgMap = args?.get() as Map<String, Any?>?
        val newArgs = emptyMap<String, Any?>().toMutableMap()
        if (curArgMap != null) {
            for ((key, value) in curArgMap) {
                newArgs[key] = transformation(value)
            }
        }
        return of(newArgs).locale(locale)
    }

    /**
     * called from transform when the args are array list based.
     */
    fun transformList(transformation: Function<*, *>): MsgContext {
        val curArgList = args!!.get() as List<*>?
        val newArgs: MutableList<Any> = ArrayList(curArgList!!.size)
        for (item in curArgList) newArgs.add(transformation.apply(item))
        return of(newArgs).locale(locale)
    }

    val isUseCodeAsDefaultMessage: Boolean

    companion object {
        // E locale(Locale loc);
        @JvmStatic
        fun of(loc: Locale?): DefaultMsgContext? {
            return DefaultMsgContext().locale(loc)
        }

        @JvmStatic
        fun of(args: Any?): DefaultMsgContext {
            return DefaultMsgContext().args(args)
        }

        /**
         * makes context using the args and fallback message from msgKey.
         * Does NOT copy the code, just the args and fallback
         */
        @JvmStatic
        fun of(msgKey: MsgKey): DefaultMsgContext? {
            return DefaultMsgContext().args(msgKey.args).fallbackMessage(msgKey.fallbackMessage)
        }
    }
}
