package de.floydkretschmar.autofixture.strategies;

import de.floydkretschmar.autofixture.strategies.initialization.InitializationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategies;
import lombok.Builder;
import lombok.NonNull;

import java.util.Properties;

@Builder
public class MultiStepFixtureCreationStrategy implements FixtureCreationStategy {
    @NonNull
    private final InstantiationStrategies instantiationStrategies;

    @NonNull
    private final InitializationStrategy initializationStrategy;

    @Override
    public <T> T createFixture(Class<T> fixtureClass, Properties fixtureValues) {
        final var instance = instantiationStrategies.createInstance(fixtureClass);
        initializationStrategy.initializeInstance(instance, fixtureValues);
        return instance;
    }
}
