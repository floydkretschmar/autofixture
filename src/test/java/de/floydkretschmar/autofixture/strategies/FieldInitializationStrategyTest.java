package de.floydkretschmar.autofixture.strategies;

import de.floydkretschmar.autofixture.FixtureCreationException;
import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.common.TestClassNested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldInitializationStrategyTest {

    @Mock
    private Properties properties;

    private FieldInitializationStrategy strategy;

    @Mock
    private Function<Class<?>, InstantiationStrategy> instantiationStrategySelector;

    @Mock
    private InstantiationStrategy instantiationStrategy;

    @BeforeEach
    public void setUp() {
        strategy = new FieldInitializationStrategy(instantiationStrategySelector);
    }
    @Test
    public void initializeInstance_WhenCalled_ShouldInitializeFixtureWithSpecifiedValues() {
        when(properties.getProperty("testProperty")).thenReturn("true");
        when(properties.getProperty("testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClass.builder().testProperty(true).testNumberProperty(123).build();

        final var actualFixture = TestClass.builder().build();
        strategy.initializeInstance(actualFixture, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenPropertyMissingInConfiguration_ShouldSkipMissingValues() {
        when(properties.getProperty("testProperty")).thenReturn("true");
        when(properties.getProperty("testNumberProperty")).thenReturn(null);
        final var expectedFixture = TestClass.builder().testProperty(true).build();

        final var actualFixture = TestClass.builder().build();
        strategy.initializeInstance(actualFixture, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenFieldIsComplexType_ShouldCreateFixtureRecursively() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(TestClass.builder().build()));
        when(instantiationStrategySelector.apply(TestClass.class)).thenReturn(instantiationStrategy);
        when(properties.getProperty("stringProperty")).thenReturn("abc");
        when(properties.getProperty("nestedProperty.testProperty")).thenReturn("true");
        when(properties.getProperty("nestedProperty.testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClassNested.builder().stringProperty("abc").nestedProperty(TestClass.builder().testProperty(true).testNumberProperty(123).build()).build();

        final var actualFixture = TestClassNested.builder().build();
        strategy.initializeInstance(actualFixture, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenFieldIsComplexTypeAndInstantiationFails_ShouldThrowFixtureCreationException() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.empty());
        when(instantiationStrategySelector.apply(TestClass.class)).thenReturn(instantiationStrategy);
        when(properties.getProperty("stringProperty")).thenReturn("abc");

        final var actualFixture = TestClassNested.builder().build();

        assertThrows(FixtureCreationException.class, () -> strategy.initializeInstance(actualFixture, properties));
    }
}