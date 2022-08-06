/*
* Copyright 2021 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.message.MsgKey
import java.net.URI

/**
 * Core impl of the BaseProblem.
 */
open class ProblemResult : GenericProblem<ProblemResult> {
    // impl for result
    override val ok: Boolean = false
    override var defaultCode: String? = null
    override var title: String? = null
    override var status: ApiStatus = HttpStatus.BAD_REQUEST
    override var payload: Any? = null
    override var msg: MsgKey? = null
        get() {
            if(field == null) field = MsgKey.ofCode(defaultCode)
            return field
        }

    // problem imp
    override var type: URI? = null
    override var detail: String? = null
    override var violations: List<Violation>? = mutableListOf()
    override var problemCause: Throwable? = null

    fun problemCause(v: Throwable?): ProblemResult = apply { problemCause = v }

    override fun toString(): String {
        return ProblemUtils2.problemToString(this)
    }

}
