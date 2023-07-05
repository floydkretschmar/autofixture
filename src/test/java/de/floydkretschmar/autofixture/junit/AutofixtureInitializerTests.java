package de.floydkretschmar.autofixture.junit;

import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.FixtureFactory;
import de.floydkretschmar.autofixture.common.TestClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutofixtureInitializerTests {

    @Mock private FixtureFactory fixtureFactory;

    @Mock private ExtensionContext extensionContext;
    @Captor
    ArgumentCaptor<Properties> fixtureValuesCaptor;

    @Test
    public void postProcessTestInstance_WhenTestInstanceHasAutofixtureAnnotations_shouldInitializeFixtures() {
        when(fixtureFactory.createFixture(any(), any())).thenReturn(TestClass.builder().build());
        final var initializer = new AutofixtureInitializer(fixtureFactory);

        initializer.postProcessTestInstance(new TestInstance(), extensionContext);

        verify(fixtureFactory).createFixture(any(TestClass.class.getClass()), fixtureValuesCaptor.capture());
        final var actualFixtureValues = fixtureValuesCaptor.getValue();

        assertThat(actualFixtureValues, notNullValue());
        assertThat(actualFixtureValues.getProperty("testProperty"), equalTo("true"));
    }

    @Test
    public void postProcessTestInstance_WhenTestInstanceHasNoAutofixtureAnnotations_shouldDoNothing() {
        final var initializer = new AutofixtureInitializer(fixtureFactory);

        initializer.postProcessTestInstance(new TestInstanceWithoutAnnotation(), extensionContext);

        verifyNoInteractions(fixtureFactory);
    }

    class TestInstance {

        @Autofixture
        private TestClass testFixture;
    }

    class TestInstanceWithoutAnnotation {

        private TestClass testFixture;
    }
}
