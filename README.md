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
        // Initialize with options
        CubisLang lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .setDebugMode(true)
                .build()
        );

        // Get translation
        String greeting = lang.get("greeting");
        System.out.println(greeting); // Output: Hello!
    }
}
```

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

## Advanced Configuration

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

| Option                     | Type    | Default                   | Description                                 |
| -------------------------- | ------- | ------------------------- | ------------------------------------------- |
| `defaultLocale`            | String  | "en"                      | Default locale to use                       |
| `resourcePath`             | String  | "./resources/lang/"       | Path to local translation files             |
| `fallbackLocale`           | String  | "en"                      | Fallback locale when translation is missing |
| `remoteTranslationEnabled` | boolean | false                     | Enable remote translation fetching          |
| `remoteTranslationUrl`     | String  | null                      | Base URL for remote translations            |
| `encryptionEnabled`        | boolean | false                     | Enable decryption for remote files          |
| `decryptionKey`            | String  | null                      | Decryption key (AES)                        |
| `cacheRemoteTranslations`  | boolean | true                      | Cache remote translations locally           |
| `cacheDurationHours`       | int     | 24                        | Cache validity duration                     |
| `cachePath`                | String  | "./resources/cache/lang/" | Cache storage path                          |
| `debugMode`                | boolean | false                     | Enable debug logging                        |

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
