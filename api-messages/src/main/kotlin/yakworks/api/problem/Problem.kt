package yakworks.api.problem

import yakworks.api.*
import yakworks.api.ResultSupport
import yakworks.api.problem.exception.NestedExceptionUtils
import yakworks.message.MsgKey
import java.net.URI

/**
 * Simple interface for problem getters
 *
 * @see [RFC 7807: Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807)
 */
@Suppress("UNUSED_PARAMETER")
interface Problem : Result {
    override val ok: Boolean? get() = false

    override var msg: MsgKey?
        get() = MsgKey.ofCode("general.problem")
        set(v) { noImpl() }

    /**r
     * An absolute URI that identifies the problem type. When dereferenced,
     * it SHOULD provide human-readable documentation for the problem type
     * (e.g., using HTML). When this member is not present, its value is
     * assumed to be "about:blank".
     *
     * @return an absolute URI that identifies this problem's type
     */
    var type: URI?
        get() = URI.create("about:blank")
        set(v) { noImpl() }

    /**
     * The list of constraint violations or any others
     */
    var violations: List<Violation>?
        get() = mutableListOf()
        set(v) { noImpl() }

    /**
     * The list of constraint violations or any others
     */
    var problemCause: Throwable?
        get() = null
        set(v) { noImpl() }

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
    var instanceURI: URI?
        get() = null
        set(v) { noImpl() }


    companion object {
        //STATIC HELPERS
        //this is just to override the OK, doesnt make sense in the context of a Problem
        @JvmStatic
        fun OK() = noImpl()

        @JvmStatic
        fun of(): ProblemResult = ProblemResult()

        @JvmStatic
        fun of(code: String): ProblemResult = ProblemResult().msg(MsgKey.ofCode(code))

        @JvmStatic
        fun of(code: String, args: Any? = null): ProblemResult = ProblemResult().msg(MsgKey.of(code, args))

        @JvmStatic
        fun of(mk: MsgKey): ProblemResult = ProblemResult().msg(mk)

        @JvmStatic
        fun of(problemCause: Throwable): ProblemResult = ProblemResult().cause(problemCause).detailFromCause()

        @JvmStatic
        fun ofPayload(payload: Any?): ProblemResult = ProblemResult().payload(payload)

    }
}
