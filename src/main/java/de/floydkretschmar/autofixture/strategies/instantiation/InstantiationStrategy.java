package de.floydkretschmar.autofixture.strategies.instantiation;

import java.util.Optional;

public interface InstantiationStrategy {
    <T> Optional<T> createInstance(Class<T> instanceClass);
}
