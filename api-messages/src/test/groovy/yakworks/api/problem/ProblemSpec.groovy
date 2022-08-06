/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem


import spock.lang.Specification
import spock.lang.Unroll
import yakworks.message.MsgKey

import static yakworks.api.HttpStatus.NOT_FOUND

class ProblemSpec extends Specification {

    void shouldRenderTestProblem() {
        expect:
        CoreProblem problem = CoreProblem.create()
        problem.toString().contains("Problem(400)")
        !problem.ok
    }

    void "problem of"() {
        when:
        def p = Problem.of('error.data.empty', [name: 'foo'])

        then:
        !p.ok
        p.toString()
        p.code == 'error.data.empty'
        p.args.asMap().name == 'foo'
    }

    void "problem throw"() {
        when:
        def p = CoreProblem.of('error.data.empty', [name: 'foo'])
        throw p.toException()

        then:
        def ex = thrown(ThrowableProblem)
        ex.problem == p

        when:
        throw p as Exception

        then:
        def ex2 = thrown(ThrowableProblem)
        ex2.problem == p
    }

    void "problem throw with cause"() {
        when:
        def rte = new RuntimeException("bad stuff")
        def p = CoreProblem.of('error.data.empty', [name: 'foo']).cause(rte)
        throw p.toException()

        then:
        def ex = thrown(ThrowableProblem)
        ex.problem == p
        ex.rootCause == rte

    }

    @Unroll
    void "init with code statics #code"(CoreProblem problem, String code) {
        expect:
        problem instanceof CoreProblem
        problem.code == code

        where:
        problem                                     | code
        CoreProblem.of('code.args', [name: 'foo'])  | 'code.args'
        CoreProblem.ofCode('ofCode')                | 'ofCode'
        CoreProblem.ofMsg(MsgKey.ofCode('withMsg')) | 'withMsg'

    }

    void "should Render Custom Detail And Instance"() {
        when:
        final CoreProblem p = new CoreProblem().status(NOT_FOUND)
            .type(URI.create("https://example.org/problem"))
            .detail("Order 123")

        then:
        p.type.toString() == "https://example.org/problem"
        // p.title == "Not Found"
        p.status == NOT_FOUND
        p.detail == "Order 123"

    }

    void shouldRenderCustomPropertiesWhenPrintingStackTrace() {
        when:
        final ProblemResult problem = new ProblemResult().status(NOT_FOUND)
            .type(URI.create("https://example.org/problem"));


        final StringWriter writer = new StringWriter()
        problem.toException().printStackTrace(new PrintWriter(writer))

        then:
        writer.toString()
        writer.toString().contains("ProblemResult(404")
    }

    void addViolations() {
        when:
        def problem = Problem.ofCode("testing")
        problem.addViolations([
            MsgKey.ofCode("foo"), MsgKey.ofCode("bar")
        ])

        then:
        problem.violations.size() == 2
    }

}
