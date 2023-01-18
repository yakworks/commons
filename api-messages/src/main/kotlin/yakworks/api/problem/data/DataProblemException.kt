package yakworks.api.problem.data

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import yakworks.api.problem.ThrowableProblem

/**
 * generic problem
 */
open class DataProblemException : ThrowableProblem {
    constructor() : super()
    constructor(cause: Throwable?) : super(cause!!)

}
