package de.floydkretschmar.autofixture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.floydkretschmar.autofixture.exceptions.FixtureCreationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;

public class AutofixtureExtension implements TestInstancePostProcessor {

    private final ObjectMapper objectMapper;

    public AutofixtureExtension() {
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        final var declaredFixtureFields = Arrays.stream(testInstance.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autofixture.class)).toList();

        for (final var fixtureField : declaredFixtureFields) {
            final var fixtureType = fixtureField.getType();
            var className = fixtureType.getSimpleName();
            className = className.substring(0, 1).toLowerCase() + className.substring(1);
            final var configurationFile = "%s.yaml".formatted(className);
            final var fixtureConfiguration = ConfigurationLoader.readConfiguration(configurationFile);

            final Object fixture;
            try {
                fixture = objectMapper.readValue(fixtureConfiguration, fixtureType);
            } catch (JsonProcessingException e) {
                throw new FixtureCreationException("Could not create autofixture for field %s because configuration file %s could not be parsed.".formatted(fixtureField.getName(), configurationFile), e);
            }

            try {
                FieldHelper.setField(fixtureField, testInstance, fixture);
            } catch (Exception e) {
                throw new FixtureCreationException("Could not create autofixture for field %s because the instance of autofixture could not be assigned to the field.", e);
            }
        }
    }
}
