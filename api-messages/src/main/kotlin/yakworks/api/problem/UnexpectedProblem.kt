package yakworks.api.problem

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.message.Msg

/**
 * Concrete problem for unexpected exceptions or untrapped that can be called as a flow through
 * These can get special andling and alerts in logging as , well , they should not have happened
 * and deserve attention as it means code is fubarred.
 */
class UnexpectedProblem : AbstractProblem<UnexpectedProblem>() {
    override var defaultCode = DEFAULT_CODE
    override var status: ApiStatus = HttpStatus.INTERNAL_SERVER_ERROR

    companion object {
        var DEFAULT_CODE = "error.unexpected"

        fun ex(message: String?): ThrowableProblem {
            return UnexpectedProblem().msg(Msg.key(DEFAULT_CODE)).detail(message).toException() as ThrowableProblem
        }
    }
}
