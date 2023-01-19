package yakworks.api.problem.data

import yakworks.api.problem.ProblemCompanion

/**
 * generic problem
 */
class DataProblem : AbstractDataProblem<DataProblem>() {
    override var defaultCode = "error.data.problem"

    /**
     * Default statics here use the default ProblemResult.
     * Example usage: `Problem.of("some.error").payload(obj)` or Problem.withTitle("Some Error Desc").detail("some more info")
     */
    companion object Statics : ProblemCompanion<DataProblem>() {
        // @JvmStatic not allowed in abstract companion, so we override and then delgate, only needed to be compatible with java JvmStatic
        @JvmStatic override fun of(): DataProblem = DataProblem()
        @JvmStatic override fun of(problemCause: Throwable?) = super.of(problemCause)
        @JvmStatic override fun of(code: String) = super.of(code)
        @JvmStatic override fun of(code: String, args: Any?) = super.of(code, args)
        @JvmStatic override fun withTitle(title: String) = super.withTitle(title)
        /**
         * helper for legacy to throw a DataProblemException with a
         * Title will blank so it can come from the code on render and detail will have the custom message
         */
        @JvmStatic
        fun ex(detailMessage: String?): DataProblemException {
            return DataProblem().detail(detailMessage).toException() as DataProblemException
        }
    }
}
