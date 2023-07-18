package de.floydkretschmar.autofixture.utils;

import lombok.Builder;
import lombok.Value;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ConfigurationLoader {

    @Value
    @Builder
    static class LineData {
        int whitespaces;
        LineDataType type;
    }

    static enum LineDataType {
        LIST_ITEM,
        OBJECT
    }

    private static final Pattern includeListItemPattern = Pattern.compile("^(?<whitespaces> *)((?<listPrefix>- )|(?<objectPrefix>(.*: )))!include (?<fileName>.*\\.(yaml|yml)).*");

    public static String readConfiguration(String configurationFile) {
        return readConfigurationAsList(configurationFile).stream().reduce("", (base, latest) -> base.isBlank() ? latest : "%s\r\n%s".formatted(base, latest));
    }

    private static List<String> readConfigurationAsList(String configurationFile) {
        final var configurationFileUrl = ConfigurationLoader.class.getClassLoader().getResource(configurationFile);
        if (configurationFileUrl == null)
            throw new ConfigurationReadException(configurationFile);
        try {
            final var linesOfOriginalConfiguration = Files.readAllLines(Path.of(configurationFileUrl.toURI()));
            final var linesOfComposedConfiguration = new ArrayList<String>();
            for (int i = 0; i < linesOfOriginalConfiguration.size(); i++) {
                final var line = linesOfOriginalConfiguration.get(i);
                var matcher = includeListItemPattern.matcher(line);
                if (matcher.matches()) {
                    final var includedFileName = matcher.group("fileName");
                    final var type = Objects.nonNull(matcher.group("listPrefix")) ? LineDataType.LIST_ITEM : LineDataType.OBJECT;

                    final var linesOfIncludedConfiguration = readConfigurationAsList(includedFileName);
                    if (type == LineDataType.OBJECT) {
                        final var whitespacesBeforeObjectName = matcher.group("whitespaces").length();
                        linesOfComposedConfiguration.add("%s%s".formatted(" ".repeat(whitespacesBeforeObjectName), matcher.group("objectPrefix").stripTrailing()));
                        linesOfIncludedConfiguration.forEach(includedConfigurationLine -> linesOfComposedConfiguration.add("%s%s".formatted(" ".repeat(whitespacesBeforeObjectName + 2), includedConfigurationLine)));
                    } else {
                        final var whitespacesBeforeHyphen = matcher.group("whitespaces").length();
                        final var whitespacesBeforeItemContent = whitespacesBeforeHyphen + 2;
                        linesOfComposedConfiguration.add("%s- %s".formatted(" ".repeat(whitespacesBeforeHyphen), linesOfIncludedConfiguration.get(0)));
                        linesOfIncludedConfiguration.subList(1, linesOfIncludedConfiguration.size()).forEach(includedConfigurationLine -> linesOfComposedConfiguration.add("%s%s".formatted(" ".repeat(whitespacesBeforeItemContent), includedConfigurationLine)));
                    }
                } else {
                    linesOfComposedConfiguration.add(line);
                }
            }
            return linesOfComposedConfiguration;
        } catch (IOException | OutOfMemoryError | SecurityException | IllegalArgumentException |
                 FileSystemNotFoundException | URISyntaxException e) {
            throw new ConfigurationReadException(configurationFile.toString(), e);
        }
    }


}
