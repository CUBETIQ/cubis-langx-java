# CubisLang - Java Translation SDK

A powerful and flexible translation (i18n) library for Java applications including console, Swing, and JavaFX applications. CubisLang supports loading translations from local JSON files and remote CDN sources with caching, encryption, and many advanced features.

## Features

✨ **Key Features:**

-   🌍 Multi-locale support with fallback mechanism
-   📁 Load translations from local JSON files
-   🌐 Fetch translations from remote CDN/URLs
-   💾 Smart caching system with configurable duration
-   🔒 Support for encrypted translation files
-   🔄 Cache revalidation with version query parameters
-   📝 Multiple formatting options (positional, keyword-based)
-   🔢 Pluralization support
-   🎯 Context-aware translations
-   🎧 Event listeners for loading and error handling
-   🐛 Debug mode for development
-   ⚡ Thread-safe and optimized for performance
-   🌐 **Combined locales** - Display multiple languages in one result
-   🚀 **Async locale preloading** - Non-blocking background loading for faster access
-   🔍 **Missing keys extraction** - Identify and extract untranslated keys across locales
-   📝 **Write missing keys to file** - Async batch writing of missing keys directly to locale files
-   🧹 **Resource cleanup** - AutoCloseable with proper shutdown

✨ **Auto-Translation Features:**

-   🤖 **Auto-translation support** with Google Translate (free API)
-   🔌 **Extensible adapter system** for custom translation services
-   💨 **Built-in caching** - Dramatically improves performance
-   ⚡ **Batch translation** - Translate multiple texts in one request
-   🔧 **Translation file generator** - Automatically create translation files

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.cubetiqs:cubis-langx:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.cubetiqs</groupId>
    <artifactId>cubis-langx</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### 1. Create Translation Files

Create JSON files for each locale in your resources folder:

**resources/lang/en.json:**

```json
{
    "greeting": "Hello!",
    "welcome_user": "Welcome, {{0}}!",
    "item_count": "You have {{count}} items.",
    "formatted_message": "Hello {{username}}, today is {{date}}.",
    "ui": {
        "button_save": "Save",
        "button_cancel": "Cancel"
    }
}
```

**resources/lang/km.json:**

```json
{
    "greeting": "សួស្តី!",
    "welcome_user": "សូមស្វាគមន៍, {{0}}!",
    "item_count": "អ្នកមាន {{count}} ធាតុ."
}
```

### 2. Initialize CubisLang

```java
import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

public class MyApp {
    public static void main(String[] args) {
        // Option 1: Manual resource management
        CubisLang lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(true)
                .build()
        );

        try {
            // Get translation
            String greeting = lang.get("greeting");
            System.out.println(greeting); // Output: Hello!
        } finally {
            // Clean up resources when done
            lang.shutdown();
        }

        // Option 2: Try-with-resources (recommended)
        try (CubisLang lang2 = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .build()
        )) {
            String greeting = lang2.get("greeting");
            System.out.println(greeting);
        } // Automatically calls shutdown()
    }
}
```

### 3. Preload Locales for Faster Access (Optional)

For better performance, preload locales asynchronously on startup:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .setPreloadLocales(Arrays.asList("km", "zh", "fr"))
        .build()
);

// Constructor returns immediately (non-blocking)
// Locales load in the background for faster access later
```

**Benefits:**

-   ⚡ **Non-blocking** - Constructor returns immediately
-   🚀 **Faster switching** - Preloaded locales are instantly available
-   🎯 **Optimized startup** - Load what you need in the background
-   💾 **Smart caching** - Skips already-loaded locales

## Usage Examples

### Basic Translation

```java
// Simple translation
String greeting = lang.get("greeting");

// With positional arguments
String welcome = lang.get("welcome_user", "John");
// Output: Welcome, John!
```

### Pluralization

```java
String itemCount1 = lang.getPlural("item_count", 1);
// Output: You have 1 item.

