package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.common.TestClass;
import de.floydkretschmar.autofixture.strategies.FixtureCreationStategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FixtureFactoryTest {

    @Mock
    private Properties properties;

    @Mock
    private FixtureCreationStategy defaultFixtureCreationStrategy;

    private FixtureFactory factory;

    @BeforeEach
    public void setUp() {
        factory = FixtureFactory
                .builder()
                .defaultFixtureCreationStrategy(defaultFixtureCreationStrategy)
                .build();
    }

    @Test
    public void createFixture_WhenCalled_ShouldDeferToDefaultFixtureCreationStrategy() {
        final var expectedFixture = TestClass.builder().testProperty(true).testNumberProperty(123).build();
        when(defaultFixtureCreationStrategy.createFixture(any(), any())).thenReturn(expectedFixture);

        final var actualFixture = factory.createFixture(TestClass.class, properties);

        verify(defaultFixtureCreationStrategy).createFixture(TestClass.class, properties);
        assertThat(actualFixture, equalTo(expectedFixture));
    }
}
