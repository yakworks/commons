package yakworks.api.problem

import yakworks.api.*
import yakworks.api.ResultSupport
import yakworks.api.problem.data.DataProblem
import yakworks.api.problem.exception.NestedExceptionUtils
import yakworks.message.Msg
import yakworks.message.MsgKey
import java.net.URI

/**
 * Simple interface for problem getters
 *
 * @see [RFC 7807: Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807)
 */
@Suppress("UNUSED_PARAMETER")
interface Problem : Result {
    override val ok: Boolean get() = false

    override var msg: MsgKey?
        get() = Msg.key("general.problem")
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

    /**
     * Default statics here use the default ProblemResult.
     * Example usage: `Problem.of("some.error").payload(obj)` or Problem.withTitle("Some Error Desc").detail("some more info")
     */
    companion object Statics : ProblemCompanion<ProblemResult>() {
        // @JvmStatic not allowed in abstract companion, so we override and then delgate, only needed to be compatible with java JvmStatic
        @JvmStatic override fun of(): ProblemResult = ProblemResult()
        @JvmStatic override fun of(problemCause: Throwable?) = super.of(problemCause)
        @JvmStatic override fun of(code: String) = super.of(code)
        @JvmStatic override fun of(code: String, args: Any?) = super.of(code, args)
        @JvmStatic override fun withTitle(title: String) = super.withTitle(title)
    }

}
