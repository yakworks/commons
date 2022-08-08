/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import groovy.transform.CompileStatic

import yakworks.api.OkResult
import yakworks.api.Result

/**
 * This sanity checks compiles using the CompileStatic
 */
@CompileStatic
class ProblemCompileStatic {

    static runAll(){
        testProblemOf()
        return true
    }

    static Problem testProblemOf(){
        Problem prob = Problem.of("foo.bar").title("ff").payload("foo").msg("ff")
        prob
    }

    static Problem testProblem(){
        ProblemResult rprob = Problem.of("foo.bar").title("ff").problemCause(null)
        assert rprob instanceof ProblemResult

        // Problem prob = Problem.ofCode("foo.bar").payload("foo").msg("ff").detail("ff")
        Problem prob = Problem.of("foo.bar").title("wtf").detail("ff")
        assert prob instanceof ProblemResult
        return prob
        // prob.payload("foo").msg("ff")
        // Result.Fluent probtrait =  (Result.Fluent) prob
        // probtrait.payload("foo").msg()
    }

    //shoudl error?
    static Problem compileErrors(){
        List.of(1, 2)
        // Problem prob = Problem.OK() //should throw error
        Problem prob = Problem.of()
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