String itemCount5 = lang.getPlural("item_count", 5);
// Output: You have 5 items.
```

### Context-Based Translation

```java
String saveButton = lang.getWithContext("button_save", "ui");
// Looks for "ui.button_save" in translation files
```

### Keyword-Based Formatting (Mustache)

```java
Map<String, String> keywords = new HashMap<>();
keywords.put("username", "Alice");
keywords.put("date", "2024-06-01");

String message = lang.getWithKeywords("formatted_message", keywords);
// Output: Hello Alice, today is 2024-06-01.
```

### Changing Locale

```java
lang.setLocale("km"); // Switch to Khmer
String greeting = lang.get("greeting");
// Output: សួស្តី!

lang.setLocale("en"); // Switch back to English
```

### Resource Management

CubisLang implements `AutoCloseable` for proper resource cleanup:

```java
// Option 1: Try-with-resources (recommended)
try (CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .build()
)) {
    String greeting = lang.get("greeting");
    // Resources automatically cleaned up
}

// Option 2: Manual cleanup
CubisLang lang = new CubisLang(...);
try {
    String greeting = lang.get("greeting");
} finally {
    lang.shutdown(); // or lang.close()
}
```

**What gets cleaned up:**

-   🔌 HTTP client connections and thread pools
-   💾 Translation caches
-   🧹 Background preloader threads

**When to call shutdown:**

-   ✅ Application exit
-   ✅ Servlet context destroyed
-   ✅ Spring bean destruction
-   ✅ When CubisLang is no longer needed

### Combined Locales

Display translations from multiple languages in a single result - perfect for multilingual labels, tourist signs, or language learning apps:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .setCombineLocales(Arrays.asList("en", "km", "zh"))
        .setCombineSeparator(" / ")
        .build()
);

String greeting = lang.get("greeting");
// Output: Hello / សួស្តី / 你好

// Custom separator
.setCombineSeparator(" | ")  // Output: Hello | សួស្តី | 你好

// Works with formatting too
String welcome = lang.get("welcome", "John");
// Output: Welcome John / សូមស្វាគមន៍ John / 欢迎 John
```

**Smart handling of missing translations:**

-   If a translation exists in only some locales, only those are shown
-   If no translations are found in any locale, returns the key
-   Example: If "farewell" exists in English and Chinese but not Khmer: `"Goodbye / 再见"`

**Use cases:**

-   🏷️ Multilingual product labels (international packaging)
-   🗺️ Tourist information signs (show multiple languages)
-   📚 Language learning apps (display side-by-side translations)
-   🌍 International business cards and documents

## Advanced Configuration

### Auto-Translation with Google Translate

Automatically translate missing keys using Google Translate's free API:

```java
import com.cubetiqs.cubislang.GoogleTranslateAdapter;

CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("km") // Khmer as primary
        .setFallbackLocale("en") // English as fallback
        .setResourcePath("./resources/lang/")
        .setAutoTranslateEnabled(true)
        .setTranslationAdapter(new GoogleTranslateAdapter(10)) // 10 second timeout
        .build()
);

// If "farewell" doesn't exist in Khmer, it will auto-translate from English
String farewell = lang.get("farewell");
// Output: លាហើយ! (automatically translated from "Goodbye!")
```

**How it works:**

1. Looks for the key in the current locale (km)
2. If not found, looks in the fallback locale (en)
3. If found in fallback, **returns the fallback value directly** (no translation)
4. If auto-translation is enabled and the key exists in fallback, you get the fallback text
5. Auto-translation is most useful when you want on-demand translation without pre-creating translation files

**Note:** This uses Google Translate's unofficial/free API. For production use with high volume, consider using the official Google Cloud Translation API or other translation services.

### Custom Translation Adapter

Create your own translation adapter for any translation service:

```java
import com.cubetiqs.cubislang.TranslationAdapter;

public class MyCustomAdapter implements TranslationAdapter {
    @Override
    public String translate(String text, String sourceLocale, String targetLocale) {
        // Call your translation API (Microsoft Translator, DeepL, AWS Translate, etc.)
        // For example, using Microsoft Translator:
        return callMicrosoftTranslator(text, sourceLocale, targetLocale);
    }

    @Override
    public boolean isAvailable() {
        return true; // Check if your service is available
    }
}

// Use your custom adapter
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setAutoTranslateEnabled(true)
        .setTranslationAdapter(new MyCustomAdapter())
        .build()
);
```

