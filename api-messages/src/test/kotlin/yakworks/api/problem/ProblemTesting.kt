/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

/**
 * Simple OkResult with a Map as the data object
 *
 * @author Joshua Burnett (@basejump)
 */
open class ProblemTesting : GenericProblem<ProblemTesting> {
    // impl for result
    override val ok: Boolean = false
    override var defaultCode: String? = null
    override var payload: Any? = null
    //override var title: String? = null

}
