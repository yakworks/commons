/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.converters

import spock.lang.Specification
import yakworks.api.problem.Problem
import yakworks.api.problem.ViolationFieldError
import yakworks.json.groovy.JsonEngine
import yakworks.message.Msg
import yakworks.message.MsgKey

// import yakworks.commons.map.Maps

class ProblemConverterSpec extends Specification {

    void "simple problem"(){
        when:
        Problem prob2 = Problem.of("foo.bar").detail("ff")
        Problem prob = Problem.of("some.code").title("crash").detail("crash detail")
        prob.violations = [
            ViolationFieldError.of(Msg.key('v1')).field('f1')
        ]
        String res = JsonEngine.toJson(prob)

        then:
        res == '{"ok":false,"status":400,"code":"some.code","title":"crash","detail":"crash detail","errors":[{"code":"v1","field":"f1"}]}'
    }

}
