# Auto-Translation Feature

## Overview

CubisLang now supports automatic translation of missing translation keys using external translation services. This feature is built on an extensible adapter pattern that allows you to use Google Translate (free), or integrate with any other translation service.

## Key Components

### 1. TranslationAdapter Interface

The base interface that all translation adapters must implement:

```java
public interface TranslationAdapter {
    String translate(String text, String sourceLocale, String targetLocale);

    Map<String, String> translateBatch(List<String> texts, String sourceLocale, String targetLocale);

    default boolean isAvailable() { return true; }

    default boolean supportsBatchTranslation() { return false; }
}
```

### 2. GoogleTranslateAdapter

A built-in adapter that uses Google Translate's free (unofficial) API with built-in caching and batch translation support:

```java
// With default settings (10s timeout, caching enabled)
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter();

// With custom timeout
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(15);

// With custom timeout and cache control
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(15, true); // enable caching
```

**Features:**

-   ✨ **Built-in caching** - Automatically caches translations to reduce API calls
-   ⚡ **Batch translation** - Translate multiple texts in one request for better performance
-   🌐 Uses Google Translate's public endpoint (no API key required)
-   ⏱️ Configurable timeout
-   🔒 Thread-safe operations
-   📝 Automatic text encoding
-   🛡️ Error handling and logging

**Caching:**

```java
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(10, true);

// First call - fetches from API and caches
String result1 = adapter.translate("Hello", "en", "es");

// Second call - returns from cache (instant!)
String result2 = adapter.translate("Hello", "en", "es");

// Cache management
int size = adapter.getCacheSize();
adapter.clearCache();
```

**Batch Translation:**

```java
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter();

List<String> texts = Arrays.asList("Hello", "Goodbye", "Thank you");
Map<String, String> results = adapter.translateBatch(texts, "en", "es");

// Results: {"Hello": "Hola", "Goodbye": "Adiós", "Thank you": "Gracias"}
```

**Limitations:**

-   Unofficial API (may be subject to rate limiting)
-   Not recommended for high-volume production use without caching
-   Consider official Google Cloud Translation API for enterprise production

### 3. Custom Adapters

You can create custom adapters for any translation service:

```java
public class MyCustomAdapter implements TranslationAdapter {
    @Override
    public String translate(String text, String sourceLocale, String targetLocale) {
        // Your translation logic here
        // Call Microsoft Translator, DeepL, AWS Translate, etc.
        return translatedText;
    }
}
```

See [CustomTranslationAdapter.java](../src/main/java/com/cubetiqs/cubislang/example/CustomTranslationAdapter.java) for a complete example with:

-   Dictionary-based translation
-   Template for API integration
-   Error handling

## How It Works

When you request a translation with `lang.get("key")`:

1. **Check current locale** - Looks for the key in the current locale's translations
2. **Check fallback locale** - If not found, looks in the fallback locale
3. **Return fallback value** - If found in fallback, returns that value directly
4. **Auto-translate** - Only if key doesn't exist in fallback AND auto-translation is enabled, it will attempt to translate
5. **Return key** - If all else fails, returns the key itself

**Important:** Auto-translation only happens when a key is completely missing from both the current locale and the fallback locale.

## Configuration

Enable auto-translation in your configuration:

```java
CubisLangOptions options = CubisLangOptions.builder()
    .setDefaultLocale("km")               // Primary locale
    .setFallbackLocale("en")              // Fallback locale
    .setAutoTranslateEnabled(true)        // Enable auto-translation
    .setTranslationAdapter(new GoogleTranslateAdapter(10)) // Use Google Translate
    .build();

CubisLang lang = new CubisLang(options);
```

## Usage Examples

### Basic Auto-Translation

```java
// resources/lang/en.json: {"greeting": "Hello", "farewell": "Goodbye"}
// resources/lang/es.json: {"greeting": "Hola"}

CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("es")
        .setFallbackLocale("en")
        .setAutoTranslateEnabled(true)
        .setTranslationAdapter(new GoogleTranslateAdapter())
        .build()
);

String greeting = lang.get("greeting");  // Returns: "Hola" (from es.json)
String farewell = lang.get("farewell");  // Returns: "Goodbye" (from en.json fallback)
String unknown = lang.get("unknown");    // Returns: "unknown" (key itself)
```

