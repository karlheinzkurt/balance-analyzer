package org.insaneheadoflettuce.input.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class FileCollectorTest
{
    List<Path> getPaths()
    {
        return List.of(
                Paths.get("some/path/20191023-1234-export.CSV"), // in
                Paths.get("some/path/20191022-1234-import.CSV"), // out
                Paths.get("some/path/20191021-12-export.CSV"),   // out
                Paths.get("some/path/2019102-1234-export.CSV"),    // out
                Paths.get("some/path/20191001-1234-export.csv"), // in
                Paths.get("some/path/20191105-1234-Export.csv")  // in
        );
    }

    @Test
    void notExistingPath()
    {
        final var thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> new FileCollector(Paths.get("j24lk2j34ljwlj2l3j42lk3j4lk")));
        Assertions.assertTrue(thrown.getMessage().contains("j24lk2j34ljwlj2l3j42lk3j4lk"));
        Assertions.assertTrue(thrown.getMessage().startsWith("Path does not exist"));
    }

    @Test
    void unfilteredAndUnsorted()
    {
        final var result = new FileCollector(getPaths())
                .collect();
        Assertions.assertEquals(6, result.size());
        final var index = new AtomicInteger();
        getPaths().forEach(expected -> Assertions.assertEquals(expected, result.get(index.getAndIncrement())));
    }

    @Test
    void unfilteredAndSorted()
    {
        final var result = new FileCollector(getPaths())
                .sortByDate(1, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .collect();
        Assertions.assertEquals(5, result.size());
        Assertions.assertTrue(result.get(0).endsWith("20191001-1234-export.csv"));
        Assertions.assertTrue(result.get(1).endsWith("20191021-12-export.CSV"));
        Assertions.assertTrue(result.get(2).endsWith("20191022-1234-import.CSV"));
        Assertions.assertTrue(result.get(3).endsWith("20191023-1234-export.CSV"));
        Assertions.assertTrue(result.get(4).endsWith("20191105-1234-Export.csv"));
    }

    @Test
    void filteredAndUnsorted()
    {
        final var result = new FileCollector(getPaths())
                .filterByPattern(Pattern.compile("(\\d{8})[-]1234[-]export[.]csv", Pattern.CASE_INSENSITIVE))
                .collect();
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get(0).endsWith("20191023-1234-export.CSV"));
        Assertions.assertTrue(result.get(1).endsWith("20191001-1234-export.csv"));
        Assertions.assertTrue(result.get(2).endsWith("20191105-1234-Export.csv"));
    }

    @Test
    void filteredAndSorted()
    {
        final var result = new FileCollector(getPaths())
                .filterByPattern(Pattern.compile("(\\d{8})[-]1234[-]export[.]csv", Pattern.CASE_INSENSITIVE))
                .sortByDate(1, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .collect();
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.get(0).endsWith("20191001-1234-export.csv"));
        Assertions.assertTrue(result.get(1).endsWith("20191023-1234-export.CSV"));
        Assertions.assertTrue(result.get(2).endsWith("20191105-1234-Export.csv"));
    }
}
