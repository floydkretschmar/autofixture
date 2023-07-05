package de.floydkretschmar.autofixture.strategies.instantiation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ConstructorInstantiationStrategyTest {
    @Test
    public void createInstance_whenCalledWithValidClass_returnsInstance() {
        final var instantiationStrategy = new ConstructorInstantiationStrategy();

        final var instance = instantiationStrategy.createInstance(TestClassWithPublicZeroArgsConstructor.class);

        assertThat(instance.isPresent(), is(true));
    }

    @ParameterizedTest
    @ValueSource(classes = {TestClassWithoutZeroArgsConstructor.class, TestClassWithoutPublicZeroArgsConstructor.class})
    public <T> void createInstance_whenCalledForInvalidClass_returnsEmptyOptional(Class<T> instanceClass) {
        final var instantiationStrategy = new ConstructorInstantiationStrategy();

        final var instanceOptional = instantiationStrategy.createInstance(instanceClass);
        assertThat(instanceOptional.isEmpty(), is(true));
    }

    static class TestClassWithPublicZeroArgsConstructor {
        public TestClassWithPublicZeroArgsConstructor() {

        }
    }

    static class TestClassWithoutPublicZeroArgsConstructor {
        private TestClassWithoutPublicZeroArgsConstructor() {

        }
    }

    static class TestClassWithoutZeroArgsConstructor {
        public TestClassWithoutZeroArgsConstructor(String argument) {

        }
    }
}