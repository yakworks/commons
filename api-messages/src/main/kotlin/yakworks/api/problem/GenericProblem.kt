package yakworks.api.problem

import yakworks.api.*
import yakworks.api.problem.exception.NestedExceptionUtils
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
interface GenericProblem<E: GenericProblem<E>> : Problem, GenericResult<E> {

    override var msg: MsgKey?
        get() = MsgKey.ofCode("general.problem")
        set(v) { noImpl() }

    val cause: Throwable?
        get() = problemCause

    //Problem builders
    fun cause(v: Throwable?): E = apply { problemCause = v } as E

    fun detailFromCause(): E {
        detail = NestedExceptionUtils.getMostSpecificCause(problemCause!!).message
        return this as E
    }

    /**
     * fluent setter use URI.create(v) go create from a string
     */
    fun type(v: URI): E = apply { type = v } as E

    fun violations(v: List<Violation>): E = apply { violations = v as MutableList } as E

    fun addViolations(keyedErrors: List<MsgKey>): E {
        val ers = violations as MutableList
        keyedErrors.forEach{
            ers.add(ViolationFieldError.of(it))
        }
        return this as E
    }

    fun toException(): RuntimeException {
        if(problemCause != null)
            return ThrowableProblem(problemCause as Throwable).problem(this)
        else
            return ThrowableProblem().problem(this)
    }

}
