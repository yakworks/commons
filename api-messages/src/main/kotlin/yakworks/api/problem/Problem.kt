package yakworks.api.problem

import yakworks.api.Result
import yakworks.api.ResultSupport
import yakworks.api.ResultSupport.toMap
import yakworks.message.MsgKey
import yakworks.message.MsgKey.Companion.ofCode
import java.net.URI

/**
 * Simple interface for problem getters
 *
 * @see [RFC 7807: Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807)
 */
interface Problem : Result {

    override val ok: Boolean
        get() = false

    override var msg: MsgKey?
        get() = ofCode("problem.blank")
        set(msg) {
            super.msg = msg
        }

    /**r
     * An absolute URI that identifies the problem type. When dereferenced,
     * it SHOULD provide human-readable documentation for the problem type
     * (e.g., using HTML). When this member is not present, its value is
     * assumed to be "about:blank".
     *
     * @return an absolute URI that identifies this problem's type
     */
    var type: URI?
        get() = null
        set(v) {}

    /**
     * A human readable explanation specific to this occurrence of the problem.
     *
     * @return A human readable explaination of this problem
     */
    var detail: String?
        get() = null
        set(v) {}

    /**
     * The list of constraint violations or any others
     */
    var violations: List<Violation?>?
        get() = emptyList<Violation>()
        set(v) {}

    /**
     * converts to Map, helpfull for to json and can be overriden on concrete impls
     */
    override fun asMap(): Map<String, Any?> {
        val hmap = ResultSupport.toMap(this) as MutableMap<String, Any?>
        hmap["type"] = type
        hmap["detail"] = detail
        if (violations!!.size > 0) {
            hmap["errors"] = violations
        }
        return hmap
    }

    /**
     * An absolute URI that identifies the specific occurrence of the problem.
     * It may or may not yield further information if dereferenced.
     */
    val instanceURI: URI?
        get() = null

    interface Fluent<E : Fluent<E>> : Problem, Result.Fluent<E> {

        //Problem builders
        fun <E: Fluent<E>> detail(v: String?): E {
            detail = v
            return this as E
        }

        fun <E: Fluent<E>> type(v: URI?): E {
            type = v
            return this as E
        }

        fun <E: Fluent<E>> type(v: String?): E {
            type = URI.create(v)
            return this as E
        }

        fun <E: Fluent<E>> violations(v: List<Violation?>?): E {
            violations = v
            return this as E
        }
    }
}
