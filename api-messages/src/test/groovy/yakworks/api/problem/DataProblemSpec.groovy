/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package yakworks.api.problem

import spock.lang.Specification
import yakworks.api.problem.data.DataProblem
import yakworks.api.problem.data.DataProblemException
import yakworks.api.problem.data.DataProblemCodes

class DataProblemSpec extends Specification {

    void "DataProblemKinds sanity check"() {
        when:
        def rte = new RuntimeException("bad stuff")
        def e = DataProblemCodes.ReferenceKey.get().cause(rte).toException()

        then:
        e.code == 'error.data.reference'
        e.rootCause == rte
        e.message == "Data Problem: Reference or foriegn key error and this cant be updated or deleted: code=error.data.reference"
        e.problem.code == 'error.data.reference'
        e.problem.title == 'Data Problem'
        e.problem.detail == 'Reference or foriegn key error and this cant be updated or deleted'
    }

    void "DataProblem entity payload"() {
        when:
        def someEntity = DataProblem.createProblem()

        then:
        thrown(UnsupportedOperationException)
    }

    void "DataProblem entity payload2"() {
        when:
        def someEntity = new SomeEntity()
        def e = DataProblem.of('foo').payload(someEntity).toException()

        then:
        e instanceof DataProblemException
        e instanceof ThrowableProblem
        e.problem
        e.code == 'foo'
        e.args.asMap().name == 'SomeEntity'
    }

    void "DataProblem ex exception"() {
        when:
        DataProblemException e = DataProblem.ex("foo error")

        then:
        e.detail == "foo error"
        e.message.startsWith "foo error"
        e instanceof ThrowableProblem
        e.problem instanceof DataProblem
    }

    static class SomeEntity {

    }
}
