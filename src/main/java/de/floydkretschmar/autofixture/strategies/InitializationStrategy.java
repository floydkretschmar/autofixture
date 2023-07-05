package de.floydkretschmar.autofixture.strategies;

import java.util.Properties;

public interface InitializationStrategy {
    <T> void initializeInstance(T instance, Properties instanceValues);
}
