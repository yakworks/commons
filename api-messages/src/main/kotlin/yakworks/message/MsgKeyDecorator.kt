package yakworks.message

/**
 * add to a class that has a MsgKey reference as field msg
 *
 * @author Joshua Burnett (@basejump)
 * @since 0.3.0
 */
@Suppress("UNUSED_PARAMETER")
interface MsgKeyDecorator : MsgKey {
    /**
     * the MsgKey
     */
    var msg: MsgKey?
        get() = null
        set(v) { }

    override var code: String?
        get() = msg?.code
        set(v) {
            msg?.code = v
        }
    override var args: MsgArgs?
        get() = msg?.args
        set(v) {
            msg?.args = v
        }
}
