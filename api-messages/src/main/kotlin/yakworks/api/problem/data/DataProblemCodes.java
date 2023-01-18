package yakworks.api.problem.data;

import java.util.Map;

/**
 * Enum helper for codes
 */
public enum DataProblemCodes {
    NotFound("error.notFound"), OptimisticLocking("error.data.optimisticLocking"), ReferenceKey("error.data.reference"), UniqueConstraint("error.data.uniqueConstraintViolation");

    DataProblemCodes(String code) {
        this.code = code;
    }

    public DataProblem get() {
        return new DataProblem().msg(code);
    }

    public DataProblem withArgs(Map args) {
        return new DataProblem().msg(code, args);
    }

    public DataProblem of(Throwable cause) {
        return DataProblem.of(cause).msg(code);
    }

    public final String getCode() {
        return code;
    }

    private final String code;
}
