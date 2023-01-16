package yakworks.meta

import org.junit.jupiter.api.Test
import yakworks.commons.testing.pogos.Thing
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import java.nio.file.Files
import java.nio.file.Paths

//junit5 example
internal class MetaUtilsTest {

    @Test
    fun test_saveYaml() {
        var klazz = Thing::class

        assert(klazz.memberProperties != null)
    }
}
