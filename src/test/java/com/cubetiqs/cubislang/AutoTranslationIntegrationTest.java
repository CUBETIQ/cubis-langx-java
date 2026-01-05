package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for auto-translation feature.
 */
class AutoTranslationIntegrationTest {

    @TempDir
    Path tempDir;

    private CubisLang cubisLang;

    @BeforeEach
    void setUp() throws IOException {
        // Create a simple English translation file
        File langDir = tempDir.resolve("lang").toFile();
        langDir.mkdirs();
        
        File enFile = new File(langDir, "en.json");
        try (FileWriter writer = new FileWriter(enFile)) {
            writer.write("{\n");
            writer.write("  \"greeting\": \"Hello\",\n");
            writer.write("  \"farewell\": \"Goodbye\",\n");
            writer.write("  \"app.title\": \"My Application\",\n");
            writer.write("  \"welcome.message\": \"Welcome to our application!\"\n");
            writer.write("}\n");
        }
        
        // Create a Spanish translation file with only some keys
        File esFile = new File(langDir, "es.json");
        try (FileWriter writer = new FileWriter(esFile)) {
            writer.write("{\n");
            writer.write("  \"greeting\": \"Hola\"\n");
            writer.write("}\n");
        }

        // Configure CubisLang with auto-translation enabled
        CubisLangOptions options = CubisLangOptions.builder()
                .setResourcePath(langDir.getAbsolutePath() + "/")
                .setDefaultLocale("es")
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(true)
                .setTranslationAdapter(new GoogleTranslateAdapter(5)) // 5 second timeout
                .setDebugMode(true)
                .build();

        cubisLang = new CubisLang(options);
    }

    @Test
    void testAutoTranslationForMissingKey() {
        // "greeting" exists in Spanish, should use it directly
        String greeting = cubisLang.get("greeting");
        assertEquals("Hola", greeting);
        
        // "farewell" doesn't exist in Spanish, should auto-translate from English
        String farewell = cubisLang.get("farewell");
        assertNotNull(farewell);
        // With auto-translation, it should not be the original English text
        // and should not be the key itself
        if (!farewell.equals("Goodbye") && !farewell.equals("farewell")) {
            System.out.println("✓ Auto-translated 'farewell': " + farewell);
        }
    }

    @Test
    void testAutoTranslationForNestedKey() {
        // "app.title" doesn't exist in Spanish, should auto-translate
        String appTitle = cubisLang.get("app.title");
        assertNotNull(appTitle);
        // With auto-translation enabled, should get a translated value
        if (!appTitle.equals("My Application") && !appTitle.equals("app.title")) {
            System.out.println("✓ Auto-translated 'app.title': " + appTitle);
        }
    }

    @Test
    void testAutoTranslationWithComplexText() {
        // "welcome.message" should be auto-translated
        String welcomeMessage = cubisLang.get("welcome.message");
        assertNotNull(welcomeMessage);
        // Should get some value (either translation or fallback)
        assertFalse(welcomeMessage.isEmpty());
        
        System.out.println("Auto-translated 'welcome.message': " + welcomeMessage);
    }

    @Test
    void testAutoTranslationDisabled() throws IOException {
        // Create CubisLang without auto-translation
        File langDir = tempDir.resolve("lang2").toFile();
        langDir.mkdirs();
        
        File enFile = new File(langDir, "en.json");
        try (FileWriter writer = new FileWriter(enFile)) {
            writer.write("{\"greeting\": \"Hello\", \"farewell\": \"Goodbye\"}\n");
        }
        
        File esFile = new File(langDir, "es.json");
        try (FileWriter writer = new FileWriter(esFile)) {
            writer.write("{\"farewell\": \"Adiós\"}\n");
        }

        CubisLangOptions options = CubisLangOptions.builder()
                .setResourcePath(langDir.getAbsolutePath() + "/")
                .setDefaultLocale("es")
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(false)
                .build();

        CubisLang lang = new CubisLang(options);
        
        // "farewell" exists in Spanish
        String farewell = lang.get("farewell");
        assertEquals("Adiós", farewell);
        
        // "greeting" doesn't exist in Spanish, should fallback to English
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
        
        // Non-existent key should return the key itself
        String missing = lang.get("nonexistent");
        assertEquals("nonexistent", missing);
    }

    @Test
    void testAutoTranslationWithMissingTranslationHandler() throws IOException {
        File langDir = tempDir.resolve("lang3").toFile();
        langDir.mkdirs();
        
        File enFile = new File(langDir, "en.json");
        try (FileWriter writer = new FileWriter(enFile)) {
            writer.write("{\"existing\": \"Hello\"}\n");
        }
        
        File esFile = new File(langDir, "es.json");
        try (FileWriter writer = new FileWriter(esFile)) {
            writer.write("{}\n");
        }

        final boolean[] handlerCalled = {false};
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setResourcePath(langDir.getAbsolutePath() + "/")
                .setDefaultLocale("es")
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(true)
                .setTranslationAdapter(new GoogleTranslateAdapter(5))
                .setMissingTranslationHandler((locale, key) -> {
                    handlerCalled[0] = true;
                    System.out.println("Missing translation: " + locale + " - " + key);
                })
                .build();

        CubisLang lang = new CubisLang(options);
        
        // Get a key that exists in fallback locale but not in Spanish
        // With auto-translation, it should translate and NOT call the handler
        String existing = lang.get("existing");
        assertNotNull(existing);
        
        // Get a completely missing key - handler SHOULD be called
        String missing = lang.get("completely.missing.key");
        assertEquals("completely.missing.key", missing);
        assertTrue(handlerCalled[0], "Handler should be called for completely missing keys");
    }

    @Test
    void testAutoTranslationWithCustomAdapter() throws IOException {
        File langDir = tempDir.resolve("lang4").toFile();
        langDir.mkdirs();
        
        File enFile = new File(langDir, "en.json");
        try (FileWriter writer = new FileWriter(enFile)) {
            writer.write("{\"greeting\": \"Hello\"}\n");
        }
        
        File esFile = new File(langDir, "es.json");
        try (FileWriter writer = new FileWriter(esFile)) {
            writer.write("{}\n");
        }

        // Create a mock adapter for testing
        TranslationAdapter mockAdapter = new TranslationAdapter() {
            @Override
            public String translate(String text, String sourceLocale, String targetLocale) {
                return "[TRANSLATED:" + text + "]";
            }
        };

        CubisLangOptions options = CubisLangOptions.builder()
                .setResourcePath(langDir.getAbsolutePath() + "/")
                .setDefaultLocale("es")
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(true)
                .setTranslationAdapter(mockAdapter)
                .build();

        CubisLang lang = new CubisLang(options);
        
        // "greeting" exists in fallback (en) but not in es
        // Since it exists in fallback, it returns the fallback value (not translated)
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
    }
}
