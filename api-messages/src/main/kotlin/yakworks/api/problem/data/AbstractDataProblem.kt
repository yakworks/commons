package yakworks.api.problem.data

import yakworks.api.*
import yakworks.api.problem.AbstractProblem
import yakworks.api.problem.ThrowableProblem
import yakworks.message.Msg
import yakworks.message.MsgKey

/**
 * Problem with generics for fluent builders so its easy to use in java and groovy.
 * We go through the efforts and gyrations here to make it fluent (meaning there are methods that return the object when used as setters)
 * means we have some kind of ugly looking self referenceing generics but the trade offs are worth it to keep usage of this clean.
 * Some good comments here, we did not go with accepted answer as we want to stick with interfaces. https://stackoverflow.com/a/23785473/6500859
 * Also here https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62
 */
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
abstract class AbstractDataProblem<E: AbstractDataProblem<E>> : AbstractProblem<E>() {

    override fun payload(v: Any?): E {
        if(v != null) {
            this.payload = v
            ResultSupport.addCommonArgs(args!!.asMap()!!, v)
        }
        return this as E
    }

    override fun toException(): ThrowableProblem {
        return if(cause != null) DataProblemException(cause).problem(this) else DataProblemException().problem(this)
    }

}

abstract class ProblemFactory<E: AbstractDataProblem<E>> {
    abstract fun createProblem(): E

    // @JvmStatic not allowed here, so make them open and then delegate to then in implementer
    open fun of(code: String) = createProblem().msg(Msg.key(code))

    open fun of(code: String, args: Any? = null) = createProblem().msg(Msg.key(code, args))

    open fun of(mk: MsgKey) = createProblem().msg(mk)

    open fun ofPayload(payload: Any?) = createProblem().payload(payload)

    open fun of(problemCause: Throwable?) = createProblem().cause(problemCause).detailFromCause()

}
