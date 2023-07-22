package de.floydkretschmar.autofixture.junit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.FixtureCreationException;
import de.floydkretschmar.autofixture.utils.ConfigurationLoader;
import de.floydkretschmar.autofixture.utils.FieldHelper;
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
            final var configurationFile = "%s.yaml".formatted(fixtureType.getSimpleName().toLowerCase());
            final var fixtureConfiguration = ConfigurationLoader.readConfiguration(configurationFile);

            final Object fixture;
            try {
                fixture = objectMapper.readValue(fixtureConfiguration, fixtureType);
            } catch (JsonProcessingException e) {
                throw new FixtureCreationException("Could not create autofixture for field %s because configuration file %s could not be parsed.".formatted(fixtureField.getName(), configurationFile), e);
            }
            FieldHelper.setField(fixtureField, testInstance, fixture);
        }
    }
}
