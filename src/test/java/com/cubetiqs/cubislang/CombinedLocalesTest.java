package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for combined locales feature.
 */
public class CombinedLocalesTest {

    @TempDir
    Path tempDir;

    private Path langDir;

    @BeforeEach
    public void setUp() throws IOException {
        langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create test translation files
        Files.writeString(langDir.resolve("en.json"),
                "{\"greeting\": \"Hello\", \"farewell\": \"Goodbye\", \"welcome\": \"Welcome {{0}}\"}");
        Files.writeString(langDir.resolve("km.json"),
                "{\"greeting\": \"សួស្តី\", \"thanks\": \"អរគុណ\"}");
        Files.writeString(langDir.resolve("zh.json"),
                "{\"greeting\": \"你好\", \"thanks\": \"谢谢\", \"welcome\": \"欢迎 {{0}}\"}");
        Files.writeString(langDir.resolve("fr.json"),
                "{\"greeting\": \"Bonjour\", \"farewell\": \"Au revoir\"}");
    }

    @Test
    public void testCombineTwoLocales() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello / សួស្តី", result);
    }

    @Test
    public void testCombineTwoLocalesDisabled() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocalesEnabled(false)
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello", result);
    }

    @Test
    public void testCombineThreeLocales() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km", "zh"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello / សួស្តី / 你好", result);
    }

    @Test
    public void testCombineWithMissingTranslationInOneLocale() {
        // "farewell" exists in en and fr, but not in km
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km", "fr"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("farewell");
        // Should only include en and fr, skip km
        assertEquals("Goodbye / Au revoir", result);
    }

    @Test
    public void testCombineWithOnlyOneLocaleFound() {
        // "thanks" only exists in km and zh, not in en or fr
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("thanks");
        // Should only return km translation
        assertEquals("អរគុណ", result);
    }

    @Test
    public void testCombineWithNoTranslationsFound() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("nonexistent_key");
        // Should return the key itself
        assertEquals("nonexistent_key", result);
    }

    @Test
    public void testCombineWithCustomSeparator() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "fr"))
                        .setCombineSeparator(" | ")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello | Bonjour", result);
    }

    @Test
    public void testCombineWithNoSeparator() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator("")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Helloសួស្តី", result);
    }

    @Test
    public void testCombineWithFormatting() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "zh"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("welcome", "John");
        assertEquals("Welcome John / 欢迎 John", result);
    }

    @Test
    public void testCombineLocalesNotSet() {
        // When combineLocales is not set, should behave normally
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello", result);
    }

    @Test
    public void testCombineLocalesEmpty() {
        // When combineLocales is empty, should behave normally
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList())
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello", result);
    }

    @Test
    public void testCombineWithMissingTranslationHandler() {
        final int[] missingCount = { 0 };

        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km", "zh"))
                        .setCombineSeparator(" / ")
                        .setMissingTranslationHandler((locale, key) -> {
                            missingCount[0]++;
                        })
                        .build());

        String result = lang.get("nonexistent");
        assertEquals("nonexistent", result);
        // Should be called once for each locale
        assertEquals(3, missingCount[0]);
    }

    @Test
    public void testDirectGetCombinedMethod() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" - ")
                        .build());

        String result = lang.getCombined("greeting");
        assertEquals("Hello - សួស្តី", result);
    }

    @Test
    public void testCombineSingleLocale() {
        // Test with only one locale in the list
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("greeting");
        assertEquals("Hello", result);
    }

    @Test
    public void testCombineWithAllLocalesMissing() {
        // Test where the key doesn't exist in any of the combined locales
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "fr"))
                        .setCombineSeparator(" / ")
                        .build());

        String result = lang.get("thanks"); // exists in km and zh, not in en or fr
        assertEquals("thanks", result); // Should return the key
    }
}
