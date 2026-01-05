package com.cubetiqs.cubislang;

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

        public CubisLangOptions build() {
            return new CubisLangOptions(this);
        }
    }
}
