package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

/**
 * Example demonstrating the usage of CubisLang translation library with Mart System demo data.
 */
public class SimpleExample {
    public static void main(String[] args) {

        // Initialize CubisLang with options
        CubisLang lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("km") // km, en, zh
                .setResourcePath("./resources/lang/") // Path to language files (/resources/lang/[locale].json)
                .setFallbackLocale("en") // Fallback locale if translation is missing
                .setRemoteTranslationEnabled(true) // Enable/disable remote translation fetching
                .setRemoteTranslationUrl("https://raw.githubusercontent.com/cubetiq/cubis-langx-java/refs/heads/main/demo/martsystem/lang/") // URL for remote translations
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

        String welcomeMessage = lang.get("welcome.message");
        System.out.println("welcome.message: " + welcomeMessage);

        String defaultText = lang.get("This is the default text.");
        System.out.println("non.existent.key: " + defaultText);

        String defaultFormatText = lang.get("This is a formatted text for {{0}}.", "CubisLang");
        System.out.println("non.existent.formatted.key: " + defaultFormatText);
    }
}

