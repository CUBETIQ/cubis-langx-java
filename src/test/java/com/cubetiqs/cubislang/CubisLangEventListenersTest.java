package com.cubetiqs.cubislang;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for event listeners and callbacks
 */
class CubisLangEventListenersTest {

    @Test
    void testTranslationLoadedListener() {
        AtomicBoolean listenerCalled = new AtomicBoolean(false);
        AtomicReference<String> loadedLocale = new AtomicReference<>();
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setOnTranslationLoadedListener(locale -> {
                    listenerCalled.set(true);
                    loadedLocale.set(locale);
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        assertTrue(listenerCalled.get());
        assertEquals("en", loadedLocale.get());
    }

    @Test
    void testTranslationLoadedListenerOnLocaleChange() {
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicReference<String> lastLocale = new AtomicReference<>();
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setOnTranslationLoadedListener(locale -> {
                    callCount.incrementAndGet();
                    lastLocale.set(locale);
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Initial load
        assertEquals(1, callCount.get());
        assertEquals("en", lastLocale.get());
        
        // Change to French
        lang.setLocale("fr");
        assertEquals(2, callCount.get());
        assertEquals("fr", lastLocale.get());
        
        // Change to Chinese
        lang.setLocale("zh");
        assertEquals(3, callCount.get());
        assertEquals("zh", lastLocale.get());
    }

    @Test
    void testMissingTranslationHandler() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        AtomicReference<String> missingKey = new AtomicReference<>();
        AtomicReference<String> localeRef = new AtomicReference<>();
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setMissingTranslationHandler((locale, key) -> {
                    handlerCalled.set(true);
                    missingKey.set(key);
                    localeRef.set(locale);
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Try to get a non-existent key
        String result = lang.get("nonexistent_key");
        
        assertTrue(handlerCalled.get());
        assertEquals("nonexistent_key", missingKey.get());
        assertEquals("en", localeRef.get());
    }

    @Test
    void testMissingTranslationHandlerWithDifferentLocales() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setMissingTranslationHandler((locale, key) -> {
                    callCount.incrementAndGet();
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Missing key in English
        lang.get("missing_en");
        assertEquals(1, callCount.get());
        
        // Switch to French
        lang.setLocale("fr");
        lang.get("missing_fr");
        assertEquals(2, callCount.get());
    }

    @Test
    void testErrorListenerNotCalledOnSuccess() {
        AtomicBoolean errorCalled = new AtomicBoolean(false);
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setOnTranslationErrorListener((locale, error) -> {
                    errorCalled.set(true);
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Should load successfully
        String result = lang.get("greeting");
        assertEquals("Hello!", result);
        
        // Error listener should not be called for successful operations
        assertFalse(errorCalled.get());
    }

    @Test
    void testMultipleEventListenersTogether() {
        AtomicInteger loadedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicInteger missingCount = new AtomicInteger(0);
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setOnTranslationLoadedListener(locale -> loadedCount.incrementAndGet())
                .setOnTranslationErrorListener((locale, error) -> errorCount.incrementAndGet())
                .setMissingTranslationHandler((locale, key) -> missingCount.incrementAndGet())
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Loaded should be called on initialization
        assertEquals(1, loadedCount.get());
        
        // Try missing key
        lang.get("missing_key");
        assertEquals(1, missingCount.get());
        
        // Change locale
        lang.setLocale("fr");
        assertEquals(2, loadedCount.get());
        
        // Try another missing key
        lang.get("another_missing");
        assertEquals(2, missingCount.get());
    }

    @Test
    void testNoListenersDoesNotThrowException() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .build();
        
        assertDoesNotThrow(() -> {
            CubisLang lang = new CubisLang(options);
            lang.get("greeting");
            lang.get("missing_key");
            lang.setLocale("fr");
        });
    }

    @Test
    void testListenerExceptionDoesNotBreakExecution() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setOnTranslationLoadedListener(locale -> {
                    throw new RuntimeException("Test exception in listener");
                })
                .build();
        
        // Should not throw exception even if listener throws
        assertDoesNotThrow(() -> {
            CubisLang lang = new CubisLang(options);
            String result = lang.get("greeting");
            assertEquals("Hello!", result);
        });
    }

    @Test
    void testMissingTranslationHandlerCannotChangeReturnValue() {
        AtomicReference<String> interceptedKey = new AtomicReference<>();
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setMissingTranslationHandler((locale, key) -> {
                    interceptedKey.set(key);
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        String result = lang.get("missing_key");
        
        // Should still return the key itself
        assertEquals("missing_key", result);
        assertEquals("missing_key", interceptedKey.get());
    }
}
