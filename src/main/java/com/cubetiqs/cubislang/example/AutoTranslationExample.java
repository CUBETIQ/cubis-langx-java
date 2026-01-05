package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;
import com.cubetiqs.cubislang.GoogleTranslateAdapter;

/**
 * Example demonstrating auto-translation feature.
 * This example shows how missing translations can be automatically translated
 * using Google Translate.
 */
public class AutoTranslationExample {
    
    public static void main(String[] args) {
        System.out.println("=== CubisLang Auto-Translation Example ===\n");
        
        // Configure CubisLang with auto-translation enabled
        CubisLangOptions options = CubisLangOptions.builder()
                .setResourcePath("./resources/lang/")
                .setDefaultLocale("km") // Khmer as primary
                .setFallbackLocale("en") // English as fallback
                .setAutoTranslateEnabled(true) // Enable auto-translation
                .setTranslationAdapter(new GoogleTranslateAdapter(10)) // 10 second timeout
                .setDebugMode(true)
                .setMissingTranslationHandler((locale, key) -> {
                    System.out.println("[MISSING] Translation for '" + key + "' in locale '" + locale + "'");
                })
                .build();
        
        CubisLang lang = new CubisLang(options);
        
        // Example 1: Existing translation (should use direct translation)
        System.out.println("1. Existing translation:");
        System.out.println("   greeting (km): " + lang.get("greeting"));
        System.out.println();
        
        // Example 2: Missing translation (should auto-translate from English to Khmer)
        System.out.println("2. Auto-translation (if key missing in Khmer):");
        // Note: If this key doesn't exist in km.json but exists in en.json,
        // it will be auto-translated
        System.out.println("   farewell (km): " + lang.get("farewell"));
        System.out.println();
        
        // Example 3: Switch to Chinese and demonstrate auto-translation
        System.out.println("3. Switch to Chinese with auto-translation:");
        lang.setLocale("zh");
        System.out.println("   app.title (zh): " + lang.get("app.title"));
        System.out.println("   greeting (zh): " + lang.get("greeting"));
        System.out.println();
        
        // Example 4: Demonstrate with a key that only exists in English
        System.out.println("4. Key only in English (auto-translate to current locale):");
        System.out.println("   welcome.message (zh): " + lang.get("welcome.message"));
        System.out.println();
        
        // Example 5: Custom translation adapter
        System.out.println("5. Using custom translation adapter:");
        CubisLangOptions customOptions = CubisLangOptions.builder()
                .setResourcePath("./resources/lang/")
                .setDefaultLocale("es") // Spanish
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(true)
                .setTranslationAdapter((text, sourceLocale, targetLocale) -> {
                    // Simple mock adapter for demonstration
                    return "[AUTO-TRANSLATED from " + sourceLocale + " to " + targetLocale + "]: " + text;
                })
                .build();
        
        CubisLang customLang = new CubisLang(customOptions);
        System.out.println("   Custom adapter result: " + customLang.get("greeting"));
        System.out.println();
        
        // Example 6: Disable auto-translation
        System.out.println("6. Without auto-translation (fallback behavior):");
        CubisLangOptions noAutoOptions = CubisLangOptions.builder()
                .setResourcePath("./resources/lang/")
                .setDefaultLocale("km")
                .setFallbackLocale("en")
                .setAutoTranslateEnabled(false) // Disabled
                .build();
        
        CubisLang noAutoLang = new CubisLang(noAutoOptions);
        System.out.println("   farewell (km, no auto): " + noAutoLang.get("farewell"));
        System.out.println("   (This should show English fallback, not Khmer translation)");
        
        System.out.println("\n=== Example Complete ===");
    }
}
