package de.floydkretschmar.autofixture.strategies;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.strategies.initialization.InitializationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiStepFixtureCreationStrategyTest {

    @Mock
    private InitializationStrategy initializationStrategy;

    @Mock
    private InstantiationStrategies strategies;

    @Mock
    private Properties fixtureValues;

    @Test
    public void createFixture_WhenCalled_ShouldCreateAndInitializeInstance() {
        final var expectedInstance = TestClass.builder().testProperty(true).testNumberProperty(123).build();
        when(strategies.createInstance(any())).thenReturn(expectedInstance);
        final var strategy = MultiStepFixtureCreationStrategy.builder().initializationStrategy(initializationStrategy).instantiationStrategies(strategies).build();

        strategy.createFixture(TestClass.class, fixtureValues);

        verify(strategies).createInstance(TestClass.class);
        verify(initializationStrategy).initializeInstance(expectedInstance, fixtureValues);
    }
}