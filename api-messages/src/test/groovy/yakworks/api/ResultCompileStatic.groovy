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
        testProblem()
        compileErrors()
        problemTesting()
        return true
    }

    static OkResult testOkResult(){
        OkResult res = Result.ofCode("foo.bar").title("ff").payload("foo").msg("ff")
        assert res instanceof OkResult
        return res
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    static Problem testProblem(){
        ProblemResult rprob = Problem.ofCode("foo.bar").title("ff").problemCause(null)
        assert rprob instanceof ProblemResult

        Problem prob = Problem.ofCode("foo.bar").payload("foo").msg("ff").detail("ff")
        Problem prob2 = Problem.ofCode("foo.bar").title("wtf").detail("ff")
        assert prob instanceof ProblemResult
        return prob
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    //shoudl error?
    static Problem compileErrors(){
        List.of(1, 2)
        Problem prob = Problem.OK()
        assert prob instanceof ProblemResult
        return prob
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    static void problemTesting(){
        Problem prob = new ProblemTesting()
        def tp = new ThrowableProblem(prob).payload("foo")
        //this should not work
        //prob.ok = true

        //should be false
        assert prob == tp.problem
        assert !tp.ok
        assert !tp.title
        assert !prob.ok
        assert !prob.title
        //blows up on setter
        // prob.title = "got it"
    }

}
