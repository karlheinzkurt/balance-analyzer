package org.insaneheadoflettuce.input;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
public class DataImporterTest
{
    @Autowired
    DataImporter dataImporter;

    @Test
    void availableTypes()
    {
        final var importer = dataImporter.getImporter();
        Assertions.assertEquals(2, importer.size());
        Assertions.assertTrue(importer.containsKey("LBB"));
        Assertions.assertNotEquals(null, importer.get("LBB"));
        Assertions.assertTrue(importer.containsKey("Postbank"));
        Assertions.assertNotEquals(null, importer.get("Postbank"));
    }

    @Configuration
    @ComponentScan(basePackageClasses = {DataImporter.class})
    public static class SpringConfig
    {
    }
}
