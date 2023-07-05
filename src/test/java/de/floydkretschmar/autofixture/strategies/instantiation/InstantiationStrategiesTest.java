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
class InstantiationStrategiesTest {
    @Mock
    private InstantiationStrategy instantiationStrategy;

    @Test
    public void createInstancee_WhenAnyCreationStrategySuccessful_ShouldCreateInstance() {
        final var expectedFixture = TestClass.builder().build();
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(expectedFixture));
        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        final var strategies = new InstantiationStrategies(List.of(failingCreationStrategy, instantiationStrategy));

        final var actualFixture = strategies.createInstance(TestClass.class);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenAllCreationStrategiesUnsuccessful_ShouldThrowFixtureCreationException() {
        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        final var strategies = new InstantiationStrategies(List.of(failingCreationStrategy));

        final var exception = assertThrows(FixtureCreationException.class, () -> strategies.createInstance(TestClass.class));
        assertThat(exception.getMessage(), containsString("None of the registered creation strategies"));
    }
}