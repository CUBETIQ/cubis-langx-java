package com.cubetiqs.cubislang;

/**
 * Functional interface for handling missing translations.
 */
@FunctionalInterface
public interface MissingTranslationHandler {
    /**
     * Called when a translation key is not found.
     *
     * @param locale the locale in which the translation was not found
     * @param key    the missing translation key
     */
    void onMissingTranslation(String locale, String key);
}
