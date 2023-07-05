package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.common.TestClassNested;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FixtureFactoryTest {

    @Mock private Properties properties;

    @Mock private InstantiationStrategy instantiationStrategy;

    private FixtureFactory factory;

    @BeforeEach
    public void setUp() {
        factory = FixtureFactory
                .builder()
                .instantiationStrategy(instantiationStrategy)
                .build();
    }

    @Test
    public void createFixture_WhenCalled_ShouldCreateFixtureWithSpecifiedValues() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(TestClass.builder().build()));
        when(properties.getProperty("testProperty")).thenReturn("true");
        when(properties.getProperty("testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClass.builder().testProperty(true).testNumberProperty(123).build();


        final var actualFixture = factory.createFixture(TestClass.class, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenPropertyMissingInConfiguration_ShouldSkipMissingValues() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(TestClass.builder().build()));
        when(properties.getProperty("testProperty")).thenReturn("true");
        when(properties.getProperty("testNumberProperty")).thenReturn(null);
        final var expectedFixture = TestClass.builder().testProperty(true).build();

        final var actualFixture = factory.createFixture(TestClass.class, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenFieldIsComplexType_ShouldCreateFixtureRecursively() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(TestClass.builder().build()));
        when(instantiationStrategy.createInstance(TestClassNested.class)).thenReturn(Optional.of(TestClassNested.builder().build()));
        when(properties.getProperty("stringProperty")).thenReturn("abc");
        when(properties.getProperty("nestedProperty.testProperty")).thenReturn("true");
        when(properties.getProperty("nestedProperty.testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClassNested.builder().stringProperty("abc").nestedProperty(TestClass.builder().testProperty(true).testNumberProperty(123).build()).build();

        final var actualFixture = factory.createFixture(TestClassNested.class, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenAnyCreationStrategySuccessful_ShouldCreateFixtureWithSpecifiedValues() {
        when(instantiationStrategy.createInstance(TestClass.class)).thenReturn(Optional.of(TestClass.builder().build()));
        when(properties.getProperty("testProperty")).thenReturn("true");
        when(properties.getProperty("testNumberProperty")).thenReturn("123");
        final var expectedFixture = TestClass.builder().testProperty(true).testNumberProperty(123).build();

        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        factory = factory.toBuilder()
                .clearInstantiationStrategies()
                .instantiationStrategy(failingCreationStrategy)
                .instantiationStrategy(instantiationStrategy)
                .build();
        final var actualFixture = factory.createFixture(TestClass.class, properties);

        assertThat(actualFixture, equalTo(expectedFixture));
    }

    @Test
    public void createFixture_WhenAllCreationStrategiesUnsuccessful_ShouldThrowFixtureCreationException() {
        final var failingCreationStrategy = mock(InstantiationStrategy.class);
        when(failingCreationStrategy.createInstance(any())).thenReturn(Optional.empty());
        factory = factory.toBuilder()
                .clearInstantiationStrategies()
                .instantiationStrategy(failingCreationStrategy)
                .build();

        final var exception = assertThrows(FixtureCreationException.class, () -> factory.createFixture(TestClass.class, properties));
        assertThat(exception.getMessage(), containsString("None of the registered creation strategies"));
    }

    @Test
    public void createFixture_WhenNoCreationStrategiesRegistered_ShouldThrowFixtureCreationException() {
        factory = factory.toBuilder()
                .clearInstantiationStrategies()
                .build();

        final var exception = assertThrows(FixtureCreationException.class, () -> factory.createFixture(TestClass.class, properties));
        assertThat(exception.getMessage(), equalTo("No creation strategies were registered"));
    }
}
