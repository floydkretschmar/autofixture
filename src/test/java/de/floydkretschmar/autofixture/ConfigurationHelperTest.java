package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.utils.ConfigurationHelper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationHelperTest {

    @Test
    public void readConfiguration_whenCalledWithValidFileName_shouldReturnLoadedProperties() {
        final var properties = ConfigurationHelper.readConfiguration("test.properties");

        assertThat(properties, notNullValue());
        assertThat(properties.getProperty("testProperty1"), equalTo("abc"));
        assertThat(properties.getProperty("testProperty2"), equalTo("999"));
        assertThat(properties.getProperty("testPropertyNotExists"), nullValue());
    }

    @Test
    public void readConfiguration_whenCalledWithNestedProperties_shouldReturnLoadedAndFlattened() {
        final var properties = ConfigurationHelper.readConfiguration("testNested.properties");

        assertThat(properties, notNullValue());
        assertThat(properties.getProperty("testProperty1"), equalTo("cde"));
        assertThat(properties.getProperty("testProperty2"), equalTo("123"));
        assertThat(properties.getProperty("testProperty3.testNestedProperty"), equalTo("fgh"));
        assertThat(properties.getProperty("testProperty3.testNestedProperty2.testDeepNestedProperty"), equalTo("12345"));
        assertThat(properties.getProperty("testProperty3"), nullValue());
        assertThat(properties.getProperty("testProperty3.testNestedProperty2"), nullValue());
    }

    @Test
    public void readConfiguration_whenCalledWithNonExistentFileName_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> ConfigurationHelper.readConfiguration("doesNotExist.properties"));
    }
}