### Custom Adapter Integration

```java
// Use DeepL or any other service
TranslationAdapter deeplAdapter = new TranslationAdapter() {
    @Override
    public String translate(String text, String sourceLocale, String targetLocale) {
        // Call DeepL API
        return deepLService.translate(text, sourceLocale, targetLocale);
    }
};

CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setAutoTranslateEnabled(true)
        .setTranslationAdapter(deeplAdapter)
        .build()
);
```

### With Event Handlers

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setAutoTranslateEnabled(true)
        .setTranslationAdapter(new GoogleTranslateAdapter())
        .setMissingTranslationHandler((locale, key) -> {
            System.out.println("Missing: " + key + " in " + locale);
        })
        .setDebugMode(true)  // See auto-translation in action
        .build()
);
```

## Testing

The auto-translation feature includes comprehensive tests:

-   **GoogleTranslateAdapterTest.java** - 13 tests for the Google Translate adapter
-   **AutoTranslationIntegrationTest.java** - 6 integration tests for end-to-end scenarios

Run tests with:

```bash
./gradlew test
```

## Performance Considerations

1. **Network Latency** - Auto-translation requires HTTP requests, which adds latency
2. **Rate Limiting** - Free services may have rate limits
3. **Caching** - Consider caching translated values to avoid repeated translations
4. **Fallback First** - The SDK always tries fallback locale before auto-translation

## Best Practices

1. **Use for Development** - Great for quickly testing new locales without creating translation files
2. **Pre-translate for Production** - For production, pre-translate and store in JSON files
3. **Configure Timeouts** - Set appropriate timeouts for your translation adapter
4. **Handle Errors** - Implement proper error handling in custom adapters
5. **Monitor Usage** - Track auto-translation usage to identify missing translations

## Supported Translation Services

### Built-in

-   ✅ Google Translate (free/unofficial API)

### Can be integrated

-   Microsoft Translator API
-   DeepL API
-   AWS Translate
-   Azure Translator
-   IBM Watson Language Translator
-   Any custom translation service

## Examples

Complete examples are available in the `src/main/java/com/cubetiqs/cubislang/example/` directory:

-   **AutoTranslationExample.java** - Comprehensive demonstration
-   **CustomTranslationAdapter.java** - Template for custom adapters
-   **HelloWorld.java** - Basic usage example

## API Reference

### CubisLangOptions Builder Methods

```java
.setAutoTranslateEnabled(boolean enabled)
.setTranslationAdapter(TranslationAdapter adapter)
```

### TranslationAdapter Interface

```java
String translate(String text, String sourceLocale, String targetLocale);
boolean isAvailable();
```

### GoogleTranslateAdapter Constructors

```java
new GoogleTranslateAdapter()                    // Default 10 second timeout
new GoogleTranslateAdapter(int timeoutSeconds)  // Custom timeout
new GoogleTranslateAdapter(OkHttpClient client) // Custom HTTP client
```

## Troubleshooting

### Auto-translation not working

-   Check that `autoTranslateEnabled` is set to `true`
-   Verify that `translationAdapter` is not `null`
-   Ensure the key exists in the fallback locale
-   Check debug mode logs for error messages

### Slow performance

-   ✅ **Enable caching** - GoogleTranslateAdapter has built-in caching (enabled by default)
-   Use batch translation for multiple texts
-   Reduce timeout value for faster failures
-   Pre-translate frequently used keys using TranslationFileGenerator

### Rate limiting issues

-   ✅ **Use caching** to reduce API calls (built-in with GoogleTranslateAdapter)
-   ✅ **Use batch translation** to reduce number of API requests
-   Use official APIs with higher rate limits
-   Implement exponential backoff
-   Pre-translate in batch during build time using TranslationFileGenerator

## Translation File Generator

The `TranslationFileGenerator` utility helps you automatically create translation files from a source language file.

### Basic Usage

```java
// Create generator with your translation adapter
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter();
TranslationFileGenerator generator = new TranslationFileGenerator(adapter);

