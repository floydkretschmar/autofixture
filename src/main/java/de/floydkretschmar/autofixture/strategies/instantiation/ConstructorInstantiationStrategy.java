package de.floydkretschmar.autofixture.strategies.instantiation;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class ConstructorInstantiationStrategy implements InstantiationStrategy {
    @Override
    public <T> Optional<T> createInstance(Class<T> instanceClass) {
        try {
            final var optionalConstructor = Arrays.stream(instanceClass.getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findFirst();
            if (optionalConstructor.isEmpty()) return Optional.empty();

            final var instance = optionalConstructor.get().newInstance();
            return Optional.of((T) instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            return Optional.empty();
        }
    }
}
