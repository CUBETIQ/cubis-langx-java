package com.cubetiqs.cubislang;

/**
 * Functional interface for handling translation loading errors.
 */
@FunctionalInterface
public interface TranslationErrorListener {
    /**
     * Called when an error occurs while loading translations.
     *
     * @param locale the locale for which the error occurred
     * @param error  the error message
     */
    void onTranslationError(String locale, String error);
}
