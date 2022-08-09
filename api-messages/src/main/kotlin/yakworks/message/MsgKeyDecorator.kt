package yakworks.message

/**
 * add to a class that has a msgKey and will delegate to it.
 * @author Joshua Burnett (@basejump)
 */
@Suppress("UNUSED_PARAMETER")
interface MsgKeyDecorator : MsgKey {
    /** the MsgKey to wrap*/
    var msg: MsgKey?

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
