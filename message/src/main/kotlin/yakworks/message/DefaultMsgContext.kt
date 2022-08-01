package yakworks.message

import yakworks.message.MsgArgs.Companion.empty
import java.util.*

/**
 * Context and Holder for arguments so its easier to keep compatibility between named map based palceholders like icu4j
 * and ordinal array based placeholder like like spring and java.text.messageFormat
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
class DefaultMsgContext : MsgContext {
    //locale for the message
    override var locale: Locale? = null

    constructor() {}

    override var code: String? = null

    fun code(value: String?): DefaultMsgKey {
        code = value
        return this
    }

    override var args: MsgArgs? = null
        get() {
            if (field == null) field = MsgArgs.empty()
            return field
        }

    override fun setArgs(v: Any?) {
        args = MsgArgs.of(v)
    }
    /**
     * builder to pass args in.
     */
    fun args(args: Any?): DefaultMsgKey {
        setArgs(args)
        return this
    }

    override var fallbackMessage: String? = null
        get() {
            return if (field != null) field!! else args?.fallbackMessage
        }

    fun fallbackMessage(value: String?): DefaultMsgKey {
        fallbackMessage = value
        return this
    }






    //the message code/key
    override var code: String? = null

    // stored as either a list or map
    private var msgArgs: MsgArgs? = null

    // fallback message will get rendered if code fails
    override var fallbackMessage: String? = null
    override fun getLocale(): Locale {
        return locale!!
    }

    override fun setLocale(v: Locale) {
        locale = v
    }

    /** builder version of setting locale  */
    fun locale(loc: Locale?): DefaultMsgContext {
        locale = loc
        return this
    }

    override var args: MsgArgs?
        get() {
            if (msgArgs == null) msgArgs = empty()
            return msgArgs
        }
        set(v) {
            msgArgs = v
        }

    fun args(args: Any?): DefaultMsgContext {
        setArgs(args)
        return this
    }

    fun args(args: MsgArgs?): DefaultMsgContext {
        this.args = args
        return this
    }

    override fun setFallbackMessage(v: String?) {
        fallbackMessage = v
    }

    /**
     * If one is set then return it,
     * if not it looks at args and if its a map then returns the defaultMessage key if it exists
     */
    override fun getFallbackMessage(): String {
        return if (fallbackMessage != null) fallbackMessage!! else args!!.fallbackMessage!!
    }

    /** builderish setter for fallback  */
    fun fallbackMessage(defMsg: String?): DefaultMsgContext {
        fallbackMessage = defMsg
        return this
    }

    var useCodeAsDefaultMessage = true
    override fun isUseCodeAsDefaultMessage(): Boolean {
        return useCodeAsDefaultMessage
    }

    fun useCodeAsDefaultMessage(v: Boolean): DefaultMsgContext {
        useCodeAsDefaultMessage = v
        return this
    }

    companion object {
        /** static helper for quick default message  */
        fun withFallback(fallback: String?): DefaultMsgContext {
            return DefaultMsgContext().fallbackMessage(fallback)
        }
    }
}
