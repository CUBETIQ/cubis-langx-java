package com.cubetiqs.cubislang;

import java.util.List;

/**
 * Configuration options for CubisLang translation system.
 * Use the builder pattern to create instances.
 */
public class CubisLangOptions {
    private final String defaultLocale;
    private final String resourcePath;
    private final String fallbackLocale;
    private final boolean remoteTranslationEnabled;
    private final String remoteTranslationUrl;
    private final boolean encryptionEnabled;
    private final String decryptionKey;
    private final boolean cacheRemoteTranslations;
    private final int cacheDurationHours;
    private final String cachePath;
    private final boolean debugMode;
    private final TranslationLoadedListener onTranslationLoadedListener;
    private final TranslationErrorListener onTranslationErrorListener;
    private final MissingTranslationHandler missingTranslationHandler;
    private final boolean autoTranslateEnabled;
    private final TranslationAdapter translationAdapter;
    private Boolean combineLocalesEnabled = null; // default: depends on whether combineLocales is set
    private final List<String> combineLocales;
    private final String combineSeparator;
    private final List<String> preloadLocales;

    private CubisLangOptions(Builder builder) {
        this.defaultLocale = builder.defaultLocale;
        this.resourcePath = builder.resourcePath;
        this.fallbackLocale = builder.fallbackLocale;
        this.remoteTranslationEnabled = builder.remoteTranslationEnabled;
        this.remoteTranslationUrl = builder.remoteTranslationUrl;
        this.encryptionEnabled = builder.encryptionEnabled;
        this.decryptionKey = builder.decryptionKey;
        this.cacheRemoteTranslations = builder.cacheRemoteTranslations;
        this.cacheDurationHours = builder.cacheDurationHours;
        this.cachePath = builder.cachePath;
        this.debugMode = builder.debugMode;
        this.onTranslationLoadedListener = builder.onTranslationLoadedListener;
        this.onTranslationErrorListener = builder.onTranslationErrorListener;
        this.missingTranslationHandler = builder.missingTranslationHandler;
        this.autoTranslateEnabled = builder.autoTranslateEnabled;
        this.translationAdapter = builder.translationAdapter;

        this.combineLocalesEnabled = builder.combineLocalesEnabled;
        this.combineLocales = builder.combineLocales;
        this.combineSeparator = builder.combineSeparator;
        this.preloadLocales = builder.preloadLocales;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getDefaultLocale() { return defaultLocale; }
    public String getResourcePath() { return resourcePath; }
    public String getFallbackLocale() { return fallbackLocale; }
    public boolean isRemoteTranslationEnabled() { return remoteTranslationEnabled; }
    public String getRemoteTranslationUrl() { return remoteTranslationUrl; }
    public boolean isEncryptionEnabled() { return encryptionEnabled; }
    public String getDecryptionKey() { return decryptionKey; }
    public boolean isCacheRemoteTranslations() { return cacheRemoteTranslations; }
    public int getCacheDurationHours() { return cacheDurationHours; }
    public String getCachePath() { return cachePath; }
    public boolean isDebugMode() { return debugMode; }
    public TranslationLoadedListener getOnTranslationLoadedListener() { return onTranslationLoadedListener; }
    public TranslationErrorListener getOnTranslationErrorListener() { return onTranslationErrorListener; }
    public MissingTranslationHandler getMissingTranslationHandler() { return missingTranslationHandler; }
    public boolean isAutoTranslateEnabled() { return autoTranslateEnabled; }
    public TranslationAdapter getTranslationAdapter() { return translationAdapter; }
    public boolean isCombineLocalesEnabled() { 
        if (combineLocalesEnabled == null) {
            return combineLocales != null && !combineLocales.isEmpty();
        }
        return combineLocalesEnabled;
     }
    public List<String> getCombineLocales() { return combineLocales; }
    public String getCombineSeparator() { return combineSeparator; }
    public List<String> getPreloadLocales() { return preloadLocales; }

    /**
     * Builder class for CubisLangOptions.
     */
    public static class Builder {
        private String defaultLocale = "en";
        private String resourcePath = "./resources/lang/";
        private String fallbackLocale = "en";
        private boolean remoteTranslationEnabled = false;
        private String remoteTranslationUrl = null;
        private boolean encryptionEnabled = false;
        private String decryptionKey = null;
        private boolean cacheRemoteTranslations = true;
        private int cacheDurationHours = 24;
        private String cachePath = "./resources/cache/lang/";
        private boolean debugMode = false;
        private TranslationLoadedListener onTranslationLoadedListener = null;
        private TranslationErrorListener onTranslationErrorListener = null;
        private MissingTranslationHandler missingTranslationHandler = null;
        private boolean autoTranslateEnabled = false;
        private TranslationAdapter translationAdapter = null;
        private Boolean combineLocalesEnabled = null; // default: depends on whether combineLocales is set
        private List<String> combineLocales = null;
        private String combineSeparator = " / ";
        private List<String> preloadLocales = null;

        public Builder setDefaultLocale(String defaultLocale) {
            this.defaultLocale = defaultLocale;
            return this;
        }

        public Builder setResourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public Builder setFallbackLocale(String fallbackLocale) {
            this.fallbackLocale = fallbackLocale;
            return this;
        }

        public Builder setRemoteTranslationEnabled(boolean remoteTranslationEnabled) {
            this.remoteTranslationEnabled = remoteTranslationEnabled;
            return this;
        }

        public Builder setRemoteTranslationUrl(String remoteTranslationUrl) {
            this.remoteTranslationUrl = remoteTranslationUrl;
            return this;
        }

        public Builder setEncryptionEnabled(boolean encryptionEnabled) {
            this.encryptionEnabled = encryptionEnabled;
            return this;
        }

        public Builder setDecryptionKey(String decryptionKey) {
            this.decryptionKey = decryptionKey;
            return this;
        }

        public Builder setCacheRemoteTranslations(boolean cacheRemoteTranslations) {
            this.cacheRemoteTranslations = cacheRemoteTranslations;
            return this;
        }

        public Builder setCacheDurationHours(int cacheDurationHours) {
            this.cacheDurationHours = cacheDurationHours;
            return this;
        }

        public Builder setCachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public Builder setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }

        public Builder setOnTranslationLoadedListener(TranslationLoadedListener listener) {
            this.onTranslationLoadedListener = listener;
            return this;
        }

        public Builder setOnTranslationErrorListener(TranslationErrorListener listener) {
            this.onTranslationErrorListener = listener;
            return this;
        }

        public Builder setMissingTranslationHandler(MissingTranslationHandler handler) {
            this.missingTranslationHandler = handler;
            return this;
        }

        public Builder setAutoTranslateEnabled(boolean autoTranslateEnabled) {
            this.autoTranslateEnabled = autoTranslateEnabled;
            return this;
        }

        public Builder setTranslationAdapter(TranslationAdapter translationAdapter) {
            this.translationAdapter = translationAdapter;
            return this;
        }

        public Builder setCombineLocalesEnabled(Boolean combineLocalesEnabled) {
            this.combineLocalesEnabled = combineLocalesEnabled;
            return this;
        }

        public Builder setCombineLocales(List<String> combineLocales) {
            this.combineLocales = combineLocales;
            return this;
        }

        public Builder setCombineSeparator(String combineSeparator) {
            this.combineSeparator = combineSeparator;
            return this;
        }

        public Builder setPreloadLocales(List<String> preloadLocales) {
            this.preloadLocales = preloadLocales;
            return this;
        }

        public CubisLangOptions build() {
            return new CubisLangOptions(this);
        }
    }
}
