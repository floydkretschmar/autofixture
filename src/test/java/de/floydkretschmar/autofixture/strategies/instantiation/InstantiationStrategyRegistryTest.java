package de.floydkretschmar.autofixture.strategies.instantiation;

import de.floydkretschmar.autofixture.FixtureCreationException;
import de.floydkretschmar.autofixture.common.TestClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InstantiationStrategyRegistryTest {
    @Mock
    private InstantiationStrategy instantiationStrategy;
    @Test
    public void createInstancee_WhenAnyCreationStrategySuccessful_ShouldCreateInstance() {
        final var expectedFixture = TestClass.builder().build();
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(expectedFixture));
        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        final var registry = InstantiationStrategyRegistry.builder(List.of(failingCreationStrategy, instantiationStrategy)).build();

        final var actualFixture = registry.createInstance(TestClass.class);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenAllCreationStrategiesUnsuccessful_ShouldThrowFixtureCreationException() {
        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        final var registry = InstantiationStrategyRegistry.builder(List.of(failingCreationStrategy)).build();

        final var exception = assertThrows(FixtureCreationException.class, () -> registry.createInstance(TestClass.class));
        assertThat(exception.getMessage(), containsString("None of the registered creation strategies"));
    }

    @Test
    public void createFixture_WhenNoCreationStrategiesRegistered_ShouldThrowIllegalArgumentException() {
        final var exception = assertThrows(IllegalArgumentException.class, () -> InstantiationStrategyRegistry.builder(List.of()));
        assertThat(exception.getMessage(), equalTo("At least one fallback instantiation strategy has to be registered"));
    }
}