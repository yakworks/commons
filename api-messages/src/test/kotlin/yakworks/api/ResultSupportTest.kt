package yakworks.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import yakworks.api.Result
import yakworks.api.ResultSupport

//junit5 example
internal class ResultSupportTest {

    var x = 2

    @Test
    fun this_is_an_example() {
        val okRes = Result.OK()
        val okMap = ResultSupport.toMap(okRes)
        val emptyString = ""
        val nullString: String? = null

        assert(emptyString.isEmpty())
        assert(nullString.isNullOrEmpty())

        assert(okRes.ok)
        assert(okMap["ok"] == true)
    }
}
