package org.insaneheadoflettuce.balanceAnalyzer.utility;

import com.google.common.io.MoreFiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class FileUtilities {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();

    private static Path ensureDirectory(Path path) throws IOException {
        MoreFiles.createParentDirectories(path);
        return path;
    }

    public static Path ensureFile(Path path) throws IOException {
        final var result = ensureDirectory(path);
        if (!Files.isRegularFile(result)) {
            MoreFiles.touch(result);
        }
        return path;
    }

    public static <T> List<T> readJson(Path path, Class<T[]> clazz, Supplier<T[]> factory) throws IOException {
        try (final var reader = new FileReader(FileUtilities.ensureFile(path).toFile())) {
            return Arrays.asList(Optional.ofNullable(gson.fromJson(reader, clazz)).orElse(factory.get()));
        }
    }
}
