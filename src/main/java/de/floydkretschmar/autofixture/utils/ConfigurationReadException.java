package de.floydkretschmar.autofixture.utils;

public class ConfigurationReadException extends RuntimeException {
    public ConfigurationReadException(String configurationPath) {
        super("Autofixture configuration file %s could not be read".formatted(configurationPath));
    }

    public ConfigurationReadException(String configurationPath, Throwable cause) {
        super("Autofixture configuration file %s could not be read: %s".formatted(configurationPath, cause.getMessage()), cause);
    }
}
