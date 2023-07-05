package de.floydkretschmar.autofixture.junit;

import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.strategies.FixtureCreationStategy;
import de.floydkretschmar.autofixture.strategies.MultiStepFixtureCreationStrategy;
import de.floydkretschmar.autofixture.strategies.initialization.FieldInitializationStrategy;
import de.floydkretschmar.autofixture.strategies.initialization.InitializationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.BuilderInstantiationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.ConstructorInstantiationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategyRegistry;
import de.floydkretschmar.autofixture.utils.ConfigurationHelper;
import de.floydkretschmar.autofixture.utils.FieldHelper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;
import java.util.stream.Stream;

public class AutofixtureExtension implements TestInstancePostProcessor {
    private static final InstantiationStrategy[] DEFAULT_INSTANTIATION_STRATEGIES = {
            new BuilderInstantiationStrategy(),
            new ConstructorInstantiationStrategy()
    };

    private final FixtureCreationStategy defaultFixtureCreationStrategy;

    public AutofixtureExtension() {
        defaultFixtureCreationStrategy = initializeDefaultFixtureCreationStrategy();
    }

    private FixtureCreationStategy initializeDefaultFixtureCreationStrategy() {
        final FixtureCreationStategy defaultFixtureCreationStrategy;
        final var instantiationStrategies = Stream.concat(Arrays.stream(DEFAULT_INSTANTIATION_STRATEGIES), Arrays.stream(defineCustomInstantiationStrategies())).toList();
        final var instantiationStrategyRegistry = InstantiationStrategyRegistry.builder().instantiationStrategies(instantiationStrategies).build();
        var initializationStrategy = defineCustomInitializationStrategy();
        if (initializationStrategy == null)
            initializationStrategy = FieldInitializationStrategy.builder().instantiationStrategyRegistry(instantiationStrategyRegistry).build();

        defaultFixtureCreationStrategy = MultiStepFixtureCreationStrategy.builder().instantiationStrategyRegistry(instantiationStrategyRegistry).initializationStrategy(initializationStrategy).build();
        return defaultFixtureCreationStrategy;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        final var declaredFixtureFields = Arrays.stream(testInstance.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autofixture.class)).toList();

        for (final var fixtureField : declaredFixtureFields) {
            final var fixtureType = fixtureField.getType();
            final var fixtureValues = ConfigurationHelper.readConfiguration("%s.properties".formatted(fixtureType.getSimpleName()));
            final var fixture = defaultFixtureCreationStrategy.createFixture(fixtureType, fixtureValues);
            FieldHelper.setField(fixtureField, testInstance, fixture);
        }
    }

    protected InstantiationStrategy[] defineCustomInstantiationStrategies() {
        return new InstantiationStrategy[0];
    }

    protected InitializationStrategy defineCustomInitializationStrategy() {
        return null;
    }
}
