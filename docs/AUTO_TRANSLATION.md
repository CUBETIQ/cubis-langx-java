# Auto-Translation Feature

## Overview

CubisLang now supports automatic translation of missing translation keys using external translation services. This feature is built on an extensible adapter pattern that allows you to use Google Translate (free), or integrate with any other translation service.

## Key Components

### 1. TranslationAdapter Interface

The base interface that all translation adapters must implement:

```java
public interface TranslationAdapter {
    String translate(String text, String sourceLocale, String targetLocale);
    default boolean isAvailable() { return true; }
}
```

### 2. GoogleTranslateAdapter

A built-in adapter that uses Google Translate's free (unofficial) API:

```java
GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(10); // 10 second timeout
```

**Features:**

-   Uses Google Translate's public endpoint (no API key required)
-   Configurable timeout
-   Automatic text encoding
-   Error handling and logging

**Limitations:**

-   Unofficial API (may be subject to rate limiting)
-   Not recommended for high-volume production use
-   Consider official Google Cloud Translation API for production

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

-   Reduce timeout value
-   Use a faster translation service
-   Pre-translate frequently used keys
-   Implement caching in your custom adapter

### Rate limiting issues

-   Use official APIs with higher rate limits
-   Implement exponential backoff
-   Cache translation results
-   Pre-translate in batch during build time

## Future Enhancements

Potential improvements for future versions:

-   [ ] Built-in translation result caching
-   [ ] Batch translation support
-   [ ] More built-in adapters (DeepL, Microsoft, AWS)
-   [ ] Translation quality scoring
-   [ ] Automatic translation file generation
-   [ ] Translation memory integration

## License

Same as CubisLang - MIT License

## Support

For issues or questions about auto-translation:

-   GitHub Issues: https://github.com/cubetiq/cubis-langx-java/issues
-   Examples: See `src/main/java/com/cubetiqs/cubislang/example/`
-   Tests: See `src/test/java/com/cubetiqs/cubislang/`
