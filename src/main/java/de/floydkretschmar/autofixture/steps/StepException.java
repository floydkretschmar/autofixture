package de.floydkretschmar.autofixture.steps;

public class StepException extends RuntimeException {
    public StepException(Class<?> step, String message, Throwable cause) {
        super("Error in step %s: %s".formatted(step.getSimpleName(), message), cause);
    }
}
