package yakworks.problem;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import yakworks.api.problem.CreateProblem;
import yakworks.api.problem.Problem;
import yakworks.api.problem.exception.ProblemBuilder;
import yakworks.api.problem.exception.ProblemRuntime;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static yakworks.api.HttpStatus.BAD_REQUEST;

@SuppressWarnings("unchecked")
class ProblemBuilderTest {

    private final URI type = URI.create("https://example.org/out-of-stock");

    @Test
    void shouldCreateEmptyProblem() {
        final Problem problem = CreateProblem.create();

        assertThat(problem, hasFeature("title", Problem::getTitle, is(nullValue())));
        assertThat(problem, hasFeature("detail", Problem::getDetail, is(nullValue())));
    }

    @Test
    void shouldCreateProblem() {
        final Problem problem = ProblemBuilder.of(ProblemRuntime.class)
                .type(type)
                .title("Out of Stock")
                .status(BAD_REQUEST)
                .build();

        assertThat(problem, hasFeature("type", Problem::getType, is(type)));
        assertThat(problem, hasFeature("title", Problem::getTitle, is("Out of Stock")));
        assertThat(problem, hasFeature("status", Problem::getStatus, is(BAD_REQUEST)));
        assertThat(problem, hasFeature("detail", Problem::getDetail, is(nullValue())));
    }

    @Test
    void shouldCreateProblemWithDetail() {
        final Problem problem = new ProblemBuilder()
                .type(type)
                .title("Out of Stock")
                .status(BAD_REQUEST)
                .detail("Item B00027Y5QG is no longer available")
                .build();

        assertThat(problem, hasFeature("detail", Problem::getDetail, is("Item B00027Y5QG is no longer available")));
    }

    @Test @Disabled
    void shouldCreateProblemWithCause() {
        final ProblemRuntime problem = (ProblemRuntime) new ProblemBuilder(ProblemRuntime.class)
                .type(URI.create("https://example.org/preauthorization-failed"))
                .title("Preauthorization Failed")
                .status(BAD_REQUEST)
                .cause((Throwable) new ProblemBuilder(ProblemRuntime.class)
                        .type(URI.create("https://example.org/expired-credit-card"))
                        .title("Expired Credit Card")
                        .status(BAD_REQUEST)
                        .build())
                .build();

        // assertThat(problem, hasFeature("cause", ProblemRuntime::getCause, notNullValue()));

        final ProblemRuntime cause = (ProblemRuntime)problem.getCause();
        assertThat(cause, hasFeature("type", Problem::getType, hasToString("https://example.org/expired-credit-card")));
        assertThat(cause, hasFeature("title", Problem::getTitle, is("Expired Credit Card")));
        assertThat(cause, hasFeature("status", Problem::getStatus, is(BAD_REQUEST)));
    }


}
