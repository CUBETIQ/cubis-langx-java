package com.cubetiqs.cubislang;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * CubisLang - A flexible translation library for Java applications.
 * Supports local and remote translation loading, caching, encryption, and more.
 */
public class CubisLang {
    private static final Logger logger = LoggerFactory.getLogger(CubisLang.class);
    
    private final CubisLangOptions options;
    private final Map<String, JsonObject> translations;
    private final Map<String, Long> cacheTimestamps;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final MustacheFactory mustacheFactory;
    
    private String currentLocale;

    /**
     * Creates a new CubisLang instance with the given options.
     *
     * @param options the configuration options
     */
    public CubisLang(CubisLangOptions options) {
        this.options = options;
        this.translations = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
        this.currentLocale = options.getDefaultLocale();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.mustacheFactory = new DefaultMustacheFactory();
        
        // Load initial translations for current locale
        loadTranslations(currentLocale);
        
        // Also load fallback locale if it's different from current locale
        if (options.getFallbackLocale() != null && !options.getFallbackLocale().equals(currentLocale)) {
            loadTranslations(options.getFallbackLocale());
        }
    }

    /**
     * Gets the translation for the given key.
     *
     * @param key the translation key
     * @return the translated string, or the key itself if not found
     */
    public String get(String key) {
        return get(key, (Object[]) null);
    }

    /**
     * Gets the translation for the given key with positional arguments.
     *
     * @param key  the translation key
     * @param args the arguments to format the translation with
     * @return the translated and formatted string
     */
    public String get(String key, Object... args) {
        // Check if combine locales is enabled
        if (options.isCombineLocalesEnabled()) {
            return getCombined(key, args);
        }
        
        String translation = getTranslation(currentLocale, key);
        
        if (translation == null) {
            if (options.getMissingTranslationHandler() != null) {
                options.getMissingTranslationHandler().onMissingTranslation(currentLocale, key);
            }
            translation = key;
        }
        
        // Format with positional arguments if provided
        if (args != null && args.length > 0) {
            translation = formatWithPositionalArgs(translation, args);
        }
        
        return translation;
    }

    /**
     * Gets the combined translation for multiple locales.
     * Returns translations from all specified locales separated by the configured separator.
     * If a translation is not found in a locale, it skips that locale.
     * If no translations are found in any locale, returns the key.
     *
     * @param key  the translation key
     * @param args optional formatting arguments
     * @return the combined translations separated by the separator
     */
    public String getCombined(String key, Object... args) {
        List<String> combineLocales = options.getCombineLocales();
        String separator = options.getCombineSeparator();
        
        if (combineLocales == null || combineLocales.isEmpty()) {
            // Fallback to normal get if no combine locales configured
            return get(key, args);
        }
        
        List<String> foundTranslations = new ArrayList<>();
        
        for (String locale : combineLocales) {
            // Ensure translations are loaded for this locale
            if (!translations.containsKey(locale)) {
                loadTranslations(locale);
            }
            
            // Get translation from specific locale without fallback
            String translation = getTranslationFromLocale(locale, key);
            
            if (translation != null) {
                // Format with positional arguments if provided
                if (args != null && args.length > 0) {
                    translation = formatWithPositionalArgs(translation, args);
                }
                foundTranslations.add(translation);
            }
        }
        
        // If no translations found, return the key
        if (foundTranslations.isEmpty()) {
            if (options.getMissingTranslationHandler() != null) {
                for (String locale : combineLocales) {
                    options.getMissingTranslationHandler().onMissingTranslation(locale, key);
                }
            }
            return key;
        }
        
        // Join all found translations with separator
        return String.join(separator, foundTranslations);
    }

    /**
     * Gets translation from a specific locale without fallback.
     * This is used internally for combined locales feature.
     *
     * @param locale the locale to get translation from
     * @param key the translation key
     * @return the translation or null if not found in that specific locale
     */
    private String getTranslationFromLocale(String locale, String key) {
        JsonObject localeTranslations = translations.get(locale);
        
        if (localeTranslations != null) {
            JsonElement element = getNestedValue(localeTranslations, key);
            if (element != null && element.isJsonPrimitive()) {
                return element.getAsString();
            }
        }
        
        return null;
    }

    /**
     * Gets the plural form of a translation based on the count.
     *
     * @param key   the translation key
     * @param count the count to determine plural form
     * @return the translated plural string
     */
    public String getPlural(String key, int count) {
        String translation = getTranslation(currentLocale, key);
        
        if (translation == null) {
            if (options.getMissingTranslationHandler() != null) {
                options.getMissingTranslationHandler().onMissingTranslation(currentLocale, key);
            }
            return key;
        }
        
        // Replace {{count}} placeholder
        translation = translation.replace("{{count}}", String.valueOf(count));
        
        // Simple pluralization: if count is 1, use singular form, otherwise plural
        // You can extend this with more complex pluralization rules
        if (count == 1) {
            translation = translation.replace("items", "item");
        }
        
        return translation;
    }

