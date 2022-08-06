package yakworks.problem;

import yakworks.api.problem.GenericProblem;
import yakworks.message.MsgKey;

public final class EmptyProblem implements GenericProblem<EmptyProblem> {

    @Override
    public MsgKey getMsg() {
        return MsgKey.ofCode("some.problem.key");
    }
}
