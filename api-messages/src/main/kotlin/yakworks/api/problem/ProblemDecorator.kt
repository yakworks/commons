package yakworks.api.problem

import yakworks.api.ApiStatus
import yakworks.message.MsgKey
import java.net.URI

/**
 * wraps an object that has the problem field.
 * needed for exceptions as couldnt sort out how to get the proper constructors overriden for cause and do kotlins delgates at same time.
 * So we do it the old fashioned way.
 */
@Suppress("UNUSED_PARAMETER")
interface ProblemDecorator<P : GenericProblem<P>>: GenericProblem<P> {

    var problem: GenericProblem<*>

    override var msg: MsgKey?
        get() = problem.msg
        set(value) { problem.msg = value }

    override val defaultCode: String?
        get() = problem.defaultCode

    override var title: String?
        get() = problem.title
        set(value) { problem.title = value }

    override var status: ApiStatus
        get() = problem.status
        set(value) { problem.status = value }

    override var payload: Any?
        get() = problem.payload
        set(value) { problem.payload = value }

    // --- problem impl ---
    override var type: URI?
        get() = problem.type
        set(value) { problem.type = value }
    override var detail: String?
        get() = problem.detail
        set(value) { problem.detail = value }
    override var violations: List<Violation>?
        get() = problem.violations
        set(value) { problem.violations = value }
    override var problemCause: Throwable?
        get() = problem.problemCause
        set(value) { problem.problemCause = value }

}
