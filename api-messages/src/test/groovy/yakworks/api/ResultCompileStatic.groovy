/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api

import groovy.transform.CompileStatic

import yakworks.api.problem.ProblemResult
import yakworks.api.problem.Problem
import yakworks.api.problem.ProblemTesting
import yakworks.api.problem.ThrowableProblem

/**
 * This sanity checks compiles using the CompileStatic
 */
@CompileStatic
class ResultCompileStatic {

    static runAll(){
        testOkResult()
        testProblemDetail()
        return true
    }

    static OkResult testOkResult(){
        OkResult res = Result.OK().msg("foo.bar").title("ff").payload("foo").msg("ff")
        assert res instanceof OkResult
        return res
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    static Problem testProblemDetail(){
        def ex = new IllegalArgumentException("WTF")
        Problem prob = Problem.of(ex)
        //Problem prob = Problem.ofPayload("payload").msg("foo.bar").title("ff").detail("foo")
        assert prob instanceof Problem
        return prob
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

}