See [CustomTranslationAdapter.java](src/main/java/com/cubetiqs/cubislang/example/CustomTranslationAdapter.java) for a complete example.

### Remote Translation Loading

Load translations from a CDN or remote URL:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setRemoteTranslationEnabled(true)
        .setRemoteTranslationUrl("https://cdn.example.com/translations/")
        .setCacheRemoteTranslations(true)
        .setCacheDurationHours(24)
        .setCachePath("./cache/lang/")
        .build()
);
```

The library will fetch translations from:

-   `https://cdn.example.com/translations/en.json`
-   `https://cdn.example.com/translations/km.json`
-   etc.

### Encrypted Translations

For sensitive translations, enable encryption:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setRemoteTranslationEnabled(true)
        .setRemoteTranslationUrl("https://secure.example.com/translations/")
        .setEncryptionEnabled(true)
        .setDecryptionKey("your-secret-key-16ch")
        .build()
);
```

### Event Listeners

Monitor translation loading:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setOnTranslationLoadedListener(locale -> {
            System.out.println("Loaded: " + locale);
        })
        .setOnTranslationErrorListener((locale, error) -> {
            System.err.println("Error loading " + locale + ": " + error);
        })
        .setMissingTranslationHandler((locale, key) -> {
            System.out.println("Missing key '" + key + "' in " + locale);
        })
        .build()
);
```

## Configuration Options

| Option                     | Type    | Default                   | Description                                        |
| -------------------------- | ------- | ------------------------- | -------------------------------------------------- |
| `defaultLocale`            | String  | "en"                      | Default locale to use                              |
| `resourcePath`             | String  | "./resources/lang/"       | Path to local translation files                    |
| `fallbackLocale`           | String  | "en"                      | Fallback locale when translation is missing        |
| `remoteTranslationEnabled` | boolean | false                     | Enable remote translation fetching                 |
| `remoteTranslationUrl`     | String  | null                      | Base URL for remote translations                   |
| `encryptionEnabled`        | boolean | false                     | Enable decryption for remote files                 |
| `decryptionKey`            | String  | null                      | Decryption key (AES)                               |
| `cacheRemoteTranslations`  | boolean | true                      | Cache remote translations locally                  |
| `cacheDurationHours`       | int     | 24                        | Cache validity duration                            |
| `cachePath`                | String  | "./resources/cache/lang/" | Cache storage path                                 |
| `debugMode`                | boolean | false                     | Enable debug logging                               |
| `autoTranslateEnabled`     | boolean | false                     | Enable auto-translation for missing keys           |
| `translationAdapter`       | Object  | null                      | Translation adapter (e.g., GoogleTranslateAdapter) |

## JSON Translation Format

### Simple Key-Value

```json
{
    "key": "value",
    "greeting": "Hello!",
    "goodbye": "Goodbye!"
}
```

### Nested Structure

```json
{
    "ui": {
        "button_save": "Save",
        "button_cancel": "Cancel"
    },
    "error": {
        "not_found": "Not found",
        "invalid": "Invalid input"
    }
}
```

### Placeholders

```json
{
    "welcome": "Welcome, {{0}}!",
    "message": "Hello {{username}}, you have {{count}} messages."
}
```

### Extract Missing Keys

Identify and extract missing translation keys - useful for maintaining translations across multiple locales:

```java
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;

try (CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .build()
)) {
    // Load the locales you want to compare
    lang.setLocale("en");
    lang.setLocale("fr");

    // Find which keys are missing in French
    Set<String> missingKeys = lang.findMissingKeys("en", "fr");
    System.out.println("Missing keys: " + missingKeys);
    // Output: [farewell, thanks, ui.button.cancel]

    // Get missing keys with their English values (as a template)
    Map<String, String> missingWithValues = lang.extractMissingKeysWithValues("en", "fr");
    System.out.println("Missing translations:");
    missingWithValues.forEach((key, value) ->
        System.out.println(key + " = " + value + " [NEEDS TRANSLATION]")
    );

    // Extract as JSON preserving nested structure
    JsonObject missingJson = lang.extractMissingKeysAsJson("en", "fr");
    System.out.println(missingJson);
    // Can be saved to a file for translators:
    // Files.write(Paths.get("missing_fr.json"),
    //     new Gson().toJson(missingJson).getBytes());
}
```

