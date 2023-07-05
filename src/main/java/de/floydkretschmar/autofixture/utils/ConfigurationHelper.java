package de.floydkretschmar.autofixture.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class ConfigurationHelper {
    public static Properties readConfiguration(String configurationFileName) {
        final var properties = new Properties();
        try (var inputStream = ConfigurationHelper.class.getClassLoader().getResourceAsStream(configurationFileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException(configurationFileName);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Configuration file %s could not be read: %s".formatted(configurationFileName, e.getMessage()), e);
        }
        return properties;
    }
}
