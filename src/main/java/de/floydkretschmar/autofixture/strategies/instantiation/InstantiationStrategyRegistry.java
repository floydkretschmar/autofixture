package de.floydkretschmar.autofixture.strategies.instantiation;

import de.floydkretschmar.autofixture.FixtureCreationException;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

@Builder(builderMethodName = "")
public class InstantiationStrategyRegistry {
    @Singular
    private List<InstantiationStrategy> fallbackInstantiationStrategies;

    public static InstantiationStrategyRegistryBuilder builder(List<InstantiationStrategy> fallbackInstantiationStrategies) {
        if (fallbackInstantiationStrategies == null || fallbackInstantiationStrategies.isEmpty())
            throw new IllegalArgumentException("At least one fallback instantiation strategy has to be registered");

        return new InstantiationStrategyRegistryBuilder().fallbackInstantiationStrategies(fallbackInstantiationStrategies);
    }

    public  <T> T createInstance(Class<T> fixtureClass) {
        Optional<T> instance = Optional.empty();
        for (final var creationStrategy : fallbackInstantiationStrategies) {
            instance = creationStrategy.createInstance(fixtureClass);
            if (instance.isPresent()) break;
        }

        if (instance.isEmpty())
            throw new FixtureCreationException("None of the registered creation strategies was able to create an instance of %s".formatted(fixtureClass.getSimpleName()));

        return instance.get();
    }
}
