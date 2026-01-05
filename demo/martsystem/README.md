# Mart System Demo Translations

This directory contains demo translation files for a retail/point-of-sale (POS) mart system. These files demonstrate the comprehensive i18n capabilities of CubisLang SDK.

## Available Languages

- **English (en)** - Complete translations
- **Khmer (km)** - Complete translations  
- **Chinese (zh)** - Complete translations

## Remote URL

These translations are hosted on GitHub and can be accessed remotely:

```
https://raw.githubusercontent.com/CUBETIQ/cubis-langx-java/main/demo/martsystem/lang/
```

## Translation Categories

### Application
- App title and welcome messages
- Version information

### User Interface
- Menu items (File, Edit, View, Help, Settings)
- Buttons (Save, Cancel, Delete, Edit, Add, Search, Print, etc.)

### Point of Sale (POS)
- Sale transactions
- Payment processing
- Receipt generation
- Total, subtotal, tax, discount calculations

### Product Management
- Product information (name, code, price, quantity)
- Stock status indicators
- Success messages for operations

### Customer Management
- Customer information fields
- Loyalty points tracking
- Purchase history

### Inventory
- Stock management
- Low stock alerts
- Reorder levels
- Stock in/out operations

### Reports
- Daily and monthly sales reports
- Inventory reports
- Customer reports
- Date range formatting

### Error Messages
- Network errors
- Validation errors
- Permission errors
- Payment failures

### Status Messages
- Success/error notifications
- Confirmation dialogs
- Loading indicators

## Usage Example

```java
CubisLang lang = new CubisLang(
    CubisLangOptions.builder()
        .setDefaultLocale("en")
        .setRemoteTranslationEnabled(true)
        .setRemoteTranslationUrl("https://raw.githubusercontent.com/CUBETIQ/cubis-langx-java/main/demo/martsystem/lang/")
        .setCacheRemoteTranslations(true)
        .build()
);

// Get translations
String appTitle = lang.get("app.title");
String saveButton = lang.getWithContext("button.save", "ui");
String posTitle = lang.get("pos.title");

// With parameters
String welcome = lang.get("welcome_user", "John");
String itemCount = lang.getPlural("inventory.item_count", 5);

// With keywords
Map<String, String> params = new HashMap<>();
params.put("start", "2024-01-01");
params.put("end", "2024-12-31");
String dateRange = lang.getWithKeywords("report.date_range", params);
```

## Key Features Demonstrated

1. **Hierarchical Keys** - Organized by context (app., ui., pos., product., etc.)
2. **Pluralization** - Dynamic count-based translations
3. **Parameter Substitution** - Positional `{{0}}` and named `{{key}}` placeholders
4. **Context-based Translations** - Different translations for different UI contexts
5. **Cultural Localization** - Currency symbols, date formats, and culturally appropriate greetings

## File Structure

```
demo/martsystem/lang/
├── en.json    # English translations
├── km.json    # Khmer translations
└── zh.json    # Chinese translations
```

## Adding New Languages

To add a new language:

1. Create a new JSON file: `[locale].json` (e.g., `fr.json` for French)
2. Copy the structure from `en.json`
3. Translate all values while keeping keys unchanged
4. Test with: `lang.setLocale("fr")`

## Best Practices

- **Keep keys consistent** across all language files
- **Use descriptive key names** that indicate the context
- **Test with actual data** to ensure formatting works correctly
- **Include all placeholders** in translated strings
- **Maintain the same hierarchy** in all language files

## Testing

Run the demo example:

```bash
./gradlew run
```

This will demonstrate:
- Loading translations from remote URL (with local fallback)
- Switching between languages dynamically
- All translation features (parameters, pluralization, context, keywords)

## Production Use

For production deployments:

1. Host translation files on a CDN
2. Enable caching to reduce network requests
3. Set appropriate fallback locales
4. Implement error listeners for monitoring
5. Use missing translation handlers to track incomplete translations

---

For more information, see the [main README](../../README.md) and [Usage Guide](../../USAGE_GUIDE.md).