**Practical use cases:**

-   📋 Generate translation task lists for translators
-   🔍 Audit which locales need updates
-   📝 Create template files with missing keys
-   ✅ CI/CD checks to ensure all locales are complete

### Write Missing Keys to Locale Files

Automatically track and write missing translation keys directly to locale files with async batch processing - perfect for development and continuous translation workflows:

```java
try (CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .setWriteMissingKeysToFile(true) // Enable async batch writing
        .setMissingKeysBatchSize(100) // Flush when 100 keys collected
        .setMissingKeysFlushIntervalSeconds(30) // Or every 30 seconds
        .build()
)) {
    // As you use translations, missing keys are automatically tracked
    String greeting = lang.get("missing_greeting");
    String farewell = lang.get("missing_farewell");

    // Keys are batched and written asynchronously to en.json
    // The file is updated with missing keys added as empty values:
    // {
    //   "hello": "Hello",
    //   "world": "World",
    //   "missing_greeting": "",
    //   "missing_farewell": ""
    // }

    // Manually trigger flush if needed
    lang.flushMissingKeys();

    // Switch locale
    lang.setLocale("km");
    lang.get("some_key"); // Missing keys written to km.json

} // Automatic final flush on close
```

**Benefits:**

-   🚀 **Non-blocking** - Runs in background thread, doesn't slow down your app
-   📦 **Batch processing** - Collects keys and writes in batches for efficiency
-   🔄 **Automatic flush** - Writes on shutdown or when batch size reached
-   📝 **Direct to locale files** - Missing keys added to `en.json`, `km.json`, etc.
-   🎯 **No duplicates** - Each key recorded only once per locale
-   ✨ **Pretty printed JSON** - Maintains readable format in locale files

**Perfect for:**

-   🔨 Development environments - Automatically collect missing keys as you code
-   🌍 Continuous translation - Translators can fill in empty values in real locale files
-   📊 Translation progress tracking - See which keys need translation
-   🚀 Production monitoring - Track missing keys in live applications

## Use Cases

### Console Applications

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .build()
);

System.out.println(lang.get("app_title"));
```

### Swing Applications

```java
JButton saveButton = new JButton(lang.getWithContext("button_save", "ui"));
JLabel welcomeLabel = new JLabel(lang.get("welcome_user", username));
```

### JavaFX Applications

```java
Button saveButton = new Button(lang.getWithContext("button_save", "ui"));
Label welcomeLabel = new Label(lang.get("welcome_user", username));
```

## Best Practices

1. **Use fallback locale**: Always set a fallback locale (usually English)
2. **Cache remote translations**: Enable caching to reduce network requests
3. **Handle missing translations**: Implement a missing translation handler
4. **Use nested keys**: Organize translations with nested structures for better management
5. **Debug mode in development**: Enable debug mode during development to catch issues early
6. **Version your remote translations**: Add version query parameters to CDN URLs for cache busting

## Thread Safety

CubisLang is thread-safe and can be used in multi-threaded applications. The translation cache uses `ConcurrentHashMap` to ensure safe concurrent access.

## Performance

-   Translations are loaded on-demand and cached in memory
-   Remote translations are cached locally to minimize network requests
-   JSON parsing is optimized using Gson
-   Thread-safe concurrent access to translation cache

## Dependencies

-   **Gson**: JSON parsing
-   **OkHttp**: HTTP client for remote fetching
-   **Mustache.java**: Template rendering for keyword formatting
-   **SLF4J**: Logging abstraction

## License

MIT License - see LICENSE file for details

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or contributions, please visit:

-   GitHub: https://github.com/cubetiq/cubis-langx-java
-   Issues: https://github.com/cubetiq/cubis-langx-java/issues

---

**Made with ❤️ by Cubis**
