package yakworks.problem;

import org.junit.jupiter.api.Test;
import yakworks.api.ApiStatus;
import yakworks.api.problem.Exceptional;
import yakworks.api.problem.GenericProblem;
import yakworks.message.Msg;
import yakworks.message.MsgKey;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static yakworks.api.HttpStatus.BAD_REQUEST;

class EnforceCoverageTest {

    @Test
    void shouldCoverUnreachableThrowStatement() throws Exception {
        assertThrows(FakeProblem.class, () -> {
            throw new FakeProblem().propagate();
        });
    }

    static final class FakeProblem extends Exception implements GenericProblem<FakeProblem>, Exceptional {

        @Override
        public MsgKey getMsg() {
            return Msg.key("foo.bar");
        }

        @Override
        public URI getType() {
            return URI.create("about:blank");
        }

        @Override
        public String getTitle() {
            return "Fake";
        }

        @Override
        public ApiStatus getStatus() {
            return BAD_REQUEST;
        }

        @Override
        public FakeProblem getCause() {
            // cast is safe, since the only way to set this is our constructor
            return (FakeProblem) super.getCause();
        }

    }

}
