package de.floydkretschmar.autofixture.strategies.initialization;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.common.TestClassNested;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategyRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldInitializationStrategyTest {

    @Mock
    private Properties properties;

    private FieldInitializationStrategy strategy;

    @Mock
    private InstantiationStrategyRegistry instantiationStrategyRegistry;

    @BeforeEach
    public void setUp() {
        strategy = new FieldInitializationStrategy(instantiationStrategyRegistry);
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
        when(instantiationStrategyRegistry.createInstance(TestClass.class)).thenReturn(TestClass.builder().build());
        when(properties.getProperty("stringProperty")).thenReturn("abc");
        when(properties.getProperty("nestedProperty.testProperty")).thenReturn("true");
        when(properties.getProperty("nestedProperty.testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClassNested.builder().stringProperty("abc").nestedProperty(TestClass.builder().testProperty(true).testNumberProperty(123).build()).build();

        final var actualFixture = TestClassNested.builder().build();
        strategy.initializeInstance(actualFixture, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }
}