    /**
     * Gets the translation for a key with a specific context.
     *
     * @param key     the translation key
     * @param context the context (e.g., "ui", "error", "message")
     * @return the translated string with context
     */
    public String getWithContext(String key, String context) {
        String contextKey = context + "." + key;
        String translation = getTranslation(currentLocale, contextKey);
        
        if (translation == null) {
            // Fallback to key without context
            translation = getTranslation(currentLocale, key);
        }
        
        if (translation == null) {
            if (options.getMissingTranslationHandler() != null) {
                options.getMissingTranslationHandler().onMissingTranslation(currentLocale, contextKey);
            }
            return key;
        }
        
        return translation;
    }

    /**
     * Gets the translation with keyword-based formatting using Mustache syntax.
     *
     * @param key      the translation key
     * @param keywords the map of keywords to replace
     * @return the translated and formatted string
     */
    public String getWithKeywords(String key, Map<String, String> keywords) {
        String translation = getTranslation(currentLocale, key);
        
        if (translation == null) {
            if (options.getMissingTranslationHandler() != null) {
                options.getMissingTranslationHandler().onMissingTranslation(currentLocale, key);
            }
            translation = key;
        }
        
        // Use Mustache for keyword replacement
        try {
            Mustache mustache = mustacheFactory.compile(new StringReader(translation), key);
            StringWriter writer = new StringWriter();
            mustache.execute(writer, keywords).flush();
            return writer.toString();
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Error formatting with keywords for key: " + key, e);
            }
            return translation;
        }
    }

    /**
     * Changes the current locale.
     *
     * @param locale the new locale to use
     */
    public void setLocale(String locale) {
        this.currentLocale = locale;
        loadTranslations(locale);
    }

    /**
     * Gets the current locale.
     *
     * @return the current locale
     */
    public String getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Clears all cached translations.
     */
    public void clearCache() {
        translations.clear();
        cacheTimestamps.clear();
        if (options.isDebugMode()) {
            logger.info("Translation cache cleared");
        }
    }

    /**
     * Reloads translations for the current locale.
     */
    public void reload() {
        translations.remove(currentLocale);
        cacheTimestamps.remove(currentLocale);
        loadTranslations(currentLocale);
    }

    // Private helper methods

    private String getTranslation(String locale, String key) {
        JsonObject localeTranslations = translations.get(locale);
        
        if (localeTranslations != null) {
            JsonElement element = getNestedValue(localeTranslations, key);
            if (element != null && element.isJsonPrimitive()) {
                return element.getAsString();
            }
        }
        
        // Fallback to fallback locale
        if (!locale.equals(options.getFallbackLocale())) {
            String fallbackLocale = options.getFallbackLocale();
            JsonObject fallbackTranslations = translations.get(fallbackLocale);
            
            if (fallbackTranslations != null) {
                JsonElement element = getNestedValue(fallbackTranslations, key);
                if (element != null && element.isJsonPrimitive()) {
                    return element.getAsString();
                }
            }
        }
        
        // Auto-translate if enabled and adapter is available
        if (options.isAutoTranslateEnabled() && options.getTranslationAdapter() != null) {
            return autoTranslate(locale, key);
        }
        
        return null;
    }
    
    /**
     * Auto-translates a key using the configured translation adapter.
     * First tries to get the value from the fallback locale, then translates it to the target locale.
     * 
     * @param targetLocale the target locale to translate to
     * @param key the translation key
     * @return the auto-translated text, or null if translation fails
     */
    private String autoTranslate(String targetLocale, String key) {
        TranslationAdapter adapter = options.getTranslationAdapter();
        
        if (adapter == null || !adapter.isAvailable()) {
            return null;
        }
        
        // Get the text from fallback locale
        String fallbackLocale = options.getFallbackLocale();
        JsonObject fallbackTranslations = translations.get(fallbackLocale);
        
        if (fallbackTranslations == null) {
            return null;
        }
        
        JsonElement element = getNestedValue(fallbackTranslations, key);
        if (element == null || !element.isJsonPrimitive()) {
            return null;
        }
        
        String sourceText = element.getAsString();
        
        try {
            String translated = adapter.translate(sourceText, fallbackLocale, targetLocale);
            
            if (translated != null && options.isDebugMode()) {
                logger.info("Auto-translated '{}' from {} to {}: {} -> {}", 
                        key, fallbackLocale, targetLocale, sourceText, translated);
            }
            
            return translated;
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Auto-translation failed for key: " + key, e);
            }
            return null;
        }
    }

    private JsonElement getNestedValue(JsonObject obj, String key) {
        if (obj.has(key)) {
            return obj.get(key);
        }
        
        // Support nested keys with dot notation (e.g., "ui.button_save")
        String[] parts = key.split("\\.");
        JsonObject current = obj;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (current.has(parts[i]) && current.get(parts[i]).isJsonObject()) {
                current = current.get(parts[i]).getAsJsonObject();
            } else {
                return null;
            }
        }
        
        return current.get(parts[parts.length - 1]);
    }

    private String formatWithPositionalArgs(String template, Object[] args) {
        String result = template;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{{" + i + "}}", String.valueOf(args[i]));
        }
        return result;
    }

    private void loadTranslations(String locale) {
        try {
            JsonObject translations = null;
            
            // Try to load from cache first if remote translations are enabled
            if (options.isRemoteTranslationEnabled() && options.isCacheRemoteTranslations()) {
                translations = loadFromCache(locale);
                if (translations != null && !isCacheExpired(locale)) {
                    this.translations.put(locale, translations);
                    if (options.getOnTranslationLoadedListener() != null) {
                        options.getOnTranslationLoadedListener().onTranslationLoaded(locale);
                    }
                    if (options.isDebugMode()) {
                        logger.info("Loaded translations from cache for locale: " + locale);
                    }
                    return;
                }
            }
            
            // Try to load from remote if enabled
            if (options.isRemoteTranslationEnabled()) {
                translations = loadFromRemote(locale);
                if (translations != null) {
                    this.translations.put(locale, translations);
                    
                    // Cache the remote translations
                    if (options.isCacheRemoteTranslations()) {
                        saveToCache(locale, translations);
                    }
                    
                    if (options.getOnTranslationLoadedListener() != null) {
                        options.getOnTranslationLoadedListener().onTranslationLoaded(locale);
                    }
                    if (options.isDebugMode()) {
                        logger.info("Loaded translations from remote for locale: " + locale);
                    }
                    return;
                }
            }
            
            // Fallback to local resource files
            translations = loadFromLocal(locale);
            if (translations != null) {
                this.translations.put(locale, translations);
                if (options.getOnTranslationLoadedListener() != null) {
                    options.getOnTranslationLoadedListener().onTranslationLoaded(locale);
                }
                if (options.isDebugMode()) {
                    logger.info("Loaded translations from local file for locale: " + locale);
                }
            } else {
                String error = "Failed to load translations for locale: " + locale;
                if (options.getOnTranslationErrorListener() != null) {
                    options.getOnTranslationErrorListener().onTranslationError(locale, error);
                }
                if (options.isDebugMode()) {
                    logger.error(error);
                }
            }
            
        } catch (Exception e) {
            String error = "Error loading translations for locale " + locale + ": " + e.getMessage();
            if (options.getOnTranslationErrorListener() != null) {
                options.getOnTranslationErrorListener().onTranslationError(locale, error);
            }
            if (options.isDebugMode()) {
                logger.error(error, e);
            }
        }
    }

    private JsonObject loadFromLocal(String locale) {
        try {
            String filePath = options.getResourcePath() + locale + ".json";
            File file = new File(filePath);
            
            if (!file.exists()) {
                if (options.isDebugMode()) {
                    logger.warn("Local translation file not found: " + filePath);
                }
                return null;
            }
            
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return JsonParser.parseString(content).getAsJsonObject();
            
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Error loading local translations for locale: " + locale, e);
            }
            return null;
        }
    }

    private JsonObject loadFromRemote(String locale) {
        try {
            String url = options.getRemoteTranslationUrl() + locale + ".json";
            
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().string();
                    
                    // Decrypt if encryption is enabled
                    if (options.isEncryptionEnabled() && options.getDecryptionKey() != null) {
                        content = decrypt(content, options.getDecryptionKey());
                    }
                    
                    return JsonParser.parseString(content).getAsJsonObject();
                } else {
                    if (options.isDebugMode()) {
                        logger.warn("Failed to load remote translations: HTTP " + response.code());
                    }
                }
            }
            
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Error loading remote translations for locale: " + locale, e);
            }
        }
        
        return null;
    }

    private JsonObject loadFromCache(String locale) {
        try {
            String cacheFilePath = options.getCachePath() + locale + ".json";
            File cacheFile = new File(cacheFilePath);
            
            if (!cacheFile.exists()) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(cacheFile.toPath()), StandardCharsets.UTF_8);
            cacheTimestamps.put(locale, cacheFile.lastModified());
            return JsonParser.parseString(content).getAsJsonObject();
            
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Error loading cached translations for locale: " + locale, e);
            }
            return null;
        }
    }

    private void saveToCache(String locale, JsonObject translations) {
        try {
            String cacheFilePath = options.getCachePath() + locale + ".json";
            File cacheFile = new File(cacheFilePath);
            
            // Create cache directory if it doesn't exist
            cacheFile.getParentFile().mkdirs();
            
            String content = gson.toJson(translations);
            Files.write(cacheFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
            cacheTimestamps.put(locale, System.currentTimeMillis());
            
            if (options.isDebugMode()) {
                logger.info("Saved translations to cache for locale: " + locale);
            }
            
        } catch (Exception e) {
            if (options.isDebugMode()) {
                logger.error("Error saving translations to cache for locale: " + locale, e);
            }
        }
    }

    private boolean isCacheExpired(String locale) {
        Long timestamp = cacheTimestamps.get(locale);
        if (timestamp == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        long cacheAge = currentTime - timestamp;
        long maxCacheAge = options.getCacheDurationHours() * 60 * 60 * 1000L;
        
        return cacheAge > maxCacheAge;
    }

    private String decrypt(String encryptedContent, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
