# CubisLang Quick Reference

Quick reference guide for common CubisLang operations.

## Initialization

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .build()
);
```

## Basic Operations

| Operation          | Code                      | Example Output |
| ------------------ | ------------------------- | -------------- |
| Get translation    | `lang.get("key")`         | `"Hello!"`     |
| Change locale      | `lang.setLocale("km")`    | -              |
| Get current locale | `lang.getCurrentLocale()` | `"en"`         |

## Translation Methods

### Simple Translation

```java
String text = lang.get("greeting");
// greeting: "Hello!" → "Hello!"
```

### With Parameters

```java
String text = lang.get("welcome", "John");
// welcome: "Welcome, {{0}}!" → "Welcome, John!"
```

### With Multiple Parameters

```java
String text = lang.get("message", "Alice", "5");
// message: "Hi {{0}}, you have {{1}} messages" → "Hi Alice, you have 5 messages"
```

### With Keywords

```java
Map<String, String> params = new HashMap<>();
params.put("name", "Bob");
params.put("age", "25");
String text = lang.getWithKeywords("profile", params);
// profile: "{{name}} is {{age}} years old" → "Bob is 25 years old"
```

### With Context

```java
String text = lang.getWithContext("save", "ui");
// Looks for key: "ui.save"
```

### Pluralization

```java
String one = lang.getPlural("items", 1);
// items: "{{count}} item(s)" → "1 item"

String many = lang.getPlural("items", 5);
// items: "{{count}} item(s)" → "5 items"
```

## Configuration Options

### Minimal Configuration

```java
CubisLangOptions.builder()
    .setDefaultLocale("en")
    .setResourcePath("./resources/lang/")
    .build()
```

### With Fallback

```java
CubisLangOptions.builder()
    .setDefaultLocale("en")
    .setResourcePath("./resources/lang/")
    .setFallbackLocale("en")
    .build()
```

### With Remote Loading

```java
CubisLangOptions.builder()
    .setDefaultLocale("en")
    .setResourcePath("./resources/lang/")
    .setRemoteTranslationEnabled(true)
    .setRemoteTranslationUrl("https://cdn.example.com/lang/")
    .setCacheRemoteTranslations(true)
    .setCacheDurationHours(24)
    .build()
```

### With Event Listeners

```java
CubisLangOptions.builder()
    .setDefaultLocale("en")
    .setResourcePath("./resources/lang/")
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
```

### Full Configuration

```java
CubisLangOptions.builder()
    .setDefaultLocale("en")
    .setResourcePath("./resources/lang/")
    .setFallbackLocale("en")
    .setRemoteTranslationEnabled(true)
    .setRemoteTranslationUrl("https://cdn.example.com/lang/")
    .setEncryptionEnabled(false)
    .setDecryptionKey("key")
    .setCacheRemoteTranslations(true)
    .setCacheDurationHours(24)
    .setCachePath("./cache/")
    .setDebugMode(true)
    .setOnTranslationLoadedListener(locale -> {})
    .setOnTranslationErrorListener((locale, error) -> {})
    .setMissingTranslationHandler((locale, key) -> {})
    .build()
```

## JSON Format Examples

### Basic

```json
{
    "greeting": "Hello!",
    "farewell": "Goodbye!"
}
```

### With Placeholders

```json
{
    "welcome": "Welcome, {{0}}!",
    "message": "Hi {{0}}, you have {{1}} messages"
}
```

### With Keywords

```json
{
    "profile": "{{name}} is {{age}} years old",
    "formatted": "Hello {{username}}, today is {{date}}"
}
```

### With Context

```json
{
    "ui.button.save": "Save",
    "ui.button.cancel": "Cancel",
    "error.network": "Network error",
    "error.validation": "Validation error"
}
```

### With Pluralization

```json
{
    "item_count": "{{count}} item(s)",
    "user_count": "{{count}} user(s)"
}
```

## Common Patterns

### Singleton

```java
public class I18n {
    private static CubisLang instance;

    public static CubisLang get() {
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
String text = I18n.get().get("greeting");
```

### Locale Switcher

```java
public void switchLocale(String locale) {
    lang.setLocale(locale);
    updateUI(); // Refresh UI components
}
```

### UI Update on Locale Change

```java
CubisLangOptions.builder()
    .setOnTranslationLoadedListener(locale -> {
        SwingUtilities.invokeLater(this::updateUI);
    })
    .build()
```

## Key Naming Conventions

| Type        | Pattern               | Example                 |
| ----------- | --------------------- | ----------------------- |
| UI Elements | `ui.component.action` | `ui.button.save`        |
| Errors      | `error.category.type` | `error.network.timeout` |
| Messages    | `message.type.detail` | `message.success.saved` |
| Labels      | `label.field`         | `label.username`        |
| Menu        | `menu.section.item`   | `menu.file.open`        |

## Dependencies

```gradle
dependencies {
    implementation 'com.cubetiqs:cubis-langx-java:1.0.0'
}
```

## File Structure

```
resources/
└── lang/
    ├── en.json
    ├── km.json
    └── zh.json
```

## Troubleshooting Quick Fixes

| Problem                        | Solution                                            |
| ------------------------------ | --------------------------------------------------- |
| Translations not loading       | Enable debug mode: `.setDebugMode(true)`            |
| Missing translations           | Set fallback: `.setFallbackLocale("en")`            |
| Remote loading fails           | Enable caching: `.setCacheRemoteTranslations(true)` |
| Keys showing instead of values | Check JSON file exists and is valid                 |

## Example Application Flow

```java
public class App {
    private CubisLang lang;

    public void init() {
        // 1. Initialize
        lang = new CubisLang(
            CubisLangOptions.builder()
                .setDefaultLocale("en")
                .setResourcePath("./resources/lang/")
                .setFallbackLocale("en")
                .build()
        );

        // 2. Use translations
        showWelcome();
    }

    public void showWelcome() {
        String title = lang.get("app.title");
        String greeting = lang.get("greeting");
        System.out.println(title + ": " + greeting);
    }

    public void changeLanguage(String locale) {
        lang.setLocale(locale);
        showWelcome(); // Refresh display
    }
}
```

## API Cheat Sheet

```java
// Initialize
CubisLang lang = new CubisLang(options);

// Get translation
lang.get("key")
lang.get("key", "param1", "param2", ...)

// Advanced
lang.getWithKeywords("key", mapOfParams)
lang.getWithContext("key", "context")
lang.getPlural("key", count)

// Locale management
lang.setLocale("locale")
lang.getCurrentLocale()
```

---

For detailed documentation, see [USAGE_GUIDE.md](USAGE_GUIDE.md)
