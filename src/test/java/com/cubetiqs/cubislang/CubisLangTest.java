package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CubisLang translation functionality
 */
class CubisLangTest {

    private CubisLang lang;
    private String testResourcePath;

    @BeforeEach
    void setUp() {
        testResourcePath = "src/test/resources/lang/";
        
        CubisLangOptions options = CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath(testResourcePath)
                .setFallbackLocale("en")
                .setDebugMode(false)
                .build();
        
        lang = new CubisLang(options);
    }

    @Test
    void testSimpleTranslation() {
        String result = lang.get("greeting");
        assertEquals("Hello!", result);
    }

    @Test
    void testTranslationWithSingleParameter() {
        String result = lang.get("welcome_user", "John");
        assertEquals("Welcome, John!", result);
    }

    @Test
    void testTranslationWithMultipleParameters() {
        String result = lang.get("multi_param", "Alice", "5");
        assertEquals("Hello Alice, you have 5 messages", result);
    }

    @Test
    void testMissingKeyReturnsSameKey() {
        String result = lang.get("nonexistent_key");
        assertEquals("nonexistent_key", result);
    }

    @Test
    void testGetCurrentLocale() {
        assertEquals("en", lang.getCurrentLocale());
    }

    @Test
    void testSetLocale() {
        lang.setLocale("fr");
        assertEquals("fr", lang.getCurrentLocale());
        
        String result = lang.get("greeting");
        assertEquals("Bonjour!", result);
    }

    @Test
    void testLocaleSwitching() {
        // Start with English
        String englishGreeting = lang.get("greeting");
        assertEquals("Hello!", englishGreeting);
        
        // Switch to French
        lang.setLocale("fr");
        String frenchGreeting = lang.get("greeting");
        assertEquals("Bonjour!", frenchGreeting);
        
        // Switch back to English
        lang.setLocale("en");
        String englishGreeting2 = lang.get("greeting");
        assertEquals("Hello!", englishGreeting2);
    }

    @Test
    void testContextBasedTranslation() {
        String result = lang.getWithContext("button_save", "ui");
        assertEquals("Save", result);
    }

    @Test
    void testMultipleContextKeys() {
        String save = lang.getWithContext("button_save", "ui");
        String cancel = lang.getWithContext("button_cancel", "ui");
        String delete = lang.getWithContext("button_delete", "ui");
        
        assertEquals("Save", save);
        assertEquals("Cancel", cancel);
        assertEquals("Delete", delete);
    }

    @Test
    void testPluralWithSingleItem() {
        String result = lang.getPlural("item_count", 1);
        assertEquals("You have 1 item(s)", result);
    }

    @Test
    void testPluralWithMultipleItems() {
        String result = lang.getPlural("item_count", 5);
        // The implementation replaces items -> item only for count==1
        assertEquals("You have 5 item(s)", result);
    }

    @Test
    void testPluralWithZeroItems() {
        String result = lang.getPlural("user_count", 0);
        assertEquals("0 user(s)", result);
    }

    @Test
    void testKeywordBasedFormatting() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "Alice");
        params.put("date", "2024-06-01");
        
        String result = lang.getWithKeywords("formatted_message", params);
        assertEquals("Hello Alice, today is 2024-06-01", result);
    }

    @Test
    void testKeywordFormattingWithMultipleValues() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Bob");
        params.put("age", "25");
        
        String result = lang.getWithKeywords("profile", params);
        assertEquals("Bob is 25 years old", result);
    }

    @Test
    void testFallbackToDefaultLocale() {
        lang.setLocale("zh");
        
        // This key exists in English but not in Chinese
        // With fallback locale set to "en" in setUp, should fallback
        String result = lang.get("farewell");
        assertEquals("Goodbye!", result);
    }

    @Test
    void testPartialTranslationWithFallback() {
        lang.setLocale("zh");
        
        // greeting exists in Chinese
        String greeting = lang.get("greeting");
        assertEquals("你好!", greeting);
        
        // menu.file doesn't exist in Chinese, should fallback to English
        String menuFile = lang.get("menu.file");
        assertEquals("File", menuFile);
    }

    @Test
    void testEmptyParameterHandling() {
        String result = lang.get("welcome_user", "");
        assertEquals("Welcome, !", result);
    }

    @Test
    void testNullKeyHandling() {
        // Null key should throw NullPointerException or be handled
        assertThrows(NullPointerException.class, () -> {
            lang.get((String) null);
        });
    }

    @Test
    void testGetWithNoParameters() {
        String result = lang.get("greeting");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testContextWithNonExistentKey() {
        String result = lang.getWithContext("nonexistent", "ui");
        // Returns the key without context prefix
        assertEquals("nonexistent", result);
    }

    @Test
    void testMultipleTranslationsInSequence() {
        String greeting = lang.get("greeting");
        String farewell = lang.get("farewell");
        String title = lang.get("app.title");
        
        assertEquals("Hello!", greeting);
        assertEquals("Goodbye!", farewell);
        assertEquals("Test Application", title);
    }

    @Test
    void testTranslationConsistencyAfterMultipleCalls() {
        for (int i = 0; i < 10; i++) {
            String result = lang.get("greeting");
            assertEquals("Hello!", result);
        }
    }

    @Test
    void testLocaleChangeAffectsAllSubsequentCalls() {
        lang.setLocale("fr");
        
        String greeting = lang.get("greeting");
        String farewell = lang.get("farewell");
        
        assertEquals("Bonjour!", greeting);
        assertEquals("Au revoir!", farewell);
    }

    @Test
    void testSetKeyValueCalls() {
        lang.set("new_key", "New Value");
        lang.set("menu.edit", "Edit Changed 2");
        lang.set("zh", "menu.edit", "编辑 已更改 2");

        String result = lang.get("new_key");
        assertEquals("New Value", result);

        int effected = lang.commit();
        assertTrue(effected > 0);
    }
}
