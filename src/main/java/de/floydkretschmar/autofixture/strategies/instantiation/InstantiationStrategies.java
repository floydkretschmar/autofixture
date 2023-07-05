package de.floydkretschmar.autofixture.strategies.instantiation;

import de.floydkretschmar.autofixture.FixtureCreationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class InstantiationStrategies extends ArrayList<InstantiationStrategy> {

    public InstantiationStrategies(Collection<? extends InstantiationStrategy> strategies) {
        super(strategies);
    }

    public <T> T createInstance(Class<T> fixtureClass) {
        Optional<T> instance = Optional.empty();
        for (final var creationStrategy : this) {
            instance = creationStrategy.createInstance(fixtureClass);
            if (instance.isPresent()) break;
        }

        if (instance.isEmpty())
            throw new FixtureCreationException("None of the registered creation strategies was able to create an instance of %s".formatted(fixtureClass.getSimpleName()));

        return instance.get();
    }
}
