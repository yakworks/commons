package yakworks.message

import java.lang.UnsupportedOperationException

/**
 * Default implementation of the MsgKey, normally should be build with
 * MsgKey.of('somekey',....), not directly with this class
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
class DefaultMsgKey : MsgKey {
    constructor() {}
    constructor(code: String?) {
        this.code = code
    }

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
}
