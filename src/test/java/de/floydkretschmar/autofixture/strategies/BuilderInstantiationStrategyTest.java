package de.floydkretschmar.autofixture.strategies;

import de.floydkretschmar.autofixture.common.TestClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BuilderInstantiationStrategyTest {

    @Test
    public void createInstance_whenCalledWithValidClass_returnsInstance() {
        final var instantiationStrategy = new BuilderInstantiationStrategy();

        final var instance = instantiationStrategy.createInstance(TestClass.class);

        assertThat(instance.isPresent(), is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {TestClassWithoutBuilder.class, TestClassWithInvalidBuilderMethod.class, TestClassWithInvalidBuilder.class, TestClassBuilderWithInvalidBuildMethod.class})
    public <T> void createInstance_whenCalledForInvalidClass_returnsEmptyOptional(Class<T> instanceClass) {
        final var instantiationStrategy = new BuilderInstantiationStrategy();

        final var instanceOptional = instantiationStrategy.createInstance(instanceClass);
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