package de.floydkretschmar.autofixture.exceptions;

public class FixtureCreationException extends RuntimeException {
    public FixtureCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
