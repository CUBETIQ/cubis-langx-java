package com.cubetiqs.cubislang;

import java.util.List;
import java.util.Map;

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
     * Translates multiple texts in a single batch request.
     * Default implementation translates each text individually.
     * Override this method to provide optimized batch translation.
     * 
     * @param texts List of texts to translate
     * @param sourceLocale The source language locale
     * @param targetLocale The target language locale
     * @return Map of original text to translated text (null values indicate translation failures)
     */
    default Map<String, String> translateBatch(List<String> texts, String sourceLocale, String targetLocale) {
        Map<String, String> results = new java.util.LinkedHashMap<>();
        for (String text : texts) {
            results.put(text, translate(text, sourceLocale, targetLocale));
        }
        return results;
    }
    
    /**
     * Check if the adapter is available and properly configured.
     * 
     * @return true if the adapter can be used, false otherwise
     */
    default boolean isAvailable() {
        return true;
    }
    
    /**
     * Check if this adapter supports batch translation optimization.
     * 
     * @return true if batch translation is optimized, false if it uses individual translations
     */
    default boolean supportsBatchTranslation() {
        return false;
    }
}

