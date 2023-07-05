package de.floydkretschmar.autofixture.strategies;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class BuilderInstantiationStrategy implements InstantiationStrategy {
    @Override
    public <T> Optional<T> createInstance(Class<T> instanceClass) {
        try {
            final var builderMethod = instanceClass.getDeclaredMethod("builder");
            final var builder = builderMethod.invoke(null);
            final var buildMethod = builder.getClass().getDeclaredMethod("build");
            final var instance = buildMethod.invoke(builder);
            return Optional.of((T)instance);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            return Optional.empty();
        }
    }
}
