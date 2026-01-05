package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.GoogleTranslateAdapter;
import com.cubetiqs.cubislang.TranslationAdapter;
import com.cubetiqs.cubislang.TranslationFileGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating the new enhancement features:
 * 1. Built-in translation result caching
 * 2. Batch translation support
 * 3. Automatic translation file generation
 */
public class EnhancedFeaturesExample {
    
    public static void main(String[] args) {
        System.out.println("=== CubisLang Enhanced Features Example ===\n");
        
        // Feature 1: Translation Caching
        demonstrateCaching();
        
        // Feature 2: Batch Translation
        demonstrateBatchTranslation();
        
        // Feature 3: Translation File Generation
        demonstrateFileGeneration();
        
        System.out.println("\n=== Example Complete ===");
    }
    
    private static void demonstrateCaching() {
        System.out.println("1. Translation Caching Demo:");
        System.out.println("   -----------------------------");
        
        // Create adapter with caching enabled (default)
        GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(10, true);
        
        System.out.println("   First translation (cache miss):");
        long start = System.currentTimeMillis();
        String result1 = adapter.translate("Hello, how are you?", "en", "es");
        long time1 = System.currentTimeMillis() - start;
        System.out.println("   Result: " + result1);
        System.out.println("   Time: " + time1 + "ms");
        System.out.println("   Cache size: " + adapter.getCacheSize());
        
        System.out.println("\n   Second translation (cache hit):");
        start = System.currentTimeMillis();
        String result2 = adapter.translate("Hello, how are you?", "en", "es");
        long time2 = System.currentTimeMillis() - start;
        System.out.println("   Result: " + result2);
        System.out.println("   Time: " + time2 + "ms (should be much faster!)");
        System.out.println("   Cache size: " + adapter.getCacheSize());
        
        System.out.println();
    }
    
    private static void demonstrateBatchTranslation() {
        System.out.println("2. Batch Translation Demo:");
        System.out.println("   -----------------------------");
        
        GoogleTranslateAdapter adapter = new GoogleTranslateAdapter(10);
        
        List<String> texts = Arrays.asList(
            "Hello",
            "Goodbye",
            "Thank you",
            "You're welcome",
            "Good morning"
        );
        
        System.out.println("   Translating " + texts.size() + " texts in batch:");
        long start = System.currentTimeMillis();
        Map<String, String> results = adapter.translateBatch(texts, "en", "es");
        long time = System.currentTimeMillis() - start;
        
        System.out.println("   Results:");
        for (Map.Entry<String, String> entry : results.entrySet()) {
            System.out.println("     " + entry.getKey() + " → " + entry.getValue());
        }
        System.out.println("   Total time: " + time + "ms");
        System.out.println("   Cache size: " + adapter.getCacheSize());
        
        System.out.println();
    }
    
    private static void demonstrateFileGeneration() {
        System.out.println("3. Translation File Generation Demo:");
        System.out.println("   -----------------------------");
        
        // Create a mock adapter for demonstration (real usage would use GoogleTranslateAdapter)
        TranslationAdapter mockAdapter = new TranslationAdapter() {
            @Override
            public String translate(String text, String sourceLocale, String targetLocale) {
                // Mock translation: just add prefix for demo
                return "[" + targetLocale.toUpperCase() + "] " + text;
            }
            
            @Override
            public Map<String, String> translateBatch(List<String> texts, String sourceLocale, String targetLocale) {
                Map<String, String> results = new java.util.LinkedHashMap<>();
                for (String text : texts) {
                    results.put(text, translate(text, sourceLocale, targetLocale));
                }
                return results;
            }
        };
        
        TranslationFileGenerator generator = new TranslationFileGenerator(mockAdapter);
        
        System.out.println("   Generating translation files from en.json...");
        
        // Generate single file
        System.out.println("\n   a) Generate single file (es.json):");
        boolean success = generator.generateTranslationFile(
            "./resources/lang/en.json",
            "en",
            "es",
            "./output/lang/es.json"
        );
        System.out.println("   Success: " + success);
        
        // Generate multiple files
        System.out.println("\n   b) Generate multiple files:");
        List<String> targetLocales = Arrays.asList("fr", "de", "it");
        Map<String, Boolean> results = generator.generateMultipleTranslationFiles(
            "./resources/lang/en.json",
            "en",
            targetLocales,
            "./output/lang/"
        );
        
        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            System.out.println("     " + entry.getKey() + ".json → " + 
                             (entry.getValue() ? "✓ Success" : "✗ Failed"));
        }
        
        // Batch generation
        System.out.println("\n   c) Batch generation (optimized):");
        boolean batchSuccess = generator.generateTranslationFileBatch(
            "./resources/lang/en.json",
            "en",
            "zh",
            "./output/lang/zh.json"
        );
        System.out.println("   Success: " + batchSuccess);
        System.out.println("   (Uses batch translation for better performance)");
        
        // Merge translations
        System.out.println("\n   d) Merge into existing file:");
        boolean mergeSuccess = generator.mergeTranslationFile(
            "./resources/lang/en.json",
            "./resources/lang/km.json",
            "en",
            "km"
        );
        System.out.println("   Success: " + mergeSuccess);
        System.out.println("   (Only translates missing keys)");
        
        System.out.println();
    }
}
