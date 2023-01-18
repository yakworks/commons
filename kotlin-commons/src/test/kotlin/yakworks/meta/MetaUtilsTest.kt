package yakworks.meta

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import yakworks.commons.testing.pogos.Thing
import yakworks.testing.SomeClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import java.nio.file.Files
import java.nio.file.Paths

//junit5 example
internal class MetaUtilsTest {

    @Test
    fun test_saveYaml() {
        var klazz = SomeClass::class

        assert(klazz.memberProperties != null)
    }

    @Test
    @Disabled
    fun blows_up_with_groovy_trait() {
        var klazz = Thing::class

        assert(klazz.memberProperties != null)
    }
}
