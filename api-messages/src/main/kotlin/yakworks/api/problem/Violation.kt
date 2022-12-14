package yakworks.api.problem

import yakworks.api.AsMap
import yakworks.message.MsgKey

/**
 * @author Joshua Burnett (@basejump)
 * @since 7.0.8
 */
interface Violation : AsMap {
    val msg: MsgKey?
    val code: String?
        get() = if (msg != null) msg!!.code else null
    val field: String?
        get() = null
    val message: String?
        get() = null

    override fun asMap(): Map<String, Any?> {
        val hmap: MutableMap<String, Any?> = HashMap()
        hmap["code"] = this.code
        hmap["field"] = field
        hmap["message"] = message
        return hmap
    }
}
