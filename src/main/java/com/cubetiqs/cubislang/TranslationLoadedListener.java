package com.cubetiqs.cubislang;

/**
 * Functional interface for handling translation loaded events.
 */
@FunctionalInterface
public interface TranslationLoadedListener {
    /**
     * Called when translations are successfully loaded for a locale.
     *
     * @param locale the locale for which translations were loaded
     */
    void onTranslationLoaded(String locale);
}
