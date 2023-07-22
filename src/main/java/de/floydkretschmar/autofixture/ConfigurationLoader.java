package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.exceptions.ConfigurationReadException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ConfigurationLoader {
    private static final Pattern includePattern = Pattern.compile("^(?<whitespaces> *)((?<listPrefix>- )|(?<objectPrefix>(.*: )))!include (?<fileName>.*\\.ya?ml)");

    public static String readConfiguration(String configurationFile) {
        return readConfigurationAsList(configurationFile).reduce("", (base, latest) -> base.isBlank() ? latest : "%s\r\n%s".formatted(base, latest));
    }

    private static Stream<String> readConfigurationAsList(String configurationFile) {
        final var configurationFileUrl = ConfigurationLoader.class.getClassLoader().getResource(configurationFile);
        if (configurationFileUrl == null)
            throw new ConfigurationReadException(configurationFile);
        try {
            final var linesOfOriginalConfiguration = Files.readAllLines(Path.of(configurationFileUrl.toURI()));

            return linesOfOriginalConfiguration.stream().flatMap(line -> {
                var matcher = includePattern.matcher(line);
                return matcher.find() ? loadAndProcessSubConfiguration(matcher) : Stream.of(line);
            });
        } catch (IOException | OutOfMemoryError | SecurityException | IllegalArgumentException |
                 FileSystemNotFoundException | URISyntaxException e) {
            throw new ConfigurationReadException(configurationFile, e);
        }
    }

    private static Stream<String> loadAndProcessSubConfiguration(Matcher matcher) {
        final var includedFileName = matcher.group("fileName");
        final var linesOfIncludedConfiguration = readConfigurationAsList(includedFileName);
        if (Objects.nonNull(matcher.group("objectPrefix"))) {
            final var whitespacesBeforeObjectName = matcher.group("whitespaces").length();
            return Stream.concat(Stream.of("%s%s".formatted(" ".repeat(whitespacesBeforeObjectName), matcher.group("objectPrefix").stripTrailing())),
                    linesOfIncludedConfiguration.map(includedConfigurationLine -> "%s%s".formatted(" ".repeat(whitespacesBeforeObjectName + 2), includedConfigurationLine)));
        } else {
            final var whitespacesBeforeHyphen = matcher.group("whitespaces").length();
            final var whitespacesBeforeItemContent = whitespacesBeforeHyphen + 2;
            final var linesAsList = linesOfIncludedConfiguration.toList();
            return Stream.concat(Stream.of("%s- %s".formatted(" ".repeat(whitespacesBeforeHyphen), linesAsList.get(0))),
                    linesAsList.stream().skip(1).map(includedConfigurationLine -> "%s%s".formatted(" ".repeat(whitespacesBeforeItemContent), includedConfigurationLine)));
        }
    }
}
