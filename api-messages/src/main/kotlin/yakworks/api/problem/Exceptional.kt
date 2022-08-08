package yakworks.api.problem

/**
 * An extension of the [Problem] interface for problems that extend [Exception]. Since [Exception]
 * is a concrete type any class can only extend one exception type. [ThrowableProblem] is one choice, but we
 * don't want to force people to extend from this but choose their own super class. For this they can implement this
 * interface and get the same handling as [ThrowableProblem] for free. A common use case would be:
 *
 * <pre>`public final class OutOfStockException extends BusinessException implements Exceptional
`</pre> *
 *
 * @see Exception
 *
 * @see Problem
 *
 * @see ThrowableProblem
 */
interface Exceptional : Problem {
    val cause: Exceptional?

    fun propagate(): Exception {
        throw propagateAs(Exception::class.java)
    }

    fun <X : Throwable> propagateAs(type: Class<X>): X {
        throw type.cast(this)
    }
}
