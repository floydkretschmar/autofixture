package de.floydkretschmar.autofixture.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class ConfigurationHelper {
    public static Properties readConfiguration(String configurationFileName) {
        return readConfiguration(configurationFileName, "");
    }
    private static Properties readConfiguration(String configurationFileName, String qualifiedFieldName) {
        final var properties = new Properties();
        try (var inputStream = ConfigurationHelper.class.getClassLoader().getResourceAsStream(configurationFileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException(configurationFileName);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Configuration file %s could not be read: %s".formatted(configurationFileName, e.getMessage()), e);
        }

        for (final var keyValuePair: properties.entrySet()) {
            final var value = keyValuePair.getValue();
            final var key = keyValuePair.getKey();
            final var newQualifiedFieldName = FieldHelper.appendToQualifiedFieldName(qualifiedFieldName, (String)key);

            if (value instanceof String && ((String)value).contains(".properties")) {
                final var nestedProperties = readConfiguration((String)value, newQualifiedFieldName);
                properties.putAll(nestedProperties);
                properties.remove(key);
            }
            else if (!qualifiedFieldName.isBlank()) {
                properties.put(newQualifiedFieldName, value);
                properties.remove(key);
            }
        }

        return properties;
    }
}
