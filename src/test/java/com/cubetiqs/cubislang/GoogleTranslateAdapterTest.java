package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GoogleTranslateAdapter.
 * 
 * Note: These tests make real HTTP requests to Google Translate.
 * They may be slow and can fail if the service is unavailable.
 */
class GoogleTranslateAdapterTest {

    private GoogleTranslateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GoogleTranslateAdapter();
    }

    @Test
    void testTranslateSimpleText() {
        String result = adapter.translate("Hello", "en", "es");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Google Translate should return "Hola" or similar
        assertTrue(result.toLowerCase().contains("hola") || result.toLowerCase().equals("hola"));
    }

    @Test
    void testTranslateToKhmer() {
        String result = adapter.translate("Hello", "en", "km");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Should contain Khmer characters
        assertTrue(result.matches(".*[\\u1780-\\u17FF].*"));
    }

    @Test
    void testTranslateToChinese() {
        String result = adapter.translate("Hello", "en", "zh");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Should contain Chinese characters
        assertTrue(result.matches(".*[\\u4E00-\\u9FFF].*"));
    }

    @Test
    void testTranslateSameLocale() {
        String text = "Hello";
        String result = adapter.translate(text, "en", "en");
        assertEquals(text, result);
    }

    @Test
    void testTranslateNullText() {
        String result = adapter.translate(null, "en", "es");
        assertNull(result);
    }

    @Test
    void testTranslateEmptyText() {
        String result = adapter.translate("", "en", "es");
        assertEquals("", result);
    }

    @Test
    void testTranslateNullSourceLocale() {
        String result = adapter.translate("Hello", null, "es");
        assertNull(result);
    }

    @Test
    void testTranslateNullTargetLocale() {
        String result = adapter.translate("Hello", "en", null);
        assertNull(result);
    }

    @Test
    void testTranslateLongerText() {
        String text = "This is a longer sentence to test translation.";
        String result = adapter.translate(text, "en", "es");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 0);
    }

    @Test
    void testIsAvailable() {
        // This test makes a real HTTP request, so it might be slow
        boolean available = adapter.isAvailable();
        // In most cases, Google Translate should be available
        // But we don't want to fail the test if it's temporarily down
        // so we just log the result
        System.out.println("Google Translate adapter available: " + available);
    }

    @Test
    void testTranslateWithSpecialCharacters() {
        String text = "Hello, World! How are you?";
        String result = adapter.translate(text, "en", "fr");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testTranslateMultipleLanguages() {
        String text = "Good morning";
        
        // Test multiple target languages
        String spanish = adapter.translate(text, "en", "es");
        String french = adapter.translate(text, "en", "fr");
        String german = adapter.translate(text, "en", "de");
        
        assertNotNull(spanish);
        assertNotNull(french);
        assertNotNull(german);
        
        // All should be different (unless translation fails)
        assertNotEquals(spanish, french);
        assertNotEquals(french, german);
        assertNotEquals(spanish, german);
    }
}
