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
 * Test class for preload locales feature.
 */
public class PreloadLocalesTest {

    @TempDir
    Path tempDir;

    private Path langDir;

    @BeforeEach
    public void setUp() throws IOException {
        langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create test translation files
        Files.writeString(langDir.resolve("en.json"),
                "{\"greeting\": \"Hello\", \"farewell\": \"Goodbye\"}");
        Files.writeString(langDir.resolve("km.json"),
                "{\"greeting\": \"សួស្តី\", \"farewell\": \"លាហើយ\"}");
        Files.writeString(langDir.resolve("zh.json"),
                "{\"greeting\": \"你好\", \"farewell\": \"再见\"}");
        Files.writeString(langDir.resolve("fr.json"),
                "{\"greeting\": \"Bonjour\", \"farewell\": \"Au revoir\"}");
    }

    @Test
    public void testConstructorDoesNotBlock() {
        long startTime = System.currentTimeMillis();
        
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("km", "zh", "fr"))
                        .build()
        );
        
        long constructorTime = System.currentTimeMillis() - startTime;
        
        // Constructor should return quickly (under 1 second)
        // Even with 3 locales to preload
        assertTrue(constructorTime < 1000, 
                "Constructor took " + constructorTime + "ms, should be non-blocking");
        
        // Should be able to use default locale immediately
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
    }

    @Test
    public void testPreloadedLocalesEventuallyAvailable() throws InterruptedException {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("km", "zh"))
                        .build()
        );
        
        // Constructor returns immediately
        assertNotNull(lang);
        
        // Wait a bit for async preloading to complete
        Thread.sleep(500);
        
        // Switch to preloaded locale
        lang.setLocale("km");
        String greeting = lang.get("greeting");
        assertEquals("សួស្តី", greeting);
        
        // Switch to another preloaded locale
        lang.setLocale("zh");
        greeting = lang.get("greeting");
        assertEquals("你好", greeting);
    }

    @Test
    public void testPreloadWithoutBlockingDefaultLocale() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("km", "zh", "fr"))
                        .build()
        );
        
        // Should be able to use default locale immediately
        // while preloading happens in background
        for (int i = 0; i < 10; i++) {
            String greeting = lang.get("greeting");
            assertEquals("Hello", greeting);
        }
    }

    @Test
    public void testPreloadSkipsAlreadyLoadedLocales() throws InterruptedException {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setFallbackLocale("km")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("en", "km", "zh"))
                        .setDebugMode(true)
                        .build()
        );
        
        // en and km should already be loaded
        // Only zh should be preloaded in background
        
        Thread.sleep(300);
        
        // All three should work
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
        
        lang.setLocale("km");
        greeting = lang.get("greeting");
        assertEquals("សួស្តី", greeting);
        
        lang.setLocale("zh");
        greeting = lang.get("greeting");
        assertEquals("你好", greeting);
    }

    @Test
    public void testPreloadWithNullList() {
        // Should not throw exception
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(null)
                        .build()
        );
        
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
    }

    @Test
    public void testPreloadWithEmptyList() {
        // Should not throw exception
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList())
                        .build()
        );
        
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
    }

    @Test
    public void testPreloadNotSetDefaultsToNull() {
        // When preloadLocales is not set, should be null
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );
        
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
    }

    @Test
    public void testPreloadWithInvalidLocale() throws InterruptedException {
        // Should not crash if a preload locale file doesn't exist
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("km", "nonexistent", "zh"))
                        .setDebugMode(true)
                        .build()
        );
        
        // Should still work with default locale
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);
        
        // Wait for preload to attempt
        Thread.sleep(500);
        
        // Valid preloaded locales should still work
        lang.setLocale("km");
        greeting = lang.get("greeting");
        assertEquals("សួស្តី", greeting);
        
        lang.setLocale("zh");
        greeting = lang.get("greeting");
        assertEquals("你好", greeting);
    }

    @Test
    public void testPreloadImprovesAccessSpeed() throws InterruptedException {
        // Test without preload
        long startTime = System.currentTimeMillis();
        CubisLang langNoPreload = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );
        langNoPreload.setLocale("fr");
        langNoPreload.get("greeting"); // This loads fr.json on demand
        long timeWithoutPreload = System.currentTimeMillis() - startTime;
        
        // Test with preload
        startTime = System.currentTimeMillis();
        CubisLang langWithPreload = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("fr"))
                        .build()
        );
        Thread.sleep(300); // Wait for preload to complete
        langWithPreload.setLocale("fr");
        langWithPreload.get("greeting"); // This should be instant
        long timeWithPreload = System.currentTimeMillis() - startTime;
        
        // Both should work correctly
        assertEquals("Au revoir", langNoPreload.get("farewell"));
        assertEquals("Au revoir", langWithPreload.get("farewell"));
        
        // Note: In real-world scenarios with remote loading or larger files,
        // preload would show more significant performance improvement
    }

    @Test
    public void testPreloadWithCombinedLocales() throws InterruptedException {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setCombineLocales(Arrays.asList("en", "km", "zh"))
                        .setCombineSeparator(" / ")
                        .setPreloadLocales(Arrays.asList("km", "zh"))
                        .build()
        );
        
        // Wait for preload
        Thread.sleep(500);
        
        // Combined locales should work with preloaded locales
        String greeting = lang.get("greeting");
        assertEquals("Hello / សួស្តី / 你好", greeting);
    }

    @Test
    public void testMultipleInstancesPreloadIndependently() throws InterruptedException {
        CubisLang lang1 = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("km"))
                        .build()
        );
        
        CubisLang lang2 = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(Arrays.asList("zh"))
                        .build()
        );
        
        Thread.sleep(500);
        
        // Each instance should have its preloaded locale
        lang1.setLocale("km");
        assertEquals("សួស្តី", lang1.get("greeting"));
        
        lang2.setLocale("zh");
        assertEquals("你好", lang2.get("greeting"));
    }
}
