package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

import java.util.HashMap;

/**
 * Example demonstrating the usage of CubisLang translation library with Mart System demo data.
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
                .setRemoteTranslationUrl("https://raw.githubusercontent.com/CUBETIQ/cubis-langx-java/refs/heads/main/demo/martsystem/lang/") // URL for remote translations
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

        System.out.println("\n=== CubisLang Translation Demo - Mart System ===\n");

        // Example usage - App info
        String appTitle = lang.get("app.title");
        System.out.println("app.title: " + appTitle);
        
        String appWelcome = lang.get("app.welcome");
        System.out.println("app.welcome: " + appWelcome);

        // Example usage - Basic greetings
        String greeting = lang.get("greeting");
        System.out.println("greeting: " + greeting);

        // Example with formatting
        String welcome = lang.get("welcome_user", "Samnang");
        System.out.println("welcome_user: " + welcome);

        // Example with pluralization
        String itemCountOne = lang.getPlural("inventory.item_count", 1);
        System.out.println("inventory.item_count (1): " + itemCountOne);
        String itemCountMany = lang.getPlural("inventory.item_count", 25);
        System.out.println("inventory.item_count (25): " + itemCountMany);

        // Example with context - UI buttons
        String saveButton = lang.getWithContext("button.save", "ui");
        System.out.println("ui.button.save: " + saveButton);
        
        String deleteButton = lang.getWithContext("button.delete", "ui");
        System.out.println("ui.button.delete: " + deleteButton);

        // Example with POS system
        String posTitle = lang.get("pos.title");
        System.out.println("pos.title: " + posTitle);
        
        // Example with keywords formatting
        String reportDateRange = lang.getWithKeywords("report.date_range", new HashMap<String, String>() {{
            put("start", "2024-01-01");
            put("end", "2024-12-31");
        }});
        System.out.println("report.date_range: " + reportDateRange);

        System.out.println("\n--- Changing locale to 'en' (English) ---\n");

        // Change locale to English
        lang.setLocale("en");
        String enGreeting = lang.get("greeting");
        System.out.println("greeting: " + enGreeting);
        
        String enWelcome = lang.get("welcome_user", "John");
        System.out.println("welcome_user: " + enWelcome);
        
        String enSaveButton = lang.getWithContext("button.save", "ui");
        System.out.println("ui.button.save: " + enSaveButton);
        
        String enPosTitle = lang.get("pos.title");
        System.out.println("pos.title: " + enPosTitle);

        System.out.println("\n--- Changing locale to 'zh' (Chinese) ---\n");

        // Change to Chinese
        lang.setLocale("zh");
        String zhGreeting = lang.get("greeting");
        System.out.println("greeting: " + zhGreeting);
        
        String zhWelcome = lang.get("welcome_user", "李明");
        System.out.println("welcome_user: " + zhWelcome);
        
        String zhAppTitle = lang.get("app.title");
        System.out.println("app.title: " + zhAppTitle);
        
        String zhPosTitle = lang.get("pos.title");
        System.out.println("pos.title: " + zhPosTitle);

        System.out.println("\n=== Demo Complete ===\n");

        // Note* 
        // - Remote translations are loaded from: https://raw.githubusercontent.com/CUBETIQ/cubis-langx-java/main/demo/martsystem/lang/
        // - Formatting using Mustache syntax requires the Mustache library (com.github.spullara.mustache.java:compiler:0.9.11).
        // - Remote translation fetching requires internet access using OkHttp library.
        // - Encryption/decryption requires implementation based on your security requirements.
        // - Caching requires file I/O permissions to store cached files locally.
        // - Ensure proper error handling for production use.
        // - This library is optimized and highly maintainable for future updates.
    }
}

