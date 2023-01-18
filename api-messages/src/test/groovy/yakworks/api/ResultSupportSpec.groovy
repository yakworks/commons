package yakworks.api

import spock.lang.Specification
import yakworks.api.problem.Problem

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

    void "resultToStringCommon"(){
        when:
        def okRes = Result.OK()

        then:
        ResultSupport.resultToStringCommon(okRes) == "200"
        okRes.title("title")
        ResultSupport.resultToStringCommon(okRes) == "title=title, 200"
        okRes.msg("x.y")
        ResultSupport.resultToStringCommon(okRes) == "title=title, code=x.y, 200"
        okRes.payload("foo")
        ResultSupport.resultToStringCommon(okRes) == "title=title, code=x.y, payload=foo, 200"
    }

    void "resultToString"(){
        when:
        def okRes = Result.OK()
        def prob = Problem.ofPayload("foo")

        then:
        ResultSupport.resultToString(okRes) == "OkResult(ok=true, 200)"
        okRes.title("title")
        ResultSupport.resultToString(okRes) == "OkResult(ok=true, title=title, 200)"
        //problem
        ResultSupport.resultToString(prob) == "ProblemResult(ok=false, payload=foo, 400)"
    }
}
