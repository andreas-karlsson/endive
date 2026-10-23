package run.endive.wasm.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CodeSection extends Section {
    // A null entry is a body dropped by dropFunctionBody.
    private final FunctionBody[] functionBodies;
    private final boolean requiresDataCount;

    private CodeSection(List<FunctionBody> functionBodies, boolean requiresDataCount) {
        super(SectionId.CODE);
        this.functionBodies = functionBodies.toArray(new FunctionBody[0]);
        this.requiresDataCount = requiresDataCount;
    }

    /** Returns all function bodies, with {@code null} for dropped ones. */
    public FunctionBody[] functionBodies() {
        return functionBodies.clone();
    }

    public int functionBodyCount() {
        return functionBodies.length;
    }

    /**
     * @throws IllegalStateException if the function body has been dropped
     */
    public FunctionBody getFunctionBody(int idx) {
        var body = functionBodies[idx];
        if (body == null) {
            throw new IllegalStateException("Function body " + idx + " has been dropped");
        }
        return body;
    }

    /** Drops the function body at {@code idx}, see {@link run.endive.wasm.WasmModule#dropFunction(int)}. */
    public void dropFunctionBody(int idx) {
        functionBodies[idx] = null;
    }

    public boolean isRequiresDataCount() {
        return requiresDataCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<FunctionBody> functionBodies = new ArrayList<>();
        private boolean requiresDataCount;

        private Builder() {}

        /**
         * Add a function body to this section.
         *
         * @param functionBody the function body to add to this section (must not be {@code null})
         * @return the Builder
         */
        public Builder addFunctionBody(FunctionBody functionBody) {
            Objects.requireNonNull(functionBody, "functionBody");
            functionBodies.add(functionBody);
            return this;
        }

        public Builder setRequiresDataCount(boolean requiresDataCount) {
            this.requiresDataCount = requiresDataCount;
            return this;
        }

        public CodeSection build() {
            return new CodeSection(functionBodies, requiresDataCount);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeSection)) {
            return false;
        }
        CodeSection that = (CodeSection) o;
        return requiresDataCount == that.requiresDataCount
                && Arrays.equals(functionBodies, that.functionBodies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(functionBodies), requiresDataCount);
    }
}
