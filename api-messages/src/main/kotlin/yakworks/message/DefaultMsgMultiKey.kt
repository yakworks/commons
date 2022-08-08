package yakworks.message

/**
 * Default implementation of the MsgKey, normally would be build with the static helpers
 * Msg.key('somekey',....), not directly with this class
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
class DefaultMsgMultiKey : MsgMultiKey {

    override var codes: List<String>? = null
    override var msgKey: MsgKey? = null

    constructor() {}
    constructor(codes: List<String>?) {
        this.codes = codes
        msgKey = DefaultMsgKey()
    }

    constructor(msg: MsgKey?) {
        msgKey = msg
    }

    fun codes(v: List<String>?): DefaultMsgMultiKey {
        codes = v
        return this
    }

    fun msgKey(v: MsgKey?): DefaultMsgMultiKey {
        msgKey = v
        return this
    }
}
