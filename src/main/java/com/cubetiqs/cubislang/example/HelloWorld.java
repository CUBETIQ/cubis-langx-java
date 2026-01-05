package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

import java.util.HashMap;

/**
 * Example demonstrating the usage of CubisLang translation library.
 */
public class HelloWorld {
    public static void main(String[] args) {

        // Initialize CubisLang with options
        CubisLang lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("km") // km, en, zh
                .setResourcePath("./resources/lang/") // Path to language files (/resources/lang/[locale].json)
                .setFallbackLocale("en") // Fallback locale if translation is missing
                .setRemoteTranslationEnabled(true) // Enable/disable remote translation fetching
                .setRemoteTranslationUrl("https://raw.githubusercontent.com/cubetiq/poslangdata/refs/heads/main/martsystem/") // URL for remote translations (must end with a slash, e.g., GitHub raw URL)
                .setEncryptionEnabled(false) // Enable/disable decryption for remote translation files
                .setDecryptionKey("your-decryption-key") // Decryption key for encrypted translation files (if remote translations are encrypted)
                .setCacheRemoteTranslations(true) // Cache remote translations locally
                .setCacheDurationHours(24) // Duration to cache remote translations in hours
                .setCachePath("./resources/cache/lang/") // Path to store cached translation files
                .setDebugMode(true) // Enable/disable debug mode for logging
                .setOnTranslationLoadedListener(locale -> {
                    System.out.println("Translations loaded for locale: " + locale);
                }) // Callback when translations are loaded
                .setOnTranslationErrorListener((locale, error) -> {
                    System.err.println("Error loading translations for locale " + locale + ": " + error);
                }) // Callback when there is an error loading translations
                .setMissingTranslationHandler((locale, key) -> {
                    System.out.println("Missing translation for key '" + key + "' in locale '" + locale + "'");
                }) // Handler for missing translations
                .build()
        );

        System.out.println("\n=== CubisLang Translation Demo ===\n");

        // Example usage
        String greeting = lang.get("greeting");
        System.out.println("greeting: " + greeting); // Assuming the key "greeting" exists in the translation files, if not, it will fallback to "en" locale or show key as value.

        // Example with simple key
        String farewell = lang.get("Hello World!");
        System.out.println("Hello World!: " + farewell); // Assuming the key "Hello World!" exists in the translation files

        // Example with formatting
        String welcome = lang.get("welcome_user", "John"); // Assuming the key "welcome_user" has a placeholder for a name: "Welcome, {{0}}!"
        System.out.println("welcome_user: " + welcome);

        // Example with pluralization
        String itemCountOne = lang.getPlural("item_count", 1); // Assuming the key "item_count" has plural forms: "You have {{count}} item."
        System.out.println("item_count (1): " + itemCountOne);
        String itemCountMany = lang.getPlural("item_count", 5); // Assuming the key "item_count" has plural forms: "You have {{count}} items."
        System.out.println("item_count (5): " + itemCountMany);

        // Example with context
        String saveButton = lang.getWithContext("button_save", "ui"); // Assuming the key "button_save" has different translations based on context
        System.out.println("ui.button_save: " + saveButton);

        // Example with formatting keywords
        String formattedMessage = lang.getWithKeywords("formatted_message", new HashMap<String, String>() {{
            put("username", "Alice");
            put("date", "2024-06-01");
        }}); // Assuming the key "formatted_message" uses keywords like {{username}} and {{date}}
        System.out.println("formatted_message: " + formattedMessage);

        System.out.println("\n--- Changing locale to 'en' ---\n");

        // Change locale
        lang.setLocale("en");
        String newGreeting = lang.get("greeting");
        System.out.println("greeting: " + newGreeting);
        
        String newWelcome = lang.get("welcome_user", "John");
        System.out.println("welcome_user: " + newWelcome);
        
        String newSaveButton = lang.getWithContext("button_save", "ui");
        System.out.println("ui.button_save: " + newSaveButton);

        System.out.println("\n--- Changing locale to 'zh' ---\n");

        // Change to Chinese
        lang.setLocale("zh");
        String zhGreeting = lang.get("greeting");
        System.out.println("greeting: " + zhGreeting);
        
        String zhWelcome = lang.get("welcome_user", "李明");
        System.out.println("welcome_user: " + zhWelcome);

        System.out.println("\n=== Demo Complete ===\n");

        // Note* 
        // - Formatting using Mustache syntax requires the Mustache library to be included in project dependencies (com.github.spullara.mustache.java:compiler:0.9.11).
        // - Remote translation fetching requires internet access using OkHttp HTTP client library.
        // - Encryption/decryption requires the JCE library for cryptographic operations.
        // - Caching requires file I/O permissions to store cached files locally.
        // - Ensure proper error handling for production use.
        // - Ensure this library is optimized and highly maintainable for future updates.
    }
}
