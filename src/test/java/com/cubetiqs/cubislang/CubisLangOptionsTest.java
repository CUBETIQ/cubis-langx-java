package com.cubetiqs.cubislang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CubisLangOptions builder
 */
class CubisLangOptionsTest {

    @Test
    void testBuilderWithMinimalOptions() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .build();
        
        assertNotNull(options);
        assertEquals("en", options.getDefaultLocale());
        assertEquals("./resources/lang/", options.getResourcePath());
    }

    @Test
    void testBuilderWithAllOptions() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setRemoteTranslationEnabled(true)
                .setRemoteTranslationUrl("https://example.com/lang/")
                .setEncryptionEnabled(true)
                .setDecryptionKey("test-key")
                .setCacheRemoteTranslations(true)
                .setCacheDurationHours(24)
                .setCachePath("./cache/")
                .setDebugMode(true)
                .build();
        
        assertNotNull(options);
        assertEquals("en", options.getDefaultLocale());
        assertEquals("./resources/lang/", options.getResourcePath());
        assertEquals("en", options.getFallbackLocale());
        assertTrue(options.isRemoteTranslationEnabled());
        assertEquals("https://example.com/lang/", options.getRemoteTranslationUrl());
        assertTrue(options.isEncryptionEnabled());
        assertEquals("test-key", options.getDecryptionKey());
        assertTrue(options.isCacheRemoteTranslations());
        assertEquals(24, options.getCacheDurationHours());
        assertEquals("./cache/", options.getCachePath());
        assertTrue(options.isDebugMode());
    }

    @Test
    void testBuilderWithFallbackLocale() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("fr")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .build();
        
        assertEquals("fr", options.getDefaultLocale());
        assertEquals("en", options.getFallbackLocale());
    }

    @Test
    void testBuilderWithRemoteTranslation() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setRemoteTranslationEnabled(true)
                .setRemoteTranslationUrl("https://cdn.example.com/translations/")
                .build();
        
        assertTrue(options.isRemoteTranslationEnabled());
        assertEquals("https://cdn.example.com/translations/", options.getRemoteTranslationUrl());
    }

    @Test
    void testBuilderWithCaching() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setCacheRemoteTranslations(true)
                .setCacheDurationHours(48)
                .setCachePath("./custom/cache/")
                .build();
        
        assertTrue(options.isCacheRemoteTranslations());
        assertEquals(48, options.getCacheDurationHours());
        assertEquals("./custom/cache/", options.getCachePath());
    }

    @Test
    void testBuilderWithEncryption() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setEncryptionEnabled(true)
                .setDecryptionKey("my-secret-key-123")
                .build();
        
        assertTrue(options.isEncryptionEnabled());
        assertEquals("my-secret-key-123", options.getDecryptionKey());
    }

    @Test
    void testBuilderWithDebugMode() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setDebugMode(true)
                .build();
        
        assertTrue(options.isDebugMode());
    }

    @Test
    void testBuilderWithEventListeners() {
        boolean[] loadedCalled = {false};
        boolean[] errorCalled = {false};
        boolean[] missingCalled = {false};
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setOnTranslationLoadedListener(locale -> loadedCalled[0] = true)
                .setOnTranslationErrorListener((locale, error) -> errorCalled[0] = true)
                .setMissingTranslationHandler((locale, key) -> missingCalled[0] = true)
                .build();
        
        assertNotNull(options.getOnTranslationLoadedListener());
        assertNotNull(options.getOnTranslationErrorListener());
        assertNotNull(options.getMissingTranslationHandler());
        
        // Test callbacks
        options.getOnTranslationLoadedListener().onTranslationLoaded("en");
        assertTrue(loadedCalled[0]);
        
        options.getOnTranslationErrorListener().onTranslationError("en", "test error");
        assertTrue(errorCalled[0]);
        
        options.getMissingTranslationHandler().onMissingTranslation("en", "test.key");
        assertTrue(missingCalled[0]);
    }

    @Test
    void testDefaultValues() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .build();
        
        // Test required values are set
        assertEquals("en", options.getDefaultLocale());
        assertEquals("./resources/lang/", options.getResourcePath());
        
        // Test default values from Builder class
        assertEquals("en", options.getFallbackLocale()); // Builder sets default to "en"
        assertFalse(options.isRemoteTranslationEnabled());
        assertFalse(options.isEncryptionEnabled());
        assertTrue(options.isCacheRemoteTranslations()); // Builder sets default to true
        assertEquals(24, options.getCacheDurationHours()); // Builder sets default to 24
        assertEquals("./resources/cache/lang/", options.getCachePath()); // Builder sets default
        assertFalse(options.isDebugMode());
    }

    @Test
    void testBuilderImmutability() {
        CubisLangOptions.Builder builder = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/");
        
        CubisLangOptions options1 = builder.build();
        
        // Modify builder
        builder.setDefaultLocale("fr");
        CubisLangOptions options2 = builder.build();
        
        // Original should remain unchanged
        assertEquals("en", options1.getDefaultLocale());
        assertEquals("fr", options2.getDefaultLocale());
    }

    @Test
    void testMultipleBuildsFromSameBuilder() {
        CubisLangOptions.Builder builder = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setDebugMode(true);
        
        CubisLangOptions options1 = builder.build();
        CubisLangOptions options2 = builder.build();
        
        // Both should have same values
        assertEquals(options1.getDefaultLocale(), options2.getDefaultLocale());
        assertEquals(options1.getResourcePath(), options2.getResourcePath());
        assertEquals(options1.isDebugMode(), options2.isDebugMode());
    }

    @Test
    void testBuilderMethodChaining() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(true)
                .setCacheRemoteTranslations(true)
                .build();
        
        assertNotNull(options);
        assertEquals("en", options.getDefaultLocale());
        assertTrue(options.isDebugMode());
        assertTrue(options.isCacheRemoteTranslations());
    }
}
