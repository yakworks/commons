package yakworks.problem;

import yakworks.api.problem.Problem;
import yakworks.message.MsgKey;

public final class EmptyProblem implements Problem.Fluent<EmptyProblem> {

    @Override
    public MsgKey getMsg() {
        return MsgKey.ofCode("some.problem.key");
    }
}
