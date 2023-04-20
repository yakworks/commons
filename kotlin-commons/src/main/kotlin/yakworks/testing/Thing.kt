package yakworks.testing

import java.time.LocalDate

open class Thing (
    var name: String? = null,
    var id: Long? = null,
    var localDate: LocalDate? = null
){
    val someGetter: String
        get() = "x"

    companion object {
        @JvmStatic
        fun of(_id: Long, _name: String): Thing {
            val thing = Thing(id=_id, name = _name, )
            //thing.name = name
            //thing.id = id
            return thing
        }

        @JvmStatic
        var someStatic = "foo"
    }
}
