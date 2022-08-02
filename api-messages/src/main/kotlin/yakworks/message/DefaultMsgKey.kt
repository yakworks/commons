package yakworks.message


/**
 * Default implementation of the MsgKey, normally should be build with
 * MsgKey.of('somekey',....), not directly with this class
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
open class DefaultMsgKey : MsgKey {
    constructor() {}
    constructor(code: String?) {
        this.code = code
    }

    override var code: String? = null

    fun code(v: String?): DefaultMsgKey {
        code = v
        return this
    }

    override var args: MsgArgs? = null
        get() {
            if (field == null) field = MsgArgs.empty()
            return field
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

    open fun fallbackMessage(value: String?): DefaultMsgKey {
        fallbackMessage = value
        return this
    }
}
