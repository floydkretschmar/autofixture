package de.floydkretschmar.autofixture.junit;

import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.strategies.FixtureCreationStategy;
import de.floydkretschmar.autofixture.strategies.MultiStepFixtureCreationStrategy;
import de.floydkretschmar.autofixture.strategies.initialization.FieldInitializationStrategy;
import de.floydkretschmar.autofixture.strategies.initialization.InitializationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.BuilderInstantiationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.ConstructorInstantiationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategies;
import de.floydkretschmar.autofixture.utils.ConfigurationHelper;
import de.floydkretschmar.autofixture.utils.FieldHelper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;

public class AutofixtureExtension implements TestInstancePostProcessor {
    private static final InstantiationStrategies DEFAULT_INSTANTIATION_STRATEGIES = new InstantiationStrategies(Arrays.asList(new BuilderInstantiationStrategy(), new ConstructorInstantiationStrategy()));

    private static final InitializationStrategy DEFAULT_INITIALIZATION_STRATEGY = FieldInitializationStrategy.builder().instantiationStrategies(DEFAULT_INSTANTIATION_STRATEGIES).build();

    private static final FixtureCreationStategy DEFAULT_CREATION_STRATEGY = MultiStepFixtureCreationStrategy.builder().instantiationStrategies(DEFAULT_INSTANTIATION_STRATEGIES).initializationStrategy(DEFAULT_INITIALIZATION_STRATEGY).build();

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        final var declaredFixtureFields = Arrays.stream(testInstance.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autofixture.class)).toList();

        for (final var fixtureField : declaredFixtureFields) {
            final var fixtureType = fixtureField.getType();
            final var fixtureValues = ConfigurationHelper.readConfiguration("%s.properties".formatted(fixtureType.getSimpleName()));
            final var fixture = DEFAULT_CREATION_STRATEGY.createFixture(fixtureType, fixtureValues);
            FieldHelper.setField(fixtureField, testInstance, fixture);
        }
    }
}
