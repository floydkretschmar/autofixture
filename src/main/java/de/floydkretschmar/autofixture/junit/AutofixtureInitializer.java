package de.floydkretschmar.autofixture.junit;

import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.FixtureFactory;
import de.floydkretschmar.autofixture.utils.ConfigurationHelper;
import de.floydkretschmar.autofixture.utils.FieldHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;

@RequiredArgsConstructor

public class AutofixtureInitializer implements TestInstancePostProcessor {

    private final FixtureFactory fixtureFactory;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        final var declaredFixtureFields = Arrays.stream(testInstance.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autofixture.class)).toList();

        for (final var fixtureField : declaredFixtureFields) {
            final var fixtureType = fixtureField.getType();
            final var fixtureValues = ConfigurationHelper.readConfiguration("%s.properties".formatted(fixtureType.getSimpleName()));
            final var fixture = fixtureFactory.createFixture(fixtureType, fixtureValues);
            FieldHelper.setField(fixtureField, testInstance, fixture);
        }
    }
}
