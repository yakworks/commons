package yakworks

import org.junit.jupiter.api.Test
import yakworks.api.Result

//junit5 example
internal class ResultSupportTest {

    var x = 2

    @Test
    fun this_is_an_example() {
        val okRes = Result.OK()
        assert(okRes.ok)
    }
}
