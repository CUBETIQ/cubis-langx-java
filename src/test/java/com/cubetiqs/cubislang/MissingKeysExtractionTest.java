package com.cubetiqs.cubislang;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for missing keys extraction feature.
 */
public class MissingKeysExtractionTest {

    @TempDir
    Path tempDir;

    private Path langDir;
    private CubisLang lang;

    @BeforeEach
    public void setUp() throws IOException {
        langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create English (reference) locale with complete translations
        Files.write(langDir.resolve("en.json"),
                ("{" +
                        "\"greeting\": \"Hello\"," +
                        "\"farewell\": \"Goodbye\"," +
                        "\"thanks\": \"Thank you\"," +
                        "\"ui\": {" +
                        "\"button\": {" +
                        "\"save\": \"Save\"," +
                        "\"cancel\": \"Cancel\"" +
                        "}," +
                        "\"menu\": \"Menu\"" +
                        "}," +
                        "\"error\": {" +
                        "\"notFound\": \"Not found\"," +
                        "\"serverError\": \"Server error\"" +
                        "}" +
                        "}").getBytes(StandardCharsets.UTF_8));

        // Create French locale with some missing translations
        Files.write(langDir.resolve("fr.json"),
                ("{" +
                        "\"greeting\": \"Bonjour\"," +
                        "\"ui\": {" +
                        "\"button\": {" +
                        "\"save\": \"Enregistrer\"" +
                        "}" +
                        "}" +
                        "}").getBytes(StandardCharsets.UTF_8));

        // Create Chinese locale with different missing translations
        Files.write(langDir.resolve("zh.json"),
                ("{" +
                        "\"greeting\": \"你好\"," +
                        "\"farewell\": \"再见\"," +
                        "\"error\": {" +
                        "\"notFound\": \"未找到\"" +
                        "}" +
                        "}").getBytes(StandardCharsets.UTF_8));

        // Create empty locale
        Files.write(langDir.resolve("de.json"),
                "{}".getBytes(StandardCharsets.UTF_8));

        lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Load all locales
        lang.setLocale("fr");
        lang.setLocale("zh");
        lang.setLocale("de");
        lang.setLocale("en");
    }

    @Test
    public void testGetAllKeys() {
        Set<String> enKeys = lang.getAllKeys("en");

        assertEquals(8, enKeys.size());
        assertTrue(enKeys.contains("greeting"));
        assertTrue(enKeys.contains("farewell"));
        assertTrue(enKeys.contains("thanks"));
        assertTrue(enKeys.contains("ui.button.save"));
        assertTrue(enKeys.contains("ui.button.cancel"));
        assertTrue(enKeys.contains("ui.menu"));
        assertTrue(enKeys.contains("error.notFound"));
        assertTrue(enKeys.contains("error.serverError"));
    }

    @Test
    public void testGetAllKeysForFrench() {
        Set<String> frKeys = lang.getAllKeys("fr");

        assertEquals(2, frKeys.size());
        assertTrue(frKeys.contains("greeting"));
        assertTrue(frKeys.contains("ui.button.save"));
    }

    @Test
    public void testGetAllKeysForNonExistentLocale() {
        Set<String> keys = lang.getAllKeys("nonexistent");
        assertTrue(keys.isEmpty());
    }

    @Test
    public void testFindMissingKeysInFrench() {
        Set<String> missingKeys = lang.findMissingKeys("en", "fr");

        assertEquals(6, missingKeys.size());
        assertTrue(missingKeys.contains("farewell"));
        assertTrue(missingKeys.contains("thanks"));
        assertTrue(missingKeys.contains("ui.button.cancel"));
        assertTrue(missingKeys.contains("ui.menu"));
        assertTrue(missingKeys.contains("error.notFound"));
        assertTrue(missingKeys.contains("error.serverError"));

        assertFalse(missingKeys.contains("greeting"));
        assertFalse(missingKeys.contains("ui.button.save"));
    }

    @Test
    public void testFindMissingKeysInChinese() {
        Set<String> missingKeys = lang.findMissingKeys("en", "zh");

        assertEquals(5, missingKeys.size());
        assertTrue(missingKeys.contains("thanks"));
        assertTrue(missingKeys.contains("ui.button.save"));
        assertTrue(missingKeys.contains("ui.button.cancel"));
        assertTrue(missingKeys.contains("ui.menu"));
        assertTrue(missingKeys.contains("error.serverError"));
    }

