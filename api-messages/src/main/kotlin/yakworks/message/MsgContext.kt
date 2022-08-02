package yakworks.message

import java.util.*
import java.util.function.Function

/**
 * Context and Holder for arguments so its easier to keep compatibility between named map based palceholders like icu4j
 * and ordinal array based placeholder like like spring and java.text.messageFormat
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
@Suppress("UNCHECKED_CAST")
class MsgContext : DefaultMsgKey() {
    //locale for the message
    var locale: Locale? = null

    override fun fallbackMessage(value: String?): MsgContext {
        super.fallbackMessage = value
        return this
    }

    /** builder version of setting locale  */
    fun locale(loc: Locale?): MsgContext {
        locale = loc
        return this
    }

    var isUseCodeAsDefaultMessage = true
    fun useCodeAsDefaultMessage(v: Boolean): MsgContext {
        isUseCodeAsDefaultMessage = v
        return this
    }

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
    fun transformList(transformation: (Any?) -> String): MsgContext {
        val curArgList = args!!.get() as List<*>?
        val newArgs: MutableList<Any> = ArrayList(curArgList!!.size)
        for (item in curArgList) newArgs.add(transformation(item))
        return of(newArgs).locale(locale)
    }

    companion object {
        /** static helper for quick default message  */
        @JvmStatic
        fun withFallback(fallback: String?): MsgContext {
            return MsgContext().fallbackMessage(fallback)
        }

        @JvmStatic
        fun of(loc: Locale?): MsgContext {
            return MsgContext().locale(loc)
        }

        @JvmStatic
        fun of(args: Any?): MsgContext {
            return MsgContext().args(args) as MsgContext
        }

        /**
         * makes context using the args and fallback message from msgKey.
         * Does NOT copy the code, just the args and fallback
         */
        @JvmStatic
        fun of(msgKey: MsgKey): MsgContext {
            return MsgContext().args(msgKey.args).fallbackMessage(msgKey.fallbackMessage) as MsgContext
        }
    }
}
