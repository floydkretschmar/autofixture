package de.floydkretschmar.autofixture.steps.creation;

import java.util.Optional;

public interface CreationStrategy {
    <T> T createInstance(Class<T> instanceClass);

    <T> Optional<T> tryCreateInstance(Class<T> instanceClass);
}