    @Test
    public void testFindMissingKeysInEmptyLocale() {
        Set<String> missingKeys = lang.findMissingKeys("en", "de");

        assertEquals(8, missingKeys.size());
        // All keys should be missing
        assertTrue(missingKeys.contains("greeting"));
        assertTrue(missingKeys.contains("farewell"));
    }

    @Test
    public void testFindMissingKeysWhenNoMissing() {
        Set<String> missingKeys = lang.findMissingKeys("en", "en");
        assertTrue(missingKeys.isEmpty());
    }

    @Test
    public void testExtractMissingKeysWithValues() {
        Map<String, String> missingData = lang.extractMissingKeysWithValues("en", "fr");

        assertEquals(6, missingData.size());
        assertEquals("Goodbye", missingData.get("farewell"));
        assertEquals("Thank you", missingData.get("thanks"));
        assertEquals("Cancel", missingData.get("ui.button.cancel"));
        assertEquals("Menu", missingData.get("ui.menu"));
        assertEquals("Not found", missingData.get("error.notFound"));
        assertEquals("Server error", missingData.get("error.serverError"));

        assertFalse(missingData.containsKey("greeting"));
        assertFalse(missingData.containsKey("ui.button.save"));
    }

    @Test
    public void testExtractMissingKeysAsJson() {
        JsonObject missingJson = lang.extractMissingKeysAsJson("en", "fr");

        // Check flat keys
        assertTrue(missingJson.has("farewell"));
        assertEquals("Goodbye", missingJson.get("farewell").getAsString());
        assertTrue(missingJson.has("thanks"));
        assertEquals("Thank you", missingJson.get("thanks").getAsString());

        // Check nested keys - should preserve structure
        assertTrue(missingJson.has("ui"));
        JsonObject uiObj = missingJson.getAsJsonObject("ui");

        assertTrue(uiObj.has("button"));
        JsonObject buttonObj = uiObj.getAsJsonObject("button");
        assertTrue(buttonObj.has("cancel"));
        assertEquals("Cancel", buttonObj.get("cancel").getAsString());

        assertTrue(uiObj.has("menu"));
        assertEquals("Menu", uiObj.get("menu").getAsString());

        assertTrue(missingJson.has("error"));
        JsonObject errorObj = missingJson.getAsJsonObject("error");
        assertTrue(errorObj.has("notFound"));
        assertEquals("Not found", errorObj.get("notFound").getAsString());
        assertTrue(errorObj.has("serverError"));
        assertEquals("Server error", errorObj.get("serverError").getAsString());

        // Keys that exist in French should not be in missing
        assertFalse(missingJson.has("greeting"));
        // ui.button.save exists in French, so button should have save
        assertFalse(buttonObj.has("save"));
    }

    @Test
    public void testExtractMissingKeysAsJsonPreservesNestedStructure() {
        JsonObject missingJson = lang.extractMissingKeysAsJson("en", "zh");

        // Verify nested structure is preserved
        assertTrue(missingJson.has("ui"));
        JsonObject uiObj = missingJson.getAsJsonObject("ui");
        assertTrue(uiObj.has("button"));
        assertTrue(uiObj.has("menu"));

        JsonObject buttonObj = uiObj.getAsJsonObject("button");
        assertTrue(buttonObj.has("save"));
        assertTrue(buttonObj.has("cancel"));
    }

    @Test
    public void testGetLoadedLocales() {
        Set<String> locales = lang.getLoadedLocales();

        assertEquals(4, locales.size());
        assertTrue(locales.contains("en"));
        assertTrue(locales.contains("fr"));
        assertTrue(locales.contains("zh"));
        assertTrue(locales.contains("de"));
    }

    @Test
    public void testExtractMissingKeysWithValuesForNonExistentReferenceLocale() {
        Map<String, String> missingData = lang.extractMissingKeysWithValues("nonexistent", "fr");
        assertTrue(missingData.isEmpty());
    }

    @Test
    public void testExtractMissingKeysAsJsonForNonExistentReferenceLocale() {
        JsonObject missingJson = lang.extractMissingKeysAsJson("nonexistent", "fr");
        assertEquals(0, missingJson.size());
    }
}