// Generate single translation file
generator.generateTranslationFile(
    "./resources/lang/en.json",   // Source file
    "en",                          // Source locale
    "es",                          // Target locale
    "./resources/lang/es.json"     // Output file
);
```

### Generate Multiple Files

```java
List<String> targetLocales = Arrays.asList("fr", "de", "it", "es", "pt");

Map<String, Boolean> results = generator.generateMultipleTranslationFiles(
    "./resources/lang/en.json",
    "en",
    targetLocales,
    "./resources/lang/"
);

// Check results
for (Map.Entry<String, Boolean> entry : results.entrySet()) {
    System.out.println(entry.getKey() + ": " +
                      (entry.getValue() ? "✓ Success" : "✗ Failed"));
}
```

### Batch Generation (Optimized)

For better performance, use batch generation which translates all keys in one request:

```java
// Uses batch translation for all keys at once
generator.generateTranslationFileBatch(
    "./resources/lang/en.json",
    "en",
    "zh",
    "./resources/lang/zh.json"
);
```

### Merge with Existing Files

Add missing translations to an existing file without overwriting:

```java
// Only translates keys that don't exist in km.json
generator.mergeTranslationFile(
    "./resources/lang/en.json",
    "./resources/lang/km.json",
    "en",
    "km"
);
```

### Features

-   🌐 **Nested JSON support** - Handles complex nested structures
-   ⚡ **Batch translation** - Optimized performance with batch operations
-   🔄 **Merge mode** - Add missing keys without overwriting existing translations
-   📁 **Bulk generation** - Generate multiple language files at once
-   💾 **Automatic file creation** - Creates directories if they don't exist
-   🎯 **Selective translation** - Choose which locales to generate

### Example Output

Source file `en.json`:

```json
{
    "welcome": "Welcome",
    "greeting": {
        "morning": "Good morning",
        "evening": "Good evening"
    }
}
```

Generated `es.json`:

```json
{
    "welcome": "Bienvenido",
    "greeting": {
        "morning": "Buenos días",
        "evening": "Buenas noches"
    }
}
```

## Performance Tips

### 1. Use Caching

Caching is enabled by default in GoogleTranslateAdapter and dramatically improves performance:

```java
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(10, true);

// First call - API request
adapter.translate("Hello", "en", "es"); // ~200ms

// Subsequent calls - cached
adapter.translate("Hello", "en", "es"); // <1ms
```

### 2. Use Batch Translation

When translating multiple texts, batch translation is much faster:

```java
// Slow: Individual translations
for (String text : texts) {
    adapter.translate(text, "en", "es"); // N API calls
}

// Fast: Batch translation
adapter.translateBatch(texts, "en", "es"); // 1 API call
```

### 3. Pre-generate Translation Files

Instead of runtime translation, pre-generate files during build:

```java
// In your build script or initialization
TranslationFileGenerator generator = new TranslationFileGenerator(adapter);
generator.generateTranslationFileBatch(
    "en.json", "en", "es", "es.json"
);
```

## Future Enhancements

Potential improvements for future versions:

-   [x] Built-in translation result caching ✅
-   [x] Batch translation support ✅
-   [x] Automatic translation file generation ✅
-   [ ] More built-in adapters (DeepL, Microsoft, AWS)
-   [ ] Translation quality scoring
-   [ ] Translation memory integration
-   [ ] Persistent disk cache (survive application restarts)
-   [ ] Incremental file generation (only translate new keys)

For issues or questions about auto-translation:

-   GitHub Issues: https://github.com/cubetiq/cubis-langx-java/issues
-   Examples: See `src/main/java/com/cubetiqs/cubislang/example/`
-   Tests: See `src/test/java/com/cubetiqs/cubislang/`
