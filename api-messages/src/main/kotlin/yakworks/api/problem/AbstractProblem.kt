package yakworks.api.problem

import yakworks.api.*
import yakworks.api.problem.exception.NestedExceptionUtils
import yakworks.message.Msg
import yakworks.message.MsgKey
import java.net.URI

/**
 * Problem with generics for fluent builders so its easy to use in java and groovy.
 * We go through the efforts and gyrations here to make it fluent (meaning there are methods that return the object when used as setters)
 * means we have some kind of ugly looking self referenceing generics but the trade offs are worth it to keep usage of this clean.
 * Some good comments here, we did not go with accepted answer as we want to stick with interfaces. https://stackoverflow.com/a/23785473/6500859
 * Also here https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62
 */
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
abstract class AbstractProblem<E: AbstractProblem<E>> : GenericProblem<E> {

    override val ok: Boolean = false
    override val defaultCode: String? = null
    override var title: String? = null
    override var detail: String? = null
    override var status: ApiStatus = HttpStatus.BAD_REQUEST
    override var payload: Any? = null
    override var type: URI? = null
    override var problemCause: Throwable? = null
    override var violations: List<Violation>? = mutableListOf()

    override var msg: MsgKey? = null
        get() {
            if(field == null) field = Msg.key(defaultCode)
            return field
        }

    fun problemCause(v: Throwable?): E = apply { problemCause = v } as E

    override fun toString(): String {
        return ProblemUtils.problemToString(this)
    }
}
