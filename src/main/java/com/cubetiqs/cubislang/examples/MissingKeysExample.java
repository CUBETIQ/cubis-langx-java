package com.cubetiqs.cubislang.examples;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Example demonstrating how to extract missing translation keys.
 * This is useful for maintaining translations across multiple locales.
 */
public class MissingKeysExample {
    
    public static void main(String[] args) throws IOException {
        // Setup example translation files
        setupExampleTranslations();

        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath("./example-translations/")
                        .setDebugMode(true)
                        .build()
        )) {
            // Load all locales we want to check
            lang.setLocale("en");
            lang.setLocale("fr");
            lang.setLocale("zh");

            System.out.println("=== Missing Keys Extraction Example ===\n");

            // 1. Get all loaded locales
            System.out.println("1. Loaded locales:");
            Set<String> locales = lang.getLoadedLocales();
            System.out.println("   " + locales + "\n");

            // 2. Get all keys from reference locale
            System.out.println("2. All keys in English (reference):");
            Set<String> allKeys = lang.getAllKeys("en");
            allKeys.forEach(key -> System.out.println("   - " + key));
            System.out.println("   Total: " + allKeys.size() + " keys\n");

            // 3. Find missing keys in French
            System.out.println("3. Missing keys in French:");
            Set<String> missingInFrench = lang.findMissingKeys("en", "fr");
            if (missingInFrench.isEmpty()) {
                System.out.println("   ✅ All keys translated!");
            } else {
                missingInFrench.forEach(key -> System.out.println("   ❌ " + key));
                System.out.println("   Missing: " + missingInFrench.size() + " keys\n");
            }

            // 4. Extract missing keys with values (flat map)
            System.out.println("4. Missing keys with reference values (for translators):");
            Map<String, String> missingWithValues = lang.extractMissingKeysWithValues("en", "fr");
            missingWithValues.forEach((key, value) ->
                    System.out.println("   " + key + " = \"" + value + "\" → [TRANSLATE TO FRENCH]")
            );
            System.out.println();

            // 5. Extract as JSON (preserves nested structure)
            System.out.println("5. Missing keys as JSON (preserves structure):");
            JsonObject missingJson = lang.extractMissingKeysAsJson("en", "fr");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(missingJson));
            System.out.println();

            // 6. Save missing keys to file for translators
            Path outputFile = Paths.get("./example-translations/missing_fr.json");
            Files.write(outputFile, gson.toJson(missingJson).getBytes(StandardCharsets.UTF_8));
            System.out.println("6. Saved missing keys to: " + outputFile.toAbsolutePath());
            System.out.println("   Translators can fill in these values!\n");

            // 7. Check another locale (Chinese)
            System.out.println("7. Missing keys in Chinese:");
            Set<String> missingInChinese = lang.findMissingKeys("en", "zh");
            if (missingInChinese.isEmpty()) {
                System.out.println("   ✅ All keys translated!");
            } else {
                missingInChinese.forEach(key -> System.out.println("   ❌ " + key));
                System.out.println("   Missing: " + missingInChinese.size() + " keys");
            }

            System.out.println("\n=== Example Complete ===");
            System.out.println("Use these methods in CI/CD to ensure translation completeness!");
        }
    }

    private static void setupExampleTranslations() throws IOException {
        Path langDir = Paths.get("./example-translations");
        Files.createDirectories(langDir);

        // English (complete reference)
        String enJson = "{\n" +
                "  \"app_title\": \"My Application\",\n" +
                "  \"greeting\": \"Hello\",\n" +
                "  \"farewell\": \"Goodbye\",\n" +
                "  \"ui\": {\n" +
                "    \"button\": {\n" +
                "      \"save\": \"Save\",\n" +
                "      \"cancel\": \"Cancel\",\n" +
                "      \"delete\": \"Delete\"\n" +
                "    },\n" +
                "    \"menu\": \"Menu\"\n" +
                "  },\n" +
                "  \"error\": {\n" +
                "    \"not_found\": \"Not found\",\n" +
                "    \"server_error\": \"Server error\"\n" +
                "  }\n" +
                "}";

        // French (partial - missing some translations)
        String frJson = "{\n" +
                "  \"app_title\": \"Mon Application\",\n" +
                "  \"greeting\": \"Bonjour\",\n" +
                "  \"ui\": {\n" +
                "    \"button\": {\n" +
                "      \"save\": \"Enregistrer\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Chinese (different missing keys)
        String zhJson = "{\n" +
                "  \"greeting\": \"你好\",\n" +
                "  \"farewell\": \"再见\",\n" +
                "  \"ui\": {\n" +
                "    \"menu\": \"菜单\"\n" +
                "  },\n" +
                "  \"error\": {\n" +
                "    \"not_found\": \"未找到\"\n" +
                "  }\n" +
                "}";

        Files.write(langDir.resolve("en.json"), enJson.getBytes(StandardCharsets.UTF_8));
        Files.write(langDir.resolve("fr.json"), frJson.getBytes(StandardCharsets.UTF_8));
        Files.write(langDir.resolve("zh.json"), zhJson.getBytes(StandardCharsets.UTF_8));

        System.out.println("Created example translation files in: " + langDir.toAbsolutePath());
        System.out.println();
    }
}
