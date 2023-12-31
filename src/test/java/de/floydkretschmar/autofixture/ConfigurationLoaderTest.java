package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.exceptions.ConfigurationReadException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationLoaderTest {

    @Test
    @SneakyThrows
    public void readConfiguration_whenCalledWithValidFileName_shouldReturnFileAsString() {
        final var configurationFile = ConfigurationLoaderTest.class.getClassLoader().getResource("configuration-loader/composedOrderWithListItems.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("configuration-loader/composedOrderWithListItems.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(configurationFile))));
    }

    @Test
    @SneakyThrows
    public void readConfiguration_whenCalledWithListItemAsIncludedConfiguration_shouldReturnComposedConfigurationAsString() {
        final var completeConfiguration = ConfigurationLoaderTest.class.getClassLoader().getResource("configuration-loader/composedOrderWithListItems.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("configuration-loader/orderWithIncludedFileAtListItemLevel.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(completeConfiguration))));
    }

    @Test
    @SneakyThrows
    public void readConfiguration_whenCalledWithObjectAsIncludedConfiguration_shouldReturnComposedConfigurationAsString() {
        final var completeConfiguration = ConfigurationLoaderTest.class.getClassLoader().getResource("configuration-loader/composedOrderWithObjects.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("configuration-loader/orderWithIncludedFileAtObjectLevel.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(completeConfiguration))));
    }

    @Test
    public void readConfiguration_whenCalledWithNonExistentFileName_shouldThrowConfigurationReadException() {
        assertThrows(ConfigurationReadException.class, () -> ConfigurationLoader.readConfiguration("doesnotexist.yaml"));
    }
}
