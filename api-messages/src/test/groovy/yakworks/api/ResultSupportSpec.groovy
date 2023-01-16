package yakworks.api

import spock.lang.Specification

//junit5 example
class ResultSupportSpec extends Specification {

    void "simple Ok"(){
        when:
        def okRes = Result.OK()
        def okMap = ResultSupport.toMap(okRes)
        String emptyString = ""
        String nullString = null

        then:
        emptyString.isEmpty()
        //nullString?.isEmpty()

        okRes.ok
        okMap["ok"] == true
    }
}
