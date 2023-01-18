package yakworks.api.problem.data

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.message.Msg.key

/**
 * Concrete problem for configuration or setup errors or inconsistencies
 */
class ConfigProblem : AbstractDataProblem<ConfigProblem>() {
    override var defaultCode = DEFAULT_CODE
    override var status: ApiStatus = HttpStatus.INTERNAL_SERVER_ERROR

    companion object {
        /**
         * helper for legacy to throw a DataProblemException with a message
         */
        fun ex(message: String?): DataProblemException {
            val cp = ConfigProblem()
            cp.msg = key(DEFAULT_CODE)
            return cp.title(message).toException() as DataProblemException
        }

        var DEFAULT_CODE = "error.configuration.problem"
    }
}
