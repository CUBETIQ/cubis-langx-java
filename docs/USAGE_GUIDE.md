# CubisLang Usage Guide

This guide provides detailed information on how to use CubisLang in your Java applications.

## Table of Contents

-   [Getting Started](#getting-started)
-   [Configuration Options](#configuration-options)
-   [Translation File Format](#translation-file-format)
-   [Basic Usage](#basic-usage)
-   [Advanced Features](#advanced-features)
-   [Integration Examples](#integration-examples)
-   [Best Practices](#best-practices)
-   [Troubleshooting](#troubleshooting)

## Getting Started

### 1. Add Dependency

Add CubisLang to your project's dependencies.

**Gradle:**

```gradle
dependencies {
    implementation 'com.cubetiqs:cubis-langx-java:1.0.0'
}
```

**Maven:**

```xml
<dependency>
    <groupId>com.cubetiqs</groupId>
    <artifactId>cubis-langx-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Create Translation Files

Create a directory for your translation files (e.g., `resources/lang/`):

```
resources/
└── lang/
    ├── en.json
    ├── km.json
    └── zh.json
```

### 3. Initialize CubisLang

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .build()
);
```

## Configuration Options

### Complete Configuration Example

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        // Locale Settings
        .setDefaultLocale("en")           // Required: Initial locale
        .setFallbackLocale("en")          // Optional: Fallback for missing translations

        // Local Files
        .setResourcePath("./resources/lang/")  // Required: Path to JSON files

        // Remote Translation
        .setRemoteTranslationEnabled(true)     // Optional: Enable CDN loading
        .setRemoteTranslationUrl("https://cdn.example.com/lang/")  // CDN URL

        // Caching
        .setCacheRemoteTranslations(true)      // Optional: Cache remote files
        .setCacheDurationHours(24)             // Optional: Cache lifetime
        .setCachePath("./resources/cache/")    // Optional: Cache directory

        // Security
        .setEncryptionEnabled(false)           // Optional: Enable decryption
        .setDecryptionKey("your-key")          // Optional: Decryption key

        // Development
        .setDebugMode(true)                    // Optional: Enable logging

        // Event Listeners
        .setOnTranslationLoadedListener(locale -> {
            System.out.println("Loaded: " + locale);
        })
        .setOnTranslationErrorListener((locale, error) -> {
            System.err.println("Error: " + error);
        })
        .setMissingTranslationHandler((locale, key) -> {
            System.out.println("Missing: " + key);
        })
        .build()
);
```

### Option Details

| Option                     | Type    | Required | Description                      |
| -------------------------- | ------- | -------- | -------------------------------- |
| `defaultLocale`            | String  | Yes      | Initial locale to load           |
| `resourcePath`             | String  | Yes      | Path to local translation files  |
| `fallbackLocale`           | String  | No       | Fallback locale for missing keys |
| `remoteTranslationEnabled` | boolean | No       | Enable remote loading            |
| `remoteTranslationUrl`     | String  | No       | CDN base URL                     |
| `cacheRemoteTranslations`  | boolean | No       | Cache remote translations        |
| `cacheDurationHours`       | int     | No       | Hours to cache files             |
| `cachePath`                | String  | No       | Cache directory path             |
| `encryptionEnabled`        | boolean | No       | Enable decryption                |
| `decryptionKey`            | String  | No       | Decryption key                   |
| `debugMode`                | boolean | No       | Enable debug logging             |

## Translation File Format

### Basic Structure

```json
{
    "key": "value",
    "nested.key": "nested value",
    "greeting": "Hello!",
    "farewell": "Goodbye!"
}
```

### With Placeholders

```json
{
    "welcome_user": "Welcome, {{0}}!",
    "multi_param": "Hello {{0}}, you have {{1}} messages"
}
```

Usage:

```java
String msg = lang.get("welcome_user", "John");
// Output: "Welcome, John!"

String msg2 = lang.get("multi_param", "Alice", "5");
// Output: "Hello Alice, you have 5 messages"
```

### With Keywords

```json
{
    "formatted_message": "Hello {{username}}, today is {{date}}"
}
```

Usage:

```java
Map<String, String> params = new HashMap<>();
params.put("username", "Bob");
params.put("date", "2024-01-05");

String msg = lang.getWithKeywords("formatted_message", params);
// Output: "Hello Bob, today is 2024-01-05"
```

### With Context

```json
{
    "ui.button_save": "Save",
    "ui.button_cancel": "Cancel",
    "error.not_found": "Not found",
    "error.invalid_input": "Invalid input"
}
```

Usage:

```java
String saveBtn = lang.getWithContext("button_save", "ui");
// Looks for key: "ui.button_save"

String errorMsg = lang.getWithContext("not_found", "error");
// Looks for key: "error.not_found"
```

### With Pluralization

```json
{
    "item_count": "You have {{count}} item(s)"
}
```

Usage:

```java
String one = lang.getPlural("item_count", 1);
// Output: "You have 1 item."

String many = lang.getPlural("item_count", 5);
// Output: "You have 5 items."
```

## Basic Usage

### Simple Translation

```java
String text = lang.get("greeting");
```

### Change Locale

```java
// Change to different locale
lang.setLocale("km");

// Get current locale
String current = lang.getCurrentLocale();
```

### Check if Key Exists

```java
String text = lang.get("some_key");
// If key doesn't exist, returns the key itself or fallback value
```

## Advanced Features

### 1. Remote Translation Loading

Load translations from a CDN with automatic caching:

```java
CubisLangOptions.builder()
    .setRemoteTranslationEnabled(true)
    .setRemoteTranslationUrl("https://cdn.example.com/translations/")
    .setCacheRemoteTranslations(true)
    .setCacheDurationHours(24)
    .build()
```

How it works:

1. First attempts to load from remote URL: `https://cdn.example.com/translations/en.json?v=<timestamp>`
2. If successful, caches the file locally
3. On next load, checks cache validity (within 24 hours)
4. If cache expired or remote fails, falls back to local files

### 2. Encryption/Decryption

For secure remote translations:

```java
CubisLangOptions.builder()
    .setEncryptionEnabled(true)
    .setDecryptionKey("your-32-character-secret-key!")
    .build()
```

**Note:** You'll need to implement the decryption logic based on your encryption method.

### 3. Event Handling

#### Translation Loaded Event

```java
.setOnTranslationLoadedListener(locale -> {
    System.out.println("Translations loaded for: " + locale);
    // Update UI, refresh components, etc.
})
```

#### Error Handling

```java
.setOnTranslationErrorListener((locale, error) -> {
    logger.error("Failed to load translations for {}: {}", locale, error);
    // Show user notification, log to monitoring service, etc.
})
```

#### Missing Translation Handler

```java
.setMissingTranslationHandler((locale, key) -> {
    logger.warn("Missing translation: {} in locale: {}", key, locale);
    // Track missing keys for translation team
    // Send to analytics
})
```

### 4. Debug Mode

Enable detailed logging during development:

```java
CubisLangOptions.builder()
    .setDebugMode(true)
    .build()
```

Logs include:

-   Translation file loading
-   Cache operations
-   Remote fetching
-   Fallback usage
-   Missing keys

## Integration Examples

### Console Application

```java
public class ConsoleApp {
    private static CubisLang lang;

    public static void main(String[] args) {
        // Initialize
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .build()
        );

        // Display menu
        System.out.println(lang.get("menu.title"));
        System.out.println("1. " + lang.get("menu.option1"));
        System.out.println("2. " + lang.get("menu.option2"));

        // User input
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        // Process with translations
        if (choice.equals("1")) {
            System.out.println(lang.get("message.option1_selected"));
        }
    }
}
```

### Swing Application

```java
public class SwingApp extends JFrame {
    private CubisLang lang;
    private JLabel statusLabel;
    private JButton actionButton;

    public SwingApp() {
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setOnTranslationLoadedListener(locale -> {
                    SwingUtilities.invokeLater(this::updateUI);
                })
                .build()
        );

        initComponents();
    }

    private void initComponents() {
        statusLabel = new JLabel();
        actionButton = new JButton();

        JComboBox<String> localeSelector = new JComboBox<>(
            new String[]{"en", "km", "zh"}
        );
        localeSelector.addActionListener(e -> {
            String selected = (String) localeSelector.getSelectedItem();
            lang.setLocale(selected);
        });

        // Layout and add components...
        updateUI();
    }

    private void updateUI() {
        setTitle(lang.get("app.title"));
        statusLabel.setText(lang.get("status.ready"));
        actionButton.setText(lang.getWithContext("button_action", "ui"));
    }
}
```

### JavaFX Application

```java
public class JavaFXApp extends Application {
    private CubisLang lang;

    @Override
    public void start(Stage primaryStage) {
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setOnTranslationLoadedListener(locale -> {
                    Platform.runLater(() -> updateUI(primaryStage));
                })
                .build()
        );

        // Create UI...
        updateUI(primaryStage);
    }

    private void updateUI(Stage stage) {
        stage.setTitle(lang.get("app.title"));
        // Update other UI components...
    }
}
```

## Best Practices

### 1. Singleton Pattern

Create a single instance and share it:

```java
public class TranslationManager {
    private static CubisLang instance;

    public static CubisLang getInstance() {
        if (instance == null) {
            instance = new CubisLang(
                CubisLangOptions.builder()
                    .setDefaultLocale("en")
                    .setResourcePath("./resources/lang/")
                    .build()
            );
        }
        return instance;
    }
}

// Usage
String text = TranslationManager.getInstance().get("greeting");
```

### 2. Key Naming Conventions

Use consistent, hierarchical naming:

```json
{
    "app.title": "My Application",
    "ui.button.save": "Save",
    "ui.button.cancel": "Cancel",
    "error.network.timeout": "Network timeout",
    "error.network.offline": "No internet connection",
    "message.success.saved": "Saved successfully"
}
```

### 3. Handle Missing Translations

Always provide fallback values or use the missing translation handler:

```java
.setMissingTranslationHandler((locale, key) -> {
    // Log for translation team
    logger.warn("Missing: {} in {}", key, locale);

    // Track in analytics
    analytics.track("missing_translation", key, locale);
})
```

### 4. Test All Locales

Ensure all supported locales have complete translations:

```java
@Test
public void testAllTranslations() {
    String[] locales = {"en", "km", "zh"};
    String[] requiredKeys = {"greeting", "farewell", "ui.button_save"};

    for (String locale : locales) {
        lang.setLocale(locale);
        for (String key : requiredKeys) {
            String value = lang.get(key);
            assertNotEquals(key, value,
                "Missing translation for " + key + " in " + locale);
        }
    }
}
```

### 5. Optimize Performance

-   Load translations lazily
-   Cache frequently used translations
-   Use debug mode only in development

### 6. Error Handling

Always implement error listeners in production:

```java
.setOnTranslationErrorListener((locale, error) -> {
    // Log to monitoring service
    ErrorLogger.log("Translation error", locale, error);

    // Show user-friendly message
    showNotification("Language loading failed, using default");

    // Fall back to default locale
    lang.setLocale("en");
})
```

## Troubleshooting

### Problem: Translations not loading

**Solutions:**

1. Check file path is correct
2. Verify JSON file format is valid
3. Enable debug mode to see logs
4. Check file permissions

```java
// Enable debug logging
.setDebugMode(true)
```

### Problem: Missing translations show keys

**Solutions:**

1. Verify the key exists in JSON file
2. Check locale is correct
3. Ensure fallback locale is set
4. Use missing translation handler

```java
.setFallbackLocale("en")
.setMissingTranslationHandler((locale, key) -> {
    logger.warn("Missing: {}", key);
})
```

### Problem: Remote translations fail to load

**Solutions:**

1. Check internet connection
2. Verify URL is correct and accessible
3. Enable caching for offline support
4. Check firewall/proxy settings

```java
.setCacheRemoteTranslations(true)
.setOnTranslationErrorListener((locale, error) -> {
    logger.error("Remote load failed: {}", error);
})
```

### Problem: Performance issues

**Solutions:**

1. Disable debug mode in production
2. Optimize JSON file size
3. Use caching for remote translations
4. Reduce number of translation keys

### Problem: Special characters not displaying

**Solutions:**

1. Ensure JSON files are UTF-8 encoded
2. Check console/UI supports Unicode
3. Verify font supports the character set

## Additional Resources

-   [README.md](README.md) - Quick start guide
-   [CHANGELOG.md](CHANGELOG.md) - Version history
-   [GitHub Repository](https://github.com/cubetiq/cubis-langx-java)
-   [Issue Tracker](https://github.com/cubetiq/cubis-langx-java/issues)

## Support

For questions or issues:

-   Open an issue on GitHub
-   Email: oss@cubetiqs.com
-   Documentation: https://github.com/cubetiq/cubis-langx-java

---

Last updated: January 5, 2026
