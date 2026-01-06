# Missing Keys Extraction Feature

## Overview

Added comprehensive methods to extract and identify missing translation keys across locales. This feature is essential for maintaining translation completeness and managing multilingual applications.

## New Methods

### 1. `getAllKeys(String locale)` → `Set<String>`
Get all translation keys from a specific locale, including nested keys using dot notation.

```java
Set<String> enKeys = lang.getAllKeys("en");
// Returns: ["greeting", "farewell", "ui.button.save", "ui.button.cancel", ...]
```

### 2. `findMissingKeys(String referenceLocale, String targetLocale)` → `Set<String>`
Find which keys exist in the reference locale but are missing in the target locale.

```java
Set<String> missing = lang.findMissingKeys("en", "fr");
// Returns: ["farewell", "ui.button.cancel", "error.serverError", ...]
```

### 3. `extractMissingKeysWithValues(String referenceLocale, String targetLocale)` → `Map<String, String>`
Get missing keys with their reference locale values as a flat map - perfect for creating translation task lists.

```java
Map<String, String> missingData = lang.extractMissingKeysWithValues("en", "fr");
// Returns: {"farewell": "Goodbye", "ui.button.cancel": "Cancel", ...}
```

### 4. `extractMissingKeysAsJson(String referenceLocale, String targetLocale)` → `JsonObject`
Extract missing keys preserving the nested JSON structure - ideal for creating template files for translators.

```java
JsonObject missingJson = lang.extractMissingKeysAsJson("en", "fr");
// Returns structured JSON preserving nesting:
// {
//   "farewell": "Goodbye",
//   "ui": {
//     "button": {
//       "cancel": "Cancel"
//     }
//   }
// }
```

### 5. `getLoadedLocales()` → `Set<String>`
Get all currently loaded locales.

```java
Set<String> locales = lang.getLoadedLocales();
// Returns: ["en", "fr", "zh", "km"]
```

## Use Cases

### 1. Translation Task Management
Generate lists of keys that need translation:

```java
Map<String, String> toTranslate = lang.extractMissingKeysWithValues("en", "fr");
toTranslate.forEach((key, value) -> 
    System.out.println(key + " = " + value + " [TRANSLATE TO FRENCH]")
);
```

### 2. Generate Template Files
Create JSON files for translators with the correct structure:

```java
JsonObject template = lang.extractMissingKeysAsJson("en", "fr");
Files.write(
    Paths.get("missing_fr.json"),
    new Gson().toJson(template).getBytes()
);
```

### 3. CI/CD Completeness Checks
Ensure all locales have complete translations in your build pipeline:

```java
Set<String> missing = lang.findMissingKeys("en", "fr");
if (!missing.isEmpty()) {
    throw new AssertionError("French locale is missing " + missing.size() + " keys!");
}
```

### 4. Translation Progress Reports
Generate reports showing translation coverage:

```java
for (String locale : lang.getLoadedLocales()) {
    Set<String> missing = lang.findMissingKeys("en", locale);
    int total = lang.getAllKeys("en").size();
    int translated = total - missing.size();
    double coverage = (translated * 100.0) / total;
    System.out.printf("%s: %.1f%% complete (%d/%d keys)%n", 
        locale, coverage, translated, total);
}
```

## Implementation Details

- **Nested Key Support**: Handles dot notation (`ui.button.save`) and nested JSON structures
- **Preserves Structure**: `extractMissingKeysAsJson()` maintains the original nesting
- **Thread-Safe**: All methods are safe for concurrent access
- **Efficient**: Uses existing loaded translations without additional I/O

## Testing

- **13 unit tests** in `MissingKeysExtractionTest.java`
- **1 integration test** in `MissingKeysIntegrationTest.java`
- All tests passing ✅

## Example Output

Running `MissingKeysIntegrationTest` demonstrates:

```
=== Missing Keys Extraction Demo ===

1. Loaded locales: [en, fr]

2. All keys in English (reference): 8 keys
   - app_title
   - greeting
   - farewell
   - ui.button.save
   - ui.button.cancel
   - ui.button.delete
   - ui.menu
   - error.not_found
   - error.server_error

3. Missing keys in French: 6 keys
   ❌ farewell
   ❌ ui.button.cancel
   ❌ ui.button.delete
   ❌ ui.menu
   ❌ error.not_found
   ❌ error.server_error

4. Missing keys with reference values (for translators):
   farewell = "Goodbye" → [NEEDS TRANSLATION]
   ui.button.cancel = "Cancel" → [NEEDS TRANSLATION]
   ui.button.delete = "Delete" → [NEEDS TRANSLATION]
   ...

5. Missing keys as JSON (nested structure):
{
  "farewell": "Goodbye",
  "ui": {
    "button": {
      "cancel": "Cancel",
      "delete": "Delete"
    },
    "menu": "Menu"
  },
  "error": {
    "not_found": "Not found",
    "server_error": "Server error"
  }
}

=== Demo Complete ===
✅ Missing keys extracted successfully!
```

## Benefits

- 📋 **Translation Management**: Easily identify what needs translation
- 🔍 **Audit Tool**: Check completeness across all locales
- 📝 **Template Generation**: Create structured files for translators
- ✅ **Quality Assurance**: Verify translation completeness in CI/CD
- 🎯 **Developer Friendly**: Simple API with clear use cases
