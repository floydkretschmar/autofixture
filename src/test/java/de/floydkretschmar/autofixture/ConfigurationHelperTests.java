package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.utils.ConfigurationHelper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationHelperTests {

    @Test
    public void readConfiguration_whenCalledWithValidFileName_shouldReturnLoadedProperties() {
        final var properties = ConfigurationHelper.readConfiguration("test.properties");

        assertThat(properties, notNullValue());
        assertThat(properties.getProperty("testProperty1"), equalTo("abc"));
        assertThat(properties.getProperty("testProperty2"), equalTo("999"));
        assertThat(properties.getProperty("testPropertyNotExists"), nullValue());
    }

    @Test
    public void readConfiguration_whenCalledWithNonExistentFileName_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> ConfigurationHelper.readConfiguration("doesNotExist.properties"));
    }
}
