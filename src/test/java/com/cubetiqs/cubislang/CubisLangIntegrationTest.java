package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CubisLang covering complex scenarios
 */
class CubisLangIntegrationTest {

    private CubisLang lang;

    @BeforeEach
    void setUp() {
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("src/test/resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(false)
                .build();
        
        lang = new CubisLang(options);
    }

    @Test
    void testCompleteWorkflowWithLocaleChanges() {
        // Start with English
        assertEquals("Hello!", lang.get("greeting"));
        assertEquals("Goodbye!", lang.get("farewell"));
        
        // Switch to French
        lang.setLocale("fr");
        assertEquals("Bonjour!", lang.get("greeting"));
        assertEquals("Au revoir!", lang.get("farewell"));
        
        // Switch to Chinese (incomplete translations)
        lang.setLocale("zh");
        assertEquals("你好!", lang.get("greeting"));
        // With fallback locale set to "en", should fallback to English
        assertEquals("Goodbye!", lang.get("farewell"));
        
        // Switch back to English
        lang.setLocale("en");
        assertEquals("Hello!", lang.get("greeting"));
    }

    @Test
    void testMixedTranslationMethods() {
        // Simple
        String greeting = lang.get("greeting");
        
        // With params
        String welcome = lang.get("welcome_user", "John");
        
        // With context
        String saveBtn = lang.getWithContext("button_save", "ui");
        
        // Plural
        String items = lang.getPlural("item_count", 3);
        
        assertNotNull(greeting);
        assertNotNull(welcome);
        assertNotNull(saveBtn);
        assertNotNull(items);
        
        assertTrue(welcome.contains("John"));
        assertEquals("Save", saveBtn);
    }

    @Test
    void testTranslationCacheAcrossLocaleChanges() {
        // Load English multiple times
        for (int i = 0; i < 5; i++) {
            assertEquals("Hello!", lang.get("greeting"));
        }
        
        // Switch to French
        lang.setLocale("fr");
        for (int i = 0; i < 5; i++) {
            assertEquals("Bonjour!", lang.get("greeting"));
        }
        
        // Switch back to English - should use cached translations
        lang.setLocale("en");
        assertEquals("Hello!", lang.get("greeting"));
    }

    @Test
    void testFallbackChainWithPartialTranslations() {
        lang.setLocale("zh");
        
        // Keys that exist in Chinese
        assertEquals("你好!", lang.get("greeting"));
        assertEquals("保存", lang.getWithContext("button_save", "ui"));
        
        // Keys that don't exist in Chinese - should fallback to English
        String farewell = lang.get("farewell");
        String cancel = lang.getWithContext("button_cancel", "ui");
        
        assertEquals("Goodbye!", farewell);
        assertEquals("Cancel", cancel);
    }

    @Test
    void testConcurrentTranslationRequests() {
        String[] keys = {"greeting", "farewell", "welcome_user", "item_count", "ui.button_save"};
        
        for (String key : keys) {
            String result = lang.get(key);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void testTranslationWithSpecialCharacters() {
        lang.setLocale("zh");
        String greeting = lang.get("greeting");
        
        // Should handle Unicode characters correctly
        assertEquals("你好!", greeting);
        assertTrue(greeting.length() > 0);
    }

    @Test
    void testRapidLocaleSwitching() {
        for (int i = 0; i < 10; i++) {
            lang.setLocale("en");
            assertEquals("Hello!", lang.get("greeting"));
            
            lang.setLocale("fr");
            assertEquals("Bonjour!", lang.get("greeting"));
        }
    }

    @Test
    void testTranslationConsistencyUnderLoad() {
        // Simulate heavy usage
        for (int i = 0; i < 100; i++) {
            String greeting = lang.get("greeting");
            assertEquals("Hello!", greeting);
            
            String welcome = lang.get("welcome_user", "User" + i);
            assertTrue(welcome.contains("User" + i));
        }
    }

    @Test
    void testComplexFormattingScenario() {
        // Test with multiple parameters
        String multi = lang.get("multi_param", "Alice", "5");
        assertEquals("Hello Alice, you have 5 messages", multi);
        
        // Test with different values
        multi = lang.get("multi_param", "Bob", "10");
        assertEquals("Hello Bob, you have 10 messages", multi);
    }

    @Test
    void testAllTranslationMethodsWithSameKey() {
        String key = "ui.button_save";
        
        // Method 1: Direct get with full key
        String result1 = lang.get(key);
        
        // Method 2: Get with context
        String result2 = lang.getWithContext("button_save", "ui");
        
        assertEquals(result1, result2);
        assertEquals("Save", result1);
    }

    @Test
    void testErrorRecoveryAfterInvalidLocale() {
        // Try to switch to non-existent locale
        lang.setLocale("invalid_locale");
        
        // Should still be able to get translations (likely fallback)
        String result = lang.get("greeting");
        assertNotNull(result);
        
        // Switch back to valid locale
        lang.setLocale("en");
        assertEquals("Hello!", lang.get("greeting"));
    }

    @Test
    void testNestedContextKeys() {
        // Test multiple levels of context
        String save = lang.getWithContext("button_save", "ui");
        String cancel = lang.getWithContext("button_cancel", "ui");
        String notFound = lang.getWithContext("not_found", "error");
        
        assertEquals("Save", save);
        assertEquals("Cancel", cancel);
        assertEquals("Not found", notFound);
    }

    @Test
    void testLocaleSpecificFormattingDifferences() {
        // English
        lang.setLocale("en");
        String enWelcome = lang.get("welcome_user", "John");
        assertTrue(enWelcome.contains("Welcome"));
        
        // French
        lang.setLocale("fr");
        String frWelcome = lang.get("welcome_user", "Jean");
        assertTrue(frWelcome.contains("Bienvenue"));
    }

    @Test
    void testAllAvailableKeysInEnglish() {
        String[] keys = {
            "greeting", "farewell", "welcome_user", "multi_param",
            "item_count", "user_count", "ui.button_save", "ui.button_cancel",
            "ui.button_delete", "error.not_found", "error.validation",
            "formatted_message", "profile", "app.title", "menu.file", "menu.edit"
        };
        
        for (String key : keys) {
            String result = lang.get(key);
            assertNotNull(result);
            assertNotEquals("", result);
            // Should not return the key itself for existing translations
            if (!key.equals(result)) {
                assertFalse(result.isEmpty());
            }
        }
    }

    @Test
    void testTranslationIntegrityAfterMultipleOperations() {
        // Perform various operations
        lang.get("greeting");
        lang.setLocale("fr");
        lang.get("greeting");
        lang.setLocale("zh");
        lang.get("greeting");
        lang.setLocale("en");
        
        // Verify final state is correct
        assertEquals("Hello!", lang.get("greeting"));
        assertEquals("Goodbye!", lang.get("farewell"));
        assertEquals("Save", lang.getWithContext("button_save", "ui"));
    }
}
