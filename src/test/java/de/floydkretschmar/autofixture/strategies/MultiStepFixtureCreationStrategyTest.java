package de.floydkretschmar.autofixture.strategies;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.strategies.initialization.InitializationStrategy;
import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategyRegistry;
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
    private InstantiationStrategyRegistry registry;

    @Mock
    private Properties fixtureValues;

    @Test
    public void createFixture_WhenCalled_ShouldCreateAndInitializeInstance() {
        final var expectedInstance = TestClass.builder().testProperty(true).testNumberProperty(123).build();
        when(registry.createInstance(any())).thenReturn(expectedInstance);
        final var strategy = MultiStepFixtureCreationStrategy.builder().initializationStrategy(initializationStrategy).instantiationStrategyRegistry(registry).build();

        strategy.createFixture(TestClass.class, fixtureValues);

        verify(registry).createInstance(TestClass.class);
        verify(initializationStrategy).initializeInstance(expectedInstance, fixtureValues);
    }
}