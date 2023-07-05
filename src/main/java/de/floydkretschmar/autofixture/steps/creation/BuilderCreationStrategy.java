package de.floydkretschmar.autofixture.steps.creation;

import de.floydkretschmar.autofixture.steps.StepException;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class BuilderCreationStrategy implements CreationStrategy {
    @Override
    public <T> T createInstance(Class<T> instanceClass) {
        try {
            final var builderMethod = instanceClass.getDeclaredMethod("builder");
            final var builder = builderMethod.invoke(null);
            final var buildMethod = builder.getClass().getDeclaredMethod("build");
            final var instance = buildMethod.invoke(builder);
            return (T)instance;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            throw new StepException(this.getClass(), "Unable to build fixture for class %s using the builder pattern.".formatted(instanceClass.getSimpleName()), exception);
        }
    }

    @Override
    public <T> Optional<T> tryCreateInstance(Class<T> instanceClass) {
        try {
            return Optional.of(createInstance(instanceClass));
        } catch (StepException ex) {
            return Optional.empty();
        }
    }
}
