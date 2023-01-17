package yakworks.commons.lang

import org.junit.jupiter.api.Test
import yakworks.openapi.OapiUtilsKt
import yakworks.testing.SomeEnum

//junit5 example
internal class EnumUtilsTest {

    @Test
    fun test_getEnumList() {
        var l = EnumUtils.getEnumList(SomeEnum::class.java)

        assert(l == listOf(SomeEnum.WTF, SomeEnum.GO))
    }

    @Test
    fun test_getNameList() {
        var strList = EnumUtils.getNameList(SomeEnum::class.java)

        assert(strList == listOf("WTF", "GO"))
    }
}
