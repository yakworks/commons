package yakworks.api.problem.data

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import yakworks.api.ApiStatus
import yakworks.api.HttpStatus
import yakworks.api.problem.ThrowableProblem
import java.io.Serializable

/**
 * fillInStackTrace is overriden to show nothing
 * so it will be faster and consume less memory when thrown.
 *
 * @author Joshua Burnett (@basejump)
 * @since 6.1
 */
class NotFoundProblem : AbstractDataProblem<NotFoundProblem>() {
    fun name(nm: String?): NotFoundProblem {
        name = nm
        args!!.putIfAbsent("name", nm)
        return this
    }

    fun lookupKey(k: Serializable): NotFoundProblem {
        val map = LinkedHashMap<String, Serializable>(1)
        map["id"] = k
        key = DefaultGroovyMethods.asType(if (k is Map<*, *>) k else map, Serializable::class.java)
        args!!.putIfAbsent("key", key)
        return this
    }

    override fun toException(): ThrowableProblem {
        return Exception().problem(this)
    }

    override var defaultCode = "error.notFound"
    override var status: ApiStatus = HttpStatus.NOT_FOUND
        get() = field as HttpStatus
    var key: Serializable? = null
    var name: String? = null

    class Exception : DataProblemException() {
        @Synchronized
        override fun fillInStackTrace(): Throwable {
            return this
        }
    }

    companion object {
        fun of(key: Serializable, entityName: String): NotFoundProblem? {
            val p = NotFoundProblem().lookupKey(key).name(entityName)
            return p.detail(entityName + " lookup failed using key " + p.key.toString())
        }
    }
}
