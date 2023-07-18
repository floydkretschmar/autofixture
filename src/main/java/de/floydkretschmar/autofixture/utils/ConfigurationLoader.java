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
import java.util.regex.Pattern;

public final class ConfigurationLoader {

    @Value
    @Builder
    static class LineData {
        int indentationCount;
        LineDataType type;
    }

    static enum LineDataType {
        LIST_ITEM,
        OBJECT
    }

    private static final Pattern includeListItemPattern = Pattern.compile("^(?<startingCharacters> *- )!include (?<fileName>.*\\.(yaml|yml)).*");

    public static String readConfiguration(String configurationFile) {
        return readConfiguration(configurationFile, LineData.builder().indentationCount(0).build()).stream().reduce("", (base, latest) -> base.isBlank() ? latest : "%s\r\n%s".formatted(base, latest));
    }

    public static List<String> readConfiguration(String configurationFile, LineData lineData) {
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
                    final var linesOfIncludedConfiguration = readConfiguration(includedFileName, LineData.builder().type(LineDataType.LIST_ITEM).indentationCount(matcher.group("startingCharacters").length()).build());
                    linesOfComposedConfiguration.addAll(linesOfIncludedConfiguration);
                } else {
                    final var prefix = i == 0 && lineData.type == LineDataType.LIST_ITEM ? "%s%s".formatted(" ".repeat(lineData.indentationCount - 2), "- ") : " ".repeat(lineData.indentationCount);
                    linesOfComposedConfiguration.add("%s%s".formatted(prefix, line));
                }
            }
            return linesOfComposedConfiguration;
        } catch (IOException | OutOfMemoryError | SecurityException | IllegalArgumentException |
                 FileSystemNotFoundException | URISyntaxException e) {
            throw new ConfigurationReadException(configurationFile.toString(), e);
        }
    }


}
