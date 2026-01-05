# Combined Locales Feature

## Overview

The **Combined Locales** feature allows you to display translations from multiple languages in a single result. This is perfect for applications that need to show multilingual content side-by-side, such as:

-   🏷️ International product packaging labels
-   🗺️ Tourist information signs
-   📚 Language learning applications
-   🌍 Multilingual business documents

## Features

✨ **Key Capabilities:**

-   🌐 Combine translations from 2+ languages in one result
-   🔧 Configurable separator (default: `" / "`)
-   🎯 Smart handling of missing translations (skips missing locales)
-   📝 Works with positional formatting
-   🔄 Returns key if no translations found
-   ⚡ Automatic loading of required locales

## Basic Usage

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setResourcePath("./resources/lang/")
        .setCombineLocalesEnabled(true)
        .setCombineLocales(Arrays.asList("en", "km"))
        .setCombineSeparator(" / ")
        .build()
);

String greeting = lang.get("greeting");
// Output: Hello / សួស្តី
```

## Configuration Options

### 1. setCombineLocales(List<String> locales)

Specifies which locales to combine in the result.

```java
// Two locales
.setCombineLocales(Arrays.asList("en", "km"))
// Result: Hello / សួស្តី

// Three locales
.setCombineLocales(Arrays.asList("en", "km", "zh"))
// Result: Hello / សួស្តី / 你好

// Four or more locales
.setCombineLocales(Arrays.asList("en", "fr", "de", "es"))
// Result: Hello / Bonjour / Hallo / Hola
```

### 2. setCombineSeparator(String separator)

Configures the separator between translations (default: `" / "`).

```java
// Default separator
.setCombineSeparator(" / ")
// Result: Hello / សួស្តី

// Pipe separator
.setCombineSeparator(" | ")
// Result: Hello | សួស្តី

// Dash separator
.setCombineSeparator(" - ")
// Result: Hello - សួស្តី

// Newline for vertical display
.setCombineSeparator("\n")
// Result:
// Hello
// សួស្តី

// No separator (concatenate)
.setCombineSeparator("")
// Result: Helloសួស្តី
```

## Behavior with Missing Translations

The combined locales feature intelligently handles missing translations:

### Scenario 1: Translation exists in all locales

```java
// "greeting" exists in en, km, and zh
String result = lang.get("greeting");
// Output: Hello / សួស្តី / 你好
```

### Scenario 2: Translation exists in some locales

```java
// "farewell" exists in en and fr, but NOT in km
.setCombineLocales(Arrays.asList("en", "km", "fr"))
String result = lang.get("farewell");
// Output: Goodbye / Au revoir
// (km is skipped because translation not found)
```

### Scenario 3: Translation exists in only one locale

```java
// "thanks" exists only in km
.setCombineLocales(Arrays.asList("en", "km", "zh"))
String result = lang.get("thanks");
// Output: អរគុណ
// (only km translation shown)
```

### Scenario 4: Translation doesn't exist in any locale

```java
String result = lang.get("nonexistent_key");
// Output: nonexistent_key
// (returns the key itself)
```

## Advanced Features

### 1. Combined Locales with Formatting

Formatting works seamlessly with combined locales:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "zh"))
        .setCombineSeparator(" / ")
        .build()
);

// If templates are: "Welcome {{0}}" (en) and "欢迎 {{0}}" (zh)
String welcome = lang.get("welcome", "John");
// Output: Welcome John / 欢迎 John
```

### 2. Direct Method Call

You can also call `getCombined()` directly:

```java
String result = lang.getCombined("greeting");
// Same as lang.get("greeting") when combineLocales is set
```

### 3. Missing Translation Handler

The missing translation handler is called for each locale where translation is missing:

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "km", "zh"))
        .setMissingTranslationHandler((locale, key) -> {
            System.out.println("Missing: " + key + " in " + locale);
        })
        .build()
);

lang.get("nonexistent");
// Output:
// Missing: nonexistent in en
// Missing: nonexistent in km
// Missing: nonexistent in zh
```

## Real-World Use Cases

### Use Case 1: International Product Labels

```java
CubisLang productLang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "fr", "de", "es"))
        .setCombineSeparator(" • ")
        .build()
);

