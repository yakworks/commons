package yakworks.message

/**
 * Convenience statics for making DefaultMsgKey
 * can do `Msg.key('foo.bar)` as quick concise builder.
 * This allows to keep MsgKey as stricly a simple interface then without the static baaggage.
 */
@Suppress("UNUSED_PARAMETER")
object Msg {

    /** Make key from code */
    @JvmStatic
    fun key(code: String?): DefaultMsgKey = DefaultMsgKey(code)

    /** key from code and map args */
    @JvmStatic
    fun key(code: String, args: Any?): DefaultMsgKey = DefaultMsgKey(code).args(args)

}
