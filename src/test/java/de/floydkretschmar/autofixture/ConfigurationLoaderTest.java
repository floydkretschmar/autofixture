package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.utils.ConfigurationLoader;
import de.floydkretschmar.autofixture.utils.ConfigurationReadException;
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
        final var configurationFile = ConfigurationLoaderTest.class.getClassLoader().getResource("order.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("order.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(configurationFile))));
    }

    @Test
    @SneakyThrows
    public void readConfiguration_whenCalledWithListItemAsIncludedConfiguration_shouldReturnComposedConfigurationAsString() {
        final var completeConfiguration = ConfigurationLoaderTest.class.getClassLoader().getResource("order.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("orderWithIncludedFileAtListItemLevel.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(completeConfiguration))));
    }


    @Test
    @SneakyThrows
    public void readConfiguration_whenCalledWithObjectAsIncludedConfiguration_shouldReturnComposedConfigurationAsString() {
        final var completeConfiguration = ConfigurationLoaderTest.class.getClassLoader().getResource("orderWithAddress.yaml").toURI();
        final var actualOrderConfiguration = ConfigurationLoader.readConfiguration("orderWithIncludedFileAtObjectLevel.yaml");

        assertThat(actualOrderConfiguration, equalTo(Files.readString(Path.of(completeConfiguration))));
    }

    @Test
    public void readConfiguration_whenCalledWithNonExistentFileName_shouldThrowConfigurationReadException() {
        assertThrows(ConfigurationReadException.class, () -> ConfigurationLoader.readConfiguration("doesnotexist.yaml"));
    }
}