String ingredients = productLang.get("product.ingredients");
// Output: Water, Sugar, Natural Flavors • Eau, Sucre, Arômes naturels • Wasser, Zucker, Natürliche Aromen • Agua, Azúcar, Sabores naturales
```

### Use Case 2: Tourist Information Signs

```java
CubisLang signLang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "km"))
        .setCombineSeparator("\n")
        .build()
);

String entrance = signLang.get("sign.entrance");
// Output:
// Entrance
// ច្រកចូល
```

### Use Case 3: Language Learning App

```java
CubisLang learningLang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "target_language"))
        .setCombineSeparator(" = ")
        .build()
);

String lesson = learningLang.get("vocabulary.hello");
// Output: Hello = 你好
// (Shows English and target language side-by-side)
```

### Use Case 4: Multilingual Business Cards

```java
CubisLang businessLang = new CubisLang(
    CubisLangOptions.builder()
        .setCombineLocales(Arrays.asList("en", "zh", "km"))
        .setCombineSeparator(" | ")
        .build()
);

String title = businessLang.get("job.title");
// Output: Software Engineer | 软件工程师 | វិស្វករកម្មវិធី
```

## Implementation Details

### How It Works

1. **Configuration**: Set `combineLocales` and `combineSeparator` in options
2. **Translation Lookup**: For each locale in the list:
    - Load translations if not already loaded
    - Look up the key in that specific locale (no fallback)
    - Add to results if found
3. **Combine Results**: Join all found translations with the separator
4. **Return**: Combined string, or key if no translations found

### Code Flow

```
get("greeting")
  ↓
Check if combineLocales is set
  ↓ YES
getCombined("greeting")
  ↓
For each locale in [en, km, zh]:
  - getTranslationFromLocale(locale, "greeting")
  - Add to results if found
  ↓
Join results with separator
  ↓
Return: "Hello / សួស្តី / 你好"
```

## Testing

The feature includes 14 comprehensive tests covering:

-   ✅ Combining two locales
-   ✅ Combining three locales
-   ✅ Missing translations in one locale
-   ✅ Missing translations in multiple locales
-   ✅ All translations missing
-   ✅ Custom separators
-   ✅ Empty separator
-   ✅ Formatting with combined locales
-   ✅ Missing translation handler
-   ✅ Direct getCombined() method
-   ✅ Single locale in list
-   ✅ Empty locale list
-   ✅ Null locale list

All tests pass successfully.

## API Reference

### CubisLangOptions.Builder

```java
public Builder setCombineLocales(List<String> combineLocales)
```

Sets the list of locales to combine. If set, `get()` will return combined translations from all specified locales.

**Parameters:**

-   `combineLocales` - List of locale codes to combine (e.g., `Arrays.asList("en", "km", "zh")`)

**Returns:** The builder instance for method chaining

---

```java
public Builder setCombineSeparator(String combineSeparator)
```

Sets the separator to use between combined translations.

**Parameters:**

-   `combineSeparator` - String to separate translations (default: `" / "`)

**Returns:** The builder instance for method chaining

### CubisLang

```java
public String getCombined(String key, Object... args)
```

Gets the combined translation for multiple locales.

**Parameters:**

-   `key` - The translation key
-   `args` - Optional formatting arguments

**Returns:** Combined translations separated by the configured separator, or the key if no translations found

**Behavior:**

-   Returns translations from all specified locales
-   Skips locales where translation is not found
-   Returns key if no translations found in any locale
-   Calls missing translation handler for each locale where translation is missing

## Performance Considerations

-   **Locale Loading**: Each locale is loaded on-demand when first accessed
-   **Caching**: Translations are cached after loading
-   **Lookup Efficiency**: O(n) where n is the number of combined locales
-   **Memory**: Minimal overhead - only stores configuration

## Backward Compatibility

✅ **Fully backward compatible**

-   If `combineLocales` is not set, `get()` behaves normally
-   Existing code continues to work without changes
-   Optional feature that must be explicitly enabled

## Examples

See [CombinedLocalesExample.java](../src/main/java/com/cubetiqs/cubislang/example/CombinedLocalesExample.java) for complete working examples.
