package yakworks.openapi

import org.junit.jupiter.api.Test
import yakworks.commons.testing.pogos.Thing
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import java.nio.file.Files
import java.nio.file.Paths

//junit5 example
internal class OapiUtilsTest {

    @Test
    fun test_saveYaml() {
        var strList = OapiUtilsKt.convertEnum(SomeEnum::class.java)

        assert(strList == listOf("WTF", "GO"))
    }
}
