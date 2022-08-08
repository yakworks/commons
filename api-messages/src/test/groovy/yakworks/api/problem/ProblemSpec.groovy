/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem


import spock.lang.Specification
import spock.lang.Unroll
import yakworks.message.Msg
import yakworks.message.MsgKey

import static yakworks.api.HttpStatus.NOT_FOUND

class ProblemSpec extends Specification {

    void shouldRenderTestProblem() {
        expect:
        Problem problem = Problem.of()
        problem.toString().contains("ProblemResult(400)")
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
        def p = Problem.of('error.data.empty', [name: 'foo'])
        throw p.toException()

        then:
        def ex = thrown(ThrowableProblem)
        ex.problem == p
    }

    void "problem throw with cause"() {
        when:
        def rte = new RuntimeException("bad stuff")
        def p = Problem.of('error.data.empty', [name: 'foo']).cause(rte)
        throw p.toException()

        then:
        def ex = thrown(ThrowableProblem)
        ex.problem == p
        ex.rootCause == rte

    }

    @Unroll
    void "init with code statics #code"(Problem problem, String code) {
        expect:
        problem instanceof Problem
        problem.code == code

        where:
        problem                                     | code
        Problem.of('code.args', [name: 'foo'])  | 'code.args'
        Problem.of('ofCode')                | 'ofCode'
        Problem.of(Msg.key('withMsg'))    | 'withMsg'

    }

    void "should Render Custom Detail And Instance"() {
        when:
        final Problem p = new ProblemResult().status(NOT_FOUND)
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
        def problem = Problem.of("testing")
        problem.addViolations([
            Msg.key("foo"), Msg.key("bar")
        ])

        then:
        problem.violations.size() == 2
    }

}
