package yakworks.api.problem

import yakworks.message.Msg

/**
 * abstract class for Companion statics.
 */
abstract class ProblemCompanion<E: GenericProblem<E>> {
    /** creates the problem with nothing set yet, equivalent to "new Problem()" */
    abstract fun of(): E
    // @JvmStatic not allowed companion supers, so make them open and then delegate to then in implementer
    open fun of(code: String) = of().msg(Msg.key(code))
    open fun of(code: String, args: Any? = null) = of().msg(Msg.key(code, args))
    open fun of(problemCause: Throwable?) = of().cause(problemCause).detailFromCause()
    open fun withTitle(title: String) = of().title(title)
}
