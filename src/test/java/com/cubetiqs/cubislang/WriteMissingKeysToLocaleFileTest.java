package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class WriteMissingKeysToLocaleFileTest {

    @TempDir
    Path tempDir;

    private Path langDir;

    @BeforeEach
    public void setUp() throws Exception {
        langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create a basic en.json file with some existing keys
        String enContent = "{\n" +
                "  \"hello\": \"Hello\",\n" +
                "  \"world\": \"World\"\n" +
                "}";
        Files.write(langDir.resolve("en.json"), enContent.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testWriteMissingKeysToLocaleFile() throws Exception {
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(3) // Small batch for quick testing
                        .setMissingKeysFlushIntervalSeconds(10)
                        .build()
        )) {
            // Verify existing keys work
            assertEquals("Hello", lang.get("hello"));
            assertEquals("World", lang.get("world"));

            // Request missing keys
            lang.get("missing_key1");
            lang.get("missing_key2");
            lang.get("missing_key3"); // Triggers batch flush

            // Wait for async write
            Thread.sleep(1000);

            // Read the updated locale file
            String updatedContent = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
            
            // Verify missing keys were added with empty values
            assertTrue(updatedContent.contains("\"missing_key1\": \"\""), "Should contain missing_key1 with empty value");
            assertTrue(updatedContent.contains("\"missing_key2\": \"\""), "Should contain missing_key2 with empty value");
            assertTrue(updatedContent.contains("\"missing_key3\": \"\""), "Should contain missing_key3 with empty value");
            
            // Verify existing keys are still there
            assertTrue(updatedContent.contains("\"hello\": \"Hello\""), "Should still contain hello key");
            assertTrue(updatedContent.contains("\"world\": \"World\""), "Should still contain world key");
        }
    }

    @Test
    public void testWriteMissingKeysDisabled() throws Exception {
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(false) // Disabled
                        .build()
        )) {
            // Request missing keys
            lang.get("missing_key1");
            lang.get("missing_key2");

            // Wait a bit
            Thread.sleep(500);

            // Read the locale file
            String content = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
            
            // Verify missing keys were NOT added
            assertFalse(content.contains("missing_key1"), "Should NOT contain missing_key1");
            assertFalse(content.contains("missing_key2"), "Should NOT contain missing_key2");
        }
    }

    @Test
    public void testManualFlush() throws Exception {
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(100) // High threshold
                        .setMissingKeysFlushIntervalSeconds(60) // Long interval
                        .build()
        )) {
            // Request a few missing keys (below batch size)
            lang.get("manual_key1");
            lang.get("manual_key2");

            // Manually flush
            lang.flushMissingKeys();

            // Wait for async write
            Thread.sleep(500);

            // Read the locale file
            String content = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
            
            // Verify keys were written
            assertTrue(content.contains("\"manual_key1\": \"\""));
            assertTrue(content.contains("\"manual_key2\": \"\""));
        }
    }

    @Test
    public void testFlushOnShutdown() throws Exception {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(100) // High threshold
                        .setMissingKeysFlushIntervalSeconds(60) // Long interval
                        .build()
        );

        // Request missing keys
        lang.get("shutdown_key1");
        lang.get("shutdown_key2");

        // Close should flush
        lang.close();

        // Wait for async write
        Thread.sleep(500);

        // Read the locale file
        String content = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
        
        // Verify keys were written
        assertTrue(content.contains("\"shutdown_key1\": \"\""));
        assertTrue(content.contains("\"shutdown_key2\": \"\""));
    }

    @Test
    public void testMultipleLocales() throws Exception {
        // Create km.json file
        String kmContent = "{\n" +
                "  \"hello\": \"សួស្តី\"\n" +
                "}";
        Files.write(langDir.resolve("km.json"), kmContent.getBytes(StandardCharsets.UTF_8));

        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(2)
                        .setMissingKeysFlushIntervalSeconds(10)
                        .build()
        )) {
            // Request missing keys in English
            lang.get("en_key1");
            lang.get("en_key2"); // Triggers flush

            // Switch to Khmer
            lang.setLocale("km");
            lang.get("km_key1");
            lang.get("km_key2"); // Triggers flush

            // Wait for async writes
            Thread.sleep(1000);

            // Verify en.json has new keys
            String enContent = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
            assertTrue(enContent.contains("\"en_key1\": \"\""));
            assertTrue(enContent.contains("\"en_key2\": \"\""));

            // Verify km.json has new keys
            String kmContentUpdated = new String(Files.readAllBytes(langDir.resolve("km.json")), StandardCharsets.UTF_8);
            assertTrue(kmContentUpdated.contains("\"km_key1\": \"\""));
            assertTrue(kmContentUpdated.contains("\"km_key2\": \"\""));
            // Original key should still be there
            assertTrue(kmContentUpdated.contains("\"hello\": \"សួស្តី\""));
        }
    }

    @Test
    public void testNoDuplicateKeys() throws Exception {
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(5)
                        .setMissingKeysFlushIntervalSeconds(1)
                        .build()
        )) {
            // Request same key multiple times
            lang.get("duplicate_key");
            lang.get("duplicate_key");
            lang.get("duplicate_key");
            lang.get("other_key");
            lang.get("another_key"); // Triggers flush

            // Wait for async write
            Thread.sleep(1500);

            // Read the locale file
            String content = new String(Files.readAllBytes(langDir.resolve("en.json")), StandardCharsets.UTF_8);
            
            // Count occurrences of duplicate_key - Java 8 compatible
            long count = 0;
            for (String line : content.split("\n")) {
                if (line.contains("\"duplicate_key\":")) {
                    count++;
                }
            }
            assertEquals(1, count, "duplicate_key should only appear once");
        }
    }

    @Test
    public void testCreateNewLocaleFile() throws Exception {
        // Test creating a new locale file that doesn't exist yet
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("fr")
                        .setResourcePath(langDir.toString() + "/")
                        .setWriteMissingKeysToFile(true)
                        .setMissingKeysBatchSize(2)
                        .setMissingKeysFlushIntervalSeconds(10)
                        .build()
        )) {
            // Request missing keys in a new locale
            lang.get("bonjour");
            lang.get("au_revoir"); // Triggers flush

            // Wait for async write
            Thread.sleep(1000);

            // Verify fr.json was created
            Path frFile = langDir.resolve("fr.json");
            assertTrue(Files.exists(frFile), "fr.json should be created");

            // Verify content
            String content = new String(Files.readAllBytes(frFile), StandardCharsets.UTF_8);
            assertTrue(content.contains("\"bonjour\": \"\""));
            assertTrue(content.contains("\"au_revoir\": \"\""));
        }
    }
}
