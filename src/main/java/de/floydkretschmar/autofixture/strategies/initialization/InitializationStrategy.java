package de.floydkretschmar.autofixture.strategies.initialization;

import java.util.Properties;

public interface InitializationStrategy {
    <T> void initializeInstance(T instance, Properties instanceValues);
}
