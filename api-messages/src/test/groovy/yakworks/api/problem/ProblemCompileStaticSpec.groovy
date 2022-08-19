/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import spock.lang.Specification
import yakworks.api.ResultCompileStatic

// import yakworks.commons.map.Maps

class ProblemCompileStaticSpec extends Specification {

    void "run statics"(){
        expect:
        ProblemCompileStatic.testProblemOf()
        ProblemCompileStatic.testProblem()
        ProblemCompileStatic.compileErrors()
        ProblemCompileStatic.problemTesting()
    }

}