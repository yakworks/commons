package yakworks.api.problem.data

import yakworks.message.MsgKey

/**
 * generic problem
 */
class DataProblem : AbstractDataProblem<DataProblem>() {
    override var defaultCode = "error.data.problem"

    companion object Factory : ProblemFactory<DataProblem>() {

        @JvmStatic
        override fun createProblem(): DataProblem = DataProblem()

        /**
         * helper for legacy to throw a DataProblemException with a
         * Title will blank so it can come from the code on render and detail will have the custom message
         */
        @JvmStatic
        fun ex(detailMessage: String?): DataProblemException {
            return DataProblem().detail(detailMessage).toException() as DataProblemException
        }

        // @JvmStatic not allowed in abstract companion, so override and delgate, only needed because we need JvmStatic
        @JvmStatic override fun of(problemCause: Throwable?) = super.of(problemCause)
        @JvmStatic override fun of(code: String) = super.of(code)
        @JvmStatic override fun of(code: String, args: Any?) = super.of(code, args)
        @JvmStatic override fun of(mk: MsgKey) = super.of(mk)
        @JvmStatic override fun ofPayload(payload: Any?) = super.ofPayload(payload)

    }
}
