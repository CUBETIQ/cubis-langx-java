package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TranslationFileGenerator.
 */
class TranslationFileGeneratorTest {

    @TempDir
    Path tempDir;

    private TranslationFileGenerator generator;
    private File sourceFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a mock adapter for testing
        TranslationAdapter mockAdapter = new TranslationAdapter() {
            @Override
            public String translate(String text, String sourceLocale, String targetLocale) {
                // Simple mock: just prefix with target locale
                return "[" + targetLocale + "]" + text;
            }
            
            @Override
            public Map<String, String> translateBatch(List<String> texts, String sourceLocale, String targetLocale) {
                Map<String, String> results = new java.util.LinkedHashMap<>();
                for (String text : texts) {
                    results.put(text, translate(text, sourceLocale, targetLocale));
                }
                return results;
            }
        };
        
        generator = new TranslationFileGenerator(mockAdapter);
        
        // Create a source translation file
        sourceFile = tempDir.resolve("en.json").toFile();
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write("{\n");
            writer.write("  \"greeting\": \"Hello\",\n");
            writer.write("  \"farewell\": \"Goodbye\",\n");
            writer.write("  \"ui\": {\n");
            writer.write("    \"button_save\": \"Save\",\n");
            writer.write("    \"button_cancel\": \"Cancel\"\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    @Test
    void testGenerateTranslationFile() {
        File outputFile = tempDir.resolve("es.json").toFile();
        
        boolean success = generator.generateTranslationFile(
            sourceFile.getPath(),
            "en",
            "es",
            outputFile.getPath()
        );
        
        assertTrue(success);
        assertTrue(outputFile.exists());
    }

    @Test
    void testGenerateMultipleTranslationFiles() {
        List<String> targetLocales = Arrays.asList("es", "fr", "de");
        
        Map<String, Boolean> results = generator.generateMultipleTranslationFiles(
            sourceFile.getPath(),
            "en",
            targetLocales,
            tempDir.toString()
        );
        
        assertEquals(3, results.size());
        assertTrue(results.get("es"));
        assertTrue(results.get("fr"));
        assertTrue(results.get("de"));
        
        // Verify files were created
        assertTrue(tempDir.resolve("es.json").toFile().exists());
        assertTrue(tempDir.resolve("fr.json").toFile().exists());
        assertTrue(tempDir.resolve("de.json").toFile().exists());
    }

    @Test
    void testGenerateTranslationFileBatch() {
        File outputFile = tempDir.resolve("km.json").toFile();
        
        boolean success = generator.generateTranslationFileBatch(
            sourceFile.getPath(),
            "en",
            "km",
            outputFile.getPath()
        );
        
        assertTrue(success);
        assertTrue(outputFile.exists());
    }

    @Test
    void testMergeTranslationFile() throws IOException {
        // Create an existing target file with partial translations
        File targetFile = tempDir.resolve("es.json").toFile();
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("{\n");
            writer.write("  \"greeting\": \"Hola\"\n");
            writer.write("}\n");
        }
        
        boolean success = generator.mergeTranslationFile(
            sourceFile.getPath(),
            targetFile.getPath(),
            "en",
            "es"
        );
        
        assertTrue(success);
    }

    @Test
    void testInvalidSourceFile() {
        File outputFile = tempDir.resolve("es.json").toFile();
        
        boolean success = generator.generateTranslationFile(
            "nonexistent.json",
            "en",
            "es",
            outputFile.getPath()
        );
        
        assertFalse(success);
    }

    @Test
    void testNullAdapter() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TranslationFileGenerator(null);
        });
    }

    @Test
    void testNestedTranslations() throws IOException {
        // Create a more complex nested structure
        File complexFile = tempDir.resolve("en-complex.json").toFile();
        try (FileWriter writer = new FileWriter(complexFile)) {
            writer.write("{\n");
            writer.write("  \"level1\": {\n");
            writer.write("    \"level2\": {\n");
            writer.write("      \"level3\": \"Deep value\"\n");
            writer.write("    }\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
        
        File outputFile = tempDir.resolve("es-complex.json").toFile();
        
        boolean success = generator.generateTranslationFile(
            complexFile.getPath(),
            "en",
            "es",
            outputFile.getPath()
        );
        
        assertTrue(success);
        assertTrue(outputFile.exists());
    }

    @Test
    void testEmptySourceFile() throws IOException {
        File emptyFile = tempDir.resolve("empty.json").toFile();
        try (FileWriter writer = new FileWriter(emptyFile)) {
            writer.write("{}");
        }
        
        File outputFile = tempDir.resolve("empty-es.json").toFile();
        
        boolean success = generator.generateTranslationFile(
            emptyFile.getPath(),
            "en",
            "es",
            outputFile.getPath()
        );
        
        assertTrue(success);
        assertTrue(outputFile.exists());
    }

    @Test
    void testMergeWithNonExistentTargetFile() {
        boolean success = generator.mergeTranslationFile(
            sourceFile.getPath(),
            tempDir.resolve("new-target.json").toString(),
            "en",
            "es"
        );
        
        assertTrue(success);
        assertTrue(tempDir.resolve("new-target.json").toFile().exists());
    }

    @Test
    void testBatchVsIndividualTranslation() {
        // Test both methods produce output files
        File batchOutput = tempDir.resolve("batch-es.json").toFile();
        File individualOutput = tempDir.resolve("individual-es.json").toFile();
        
        boolean batchSuccess = generator.generateTranslationFileBatch(
            sourceFile.getPath(),
            "en",
            "es",
            batchOutput.getPath()
        );
        
        boolean individualSuccess = generator.generateTranslationFile(
            sourceFile.getPath(),
            "en",
            "es",
            individualOutput.getPath()
        );
        
        assertTrue(batchSuccess);
        assertTrue(individualSuccess);
        assertTrue(batchOutput.exists());
        assertTrue(individualOutput.exists());
    }
}
