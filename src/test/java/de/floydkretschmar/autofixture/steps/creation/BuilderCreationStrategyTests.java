package de.floydkretschmar.autofixture.steps.creation;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.steps.StepException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuilderCreationStrategyTests {
    @Test
    public void createInstance_whenCalledWithValidClass_returnsInstance() {
        final var creationStep = new BuilderCreationStrategy();

        final var instance = creationStep.createInstance(TestClass.class);

        assertThat(instance, notNullValue());
    }

    @ParameterizedTest
    @ValueSource(classes = {TestClassWithoutBuilder.class, TestClassWithInvalidBuilderMethod.class, TestClassWithInvalidBuilder.class, TestClassBuilderWithInvalidBuildMethod.class})
    public <T> void createInstance_whenCalledForInvalidClass_throwsStepException(Class<T> instanceClass) {
        final var creationStep = new BuilderCreationStrategy();

        final var exception = assertThrows(StepException.class, () -> creationStep.createInstance(instanceClass));
        exception.getMessage().contains(BuilderCreationStrategy.class.getSimpleName());
    }

    @Test
    public void tryCreateInstance_whenCalledWithValidClass_returnsInstance() {
        final var creationStep = new BuilderCreationStrategy();

        final var instance = creationStep.tryCreateInstance(TestClass.class);

        assertThat(instance.isPresent(), is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {TestClassWithoutBuilder.class, TestClassWithInvalidBuilderMethod.class, TestClassWithInvalidBuilder.class, TestClassBuilderWithInvalidBuildMethod.class})
    public <T> void tryCreateInstance_whenCalledForInvalidClass_returnsEmptyOptional(Class<T> instanceClass) {
        final var creationStep = new BuilderCreationStrategy();

        final var instanceOptional = creationStep.tryCreateInstance(instanceClass);
        assertThat(instanceOptional.isEmpty(), is(true));
    }

    static class TestClassWithoutBuilder {
    }

    static class TestClassWithInvalidBuilderMethod {
        public static TestClassBuilderWithoutBuildMethod builder(String invalidArgument) {
            return new TestClassBuilderWithoutBuildMethod();
        }
    }

    static class TestClassWithInvalidBuilder {
        public static TestClassBuilderWithoutBuildMethod builder() {
            return new TestClassBuilderWithoutBuildMethod();
        }
    }

    static class TestClassBuilderWithoutBuildMethod {
    }

    static class TestClassBuilderWithInvalidBuildMethod {
        public TestClassWithInvalidBuilder build() {
            return new TestClassWithInvalidBuilder();
        }
    }
}