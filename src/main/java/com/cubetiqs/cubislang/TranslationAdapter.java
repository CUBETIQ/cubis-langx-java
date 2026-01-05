package com.cubetiqs.cubislang;

/**
 * Interface for translation service adapters.
 * Implement this interface to create custom translation adapters for various translation services.
 */
public interface TranslationAdapter {
    
    /**
     * Translates text from source language to target language.
     * 
     * @param text The text to translate
     * @param sourceLocale The source language locale (e.g., "en", "km", "zh")
     * @param targetLocale The target language locale (e.g., "en", "km", "zh")
     * @return The translated text, or null if translation fails
     */
    String translate(String text, String sourceLocale, String targetLocale);
    
    /**
     * Check if the adapter is available and properly configured.
     * 
     * @return true if the adapter can be used, false otherwise
     */
    default boolean isAvailable() {
        return true;
    }
}
