package yakworks.message

/**
 * MsgKey that has multiple codes to lookup.
 * Should use order to look them up
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
@Suppress("UNUSED_PARAMETER")
interface MsgMultiKey : MsgKey {

    var codes: List<String>?
        get() = null
        set(value) { throw java.lang.UnsupportedOperationException("setter not implemented") }

    /**
     * the wrapped MsgKey
     */
    var msgKey: MsgKey?
        get() = null
        set(value) {
            throw UnsupportedOperationException("setter not implemented")
        }

    override var code: String?
        get() = msgKey!!.code
        set(value) {
            msgKey!!.code = value
        }

    override var args: MsgArgs?
        get() = msgKey!!.args
        set(value) {
            msgKey!!.args = value
        }

    override var fallbackMessage: String?
        get() = msgKey!!.fallbackMessage
        set(value) {
            msgKey!!.fallbackMessage = value
        }

    companion object {
        /**
         * key from code and msgKey
         */
        @JvmStatic
        fun of(msgKey: MsgKey?): DefaultMsgMultiKey? {
            return DefaultMsgMultiKey(msgKey)
        }

        @JvmStatic
        fun ofCodes(codes: List<String>?): DefaultMsgMultiKey? {
            return DefaultMsgMultiKey(codes)
        }
    }
}
