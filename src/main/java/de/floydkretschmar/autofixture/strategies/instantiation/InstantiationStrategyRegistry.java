package de.floydkretschmar.autofixture.strategies.instantiation;

import de.floydkretschmar.autofixture.FixtureCreationException;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

@Builder()
public class InstantiationStrategyRegistry {
    @Singular
    @NonNull
    private List<InstantiationStrategy> fallbackInstantiationStrategies;

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
