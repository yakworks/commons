/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic

import yakworks.api.problem.CoreProblem
import yakworks.api.problem.Problem

@CompileStatic
class ResultCompileStatic {

    static OkResult testOkResult(){
        OkResult res = Result.ofCode("foo.bar").msg("ff").payload("foo").msg("ff")
        assert res instanceof OkResult
        return res
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    static Problem testProblem(){
        Problem prob = Problem.ofCode("foo.bar").msg("ff").payload("foo").msg("ff")
        // Problem prob = Problem.ofCode("foo.bar").detail("ff")
        assert prob instanceof CoreProblem
        return prob
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

}
