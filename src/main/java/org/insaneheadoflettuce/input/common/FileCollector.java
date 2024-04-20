package org.insaneheadoflettuce.input.common;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileCollector {
    private static class Item {
        Item(Path path, Pattern pattern) {
            this.path = path;
            this.matcher = (pattern == null ? Pattern.compile(".*") : pattern)
                    .matcher(path.getFileName().toString());
        }

        boolean matches() {
            return matcher.matches();
        }

        boolean hasDate(int groupNumber, DateTimeFormatter formatter) {
            if (!matches()) {
                return false;
            }
            if (matcher.groupCount() < groupNumber) {
                return false;
            }
            final var group = matcher.group(groupNumber);
            date = LocalDate.parse(group, formatter);
            return true;
        }

        Path getPath() {
            return path;
        }

        LocalDate getDate() {
            return date;
        }

        private final Path path;
        private final Matcher matcher;
        private LocalDate date = LocalDate.MIN;
    }

    private final List<Path> paths;
    private Pattern pattern;
    private final List<Predicate<Item>> predicates = new ArrayList<>();
    private Comparator<Item> sorter = (a, b) -> 0; // Identity

    private static Path validate(Path root) {
        if (!Files.exists(root)) {
            throw new IllegalArgumentException("Path does not exist: " + root);
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Path exists but is no directory: " + root);
        }
        return root;
    }

    private static List<Path> getFileList(Path root) {
        try {
            return Files.walk(validate(root)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Could not discover file from root path: " + root);
        }
    }

    public FileCollector(Path root) {
        this(getFileList(validate(root)));
    }

    public FileCollector(List<Path> paths) {
        this.paths = paths;
        this.predicates.add(Item::matches);
    }

    public FileCollector filterByPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public FileCollector sortByDate(int groupNumber, DateTimeFormatter formatter) {
        if (pattern == null) {
            pattern = Pattern.compile(".*(\\d{8}).*"); // TODO I don't know, this is not a good assumption, fix this
        }
        this.predicates.add(item -> item.hasDate(groupNumber, formatter));
        this.sorter = Comparator.comparing(Item::getDate);
        return this;
    }

    public List<Path> collect() {
        return paths.stream()
                .map(path -> new Item(path, pattern))
                .filter(Predicates.and(predicates))
                .sorted(sorter)
                .map(Item::getPath)
                .collect(Collectors.toList());
    }
}
