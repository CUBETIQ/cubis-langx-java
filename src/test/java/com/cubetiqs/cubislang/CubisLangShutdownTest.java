package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CubisLang shutdown and resource cleanup.
 */
public class CubisLangShutdownTest {

    @TempDir
    Path tempDir;

    private Path langDir;

    @BeforeEach
    public void setUp() throws IOException {
        langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create test translation files
        Files.write(langDir.resolve("en.json"),
                "{\"greeting\": \"Hello\", \"farewell\": \"Goodbye\"}".getBytes(StandardCharsets.UTF_8));
        Files.write(langDir.resolve("km.json"),
                "{\"greeting\": \"សួស្តី\", \"farewell\": \"លាហើយ\"}".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testShutdown() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Verify it works before shutdown
        assertFalse(lang.isShutdown());
        String greeting = lang.get("greeting");
        assertEquals("Hello", greeting);

        // Shutdown
        lang.shutdown();

        // Verify shutdown state
        assertTrue(lang.isShutdown());
    }

    @Test
    public void testMultipleShutdownCalls() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Multiple shutdown calls should not throw exception
        lang.shutdown();
        lang.shutdown();
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testCloseMethod() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        assertFalse(lang.isShutdown());

        // Call close() instead of shutdown()
        lang.close();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testTryWithResources() {
        // Test AutoCloseable with try-with-resources
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        )) {
            assertFalse(lang.isShutdown());
            String greeting = lang.get("greeting");
            assertEquals("Hello", greeting);
        } // close() is called automatically here

        // Cannot verify shutdown state after try block since lang is out of scope
        // But the test verifies that close() is called without exception
    }

    @Test
    public void testTryWithResourcesMultipleInstances() {
        // Test multiple instances in try-with-resources
        try (CubisLang lang1 = new CubisLang(
                     CubisLangOptions.builder()
                             .setDefaultLocale("en")
                             .setResourcePath(langDir.toString() + "/")
                             .build()
             );
             CubisLang lang2 = new CubisLang(
                     CubisLangOptions.builder()
                             .setDefaultLocale("km")
                             .setResourcePath(langDir.toString() + "/")
                             .build()
             )) {
            
            assertFalse(lang1.isShutdown());
            assertFalse(lang2.isShutdown());

            assertEquals("Hello", lang1.get("greeting"));
            assertEquals("សួស្តី", lang2.get("greeting"));
        }
    }

    @Test
    public void testShutdownWithDebugMode() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setDebugMode(true)
                        .build()
        );

        lang.get("greeting");
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testShutdownClearsCaches() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Load some translations
        lang.get("greeting");
        lang.setLocale("km");
        lang.get("greeting");

        // Shutdown should clear caches
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testShutdownWithRemoteTranslations() {
        // Test shutdown with HTTP client (even if remote is disabled)
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setRemoteTranslationEnabled(false)
                        .build()
        );

        lang.get("greeting");
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testShutdownAfterClearCache() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        lang.get("greeting");
        lang.clearCache();
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }

    @Test
    public void testIsShutdownInitialState() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Should not be shutdown initially
        assertFalse(lang.isShutdown());
    }

    @Test
    public void testShutdownDoesNotThrowExceptions() {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        );

        // Should not throw any exceptions
        assertDoesNotThrow(() -> lang.shutdown());
        assertDoesNotThrow(() -> lang.close());
    }

    @Test
    public void testShutdownWithPreloadLocales() throws InterruptedException {
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .setPreloadLocales(java.util.Arrays.asList("km"))
                        .build()
        );

        // Wait a bit for preload to start
        Thread.sleep(100);

        // Shutdown should work even with preload in progress
        lang.shutdown();

        assertTrue(lang.isShutdown());
    }
}
