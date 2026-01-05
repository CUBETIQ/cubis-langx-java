package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

import java.util.Arrays;

/**
 * Example demonstrating the combined locales feature.
 * This feature allows you to display translations from multiple languages
 * in a single result, separated by a configurable separator.
 */
public class CombinedLocalesExample {
    
    public static void main(String[] args) {
        System.out.println("=== CubisLang Combined Locales Example ===\n");
        
        // Basic combined locales example
        basicCombinedLocales();
        
        // Custom separator
        customSeparator();
        
        // Handling missing translations
        handlingMissingTranslations();
        
        // Combined locales with formatting
        combinedWithFormatting();
        
        System.out.println("\n=== Example Complete ===");
    }
    
    private static void basicCombinedLocales() {
        System.out.println("1. Basic Combined Locales (English + Khmer):");
        System.out.println("   -----------------------------------------");
        
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator(" / ")
                        .build()
        );
        
        // This will return "Hello / សួស្តី"
        String greeting = lang.get("greeting");
        System.out.println("   greeting: " + greeting);
        System.out.println();
    }
    
    private static void customSeparator() {
        System.out.println("2. Custom Separator (English | Chinese | Khmer):");
        System.out.println("   ---------------------------------------------");
        
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "zh", "km"))
                        .setCombineSeparator(" | ")
                        .build()
        );
        
        // This will return "Hello | 你好 | សួស្តី"
        String greeting = lang.get("greeting");
        System.out.println("   greeting: " + greeting);
        
        // You can also use no separator
        CubisLang langNoSep = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator("")
                        .build()
        );
        
        String greetingNoSep = langNoSep.get("greeting");
        System.out.println("   greeting (no separator): " + greetingNoSep);
        System.out.println();
    }
    
    private static void handlingMissingTranslations() {
        System.out.println("3. Handling Missing Translations:");
        System.out.println("   -------------------------------");
        
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "km", "zh"))
                        .setCombineSeparator(" / ")
                        .build()
        );
        
        // If a translation exists in only some locales, only those are shown
        // For example, if "farewell" exists in en and zh but not km:
        // Result: "Goodbye / 再见" (km is skipped)
        System.out.println("   Partial translations: Shows only found locales");
        
        // If a translation doesn't exist in any locale, returns the key
        String missing = lang.get("nonexistent_key");
        System.out.println("   Missing key: " + missing + " (returns the key itself)");
        System.out.println();
    }
    
    private static void combinedWithFormatting() {
        System.out.println("4. Combined Locales with Formatting:");
        System.out.println("   ----------------------------------");
        
        CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "zh"))
                        .setCombineSeparator(" / ")
                        .build()
        );
        
        // Formatting works with combined locales too
        // If "welcome" template is "Welcome {{0}}" in en and "欢迎 {{0}}" in zh
        // Result: "Welcome John / 欢迎 John"
        String welcome = lang.get("welcome", "John");
        System.out.println("   welcome with name: " + welcome);
        System.out.println();
    }
    
    /**
     * Use case examples
     */
    private static void useCaseExamples() {
        System.out.println("5. Real-World Use Cases:");
        System.out.println("   --------------------");
        
        // Use Case 1: Multilingual Product Labels
        System.out.println("   a) Product Labels (for international packaging):");
        CubisLang productLang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "fr", "zh", "km"))
                        .setCombineSeparator(" • ")
                        .build()
        );
        
        String productName = productLang.get("product.name");
        System.out.println("      Product: " + productName);
        // Example output: "Apple Juice • Jus de pomme • 苹果汁 • ទឹកផ្លែប៉ោម"
        
        // Use Case 2: Tourist Information Signs
        System.out.println("\n   b) Tourist Information (multilingual signs):");
        CubisLang touristLang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "km"))
                        .setCombineSeparator("\n      ")
                        .build()
        );
        
        String directions = touristLang.get("sign.entrance");
        System.out.println("      Sign:\n      " + directions);
        // Example output:
        // Entrance
        // ច្រកចូល
        
        // Use Case 3: Language Learning Apps
        System.out.println("\n   c) Language Learning (showing translations side-by-side):");
        CubisLang learningLang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./resources/lang/")
                        .setCombineLocales(Arrays.asList("en", "zh"))
                        .setCombineSeparator(" = ")
                        .build()
        );
        
        String lesson = learningLang.get("greeting");
        System.out.println("      Lesson: " + lesson);
        // Example output: "Hello = 你好"
        
        System.out.println();
    }
}
