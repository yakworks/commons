package yakworks.api;

import yakworks.api.problem.exception.ProblemRuntime;

@SuppressWarnings("unused") // since we're testing access levels we're fine if this compiles
public final class MyProblem extends ProblemRuntime {

    MyProblem() {}

    MyProblem(final ProblemRuntime cause) {
        super(cause);
    }

}
