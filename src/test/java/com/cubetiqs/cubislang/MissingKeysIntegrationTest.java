package com.cubetiqs.cubislang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Integration test demonstrating the missing keys extraction feature in a real scenario.
 */
public class MissingKeysIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    public void demonstrateMissingKeysExtraction() throws IOException {
        Path langDir = tempDir.resolve("lang");
        Files.createDirectories(langDir);

        // Create English (complete reference)
        Files.write(langDir.resolve("en.json"),
                ("{" +
                        "\"app_title\": \"My Application\"," +
                        "\"greeting\": \"Hello\"," +
                        "\"farewell\": \"Goodbye\"," +
                        "\"ui\": {" +
                        "\"button\": {" +
                        "\"save\": \"Save\"," +
                        "\"cancel\": \"Cancel\"," +
                        "\"delete\": \"Delete\"" +
                        "}," +
                        "\"menu\": \"Menu\"" +
                        "}," +
                        "\"error\": {" +
                        "\"not_found\": \"Not found\"," +
                        "\"server_error\": \"Server error\"" +
                        "}" +
                        "}").getBytes(StandardCharsets.UTF_8));

        // Create French (partial - missing some translations)
        Files.write(langDir.resolve("fr.json"),
                ("{" +
                        "\"app_title\": \"Mon Application\"," +
                        "\"greeting\": \"Bonjour\"," +
                        "\"ui\": {" +
                        "\"button\": {" +
                        "\"save\": \"Enregistrer\"" +
                        "}" +
                        "}" +
                        "}").getBytes(StandardCharsets.UTF_8));

        System.out.println("\n=== Missing Keys Extraction Demo ===\n");

        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir.toString() + "/")
                        .build()
        )) {
            // Load both locales
            lang.setLocale("en");
            lang.setLocale("fr");

            // 1. Get all loaded locales
            System.out.println("1. Loaded locales: " + lang.getLoadedLocales());

            // 2. Get all keys from reference locale
            Set<String> allKeys = lang.getAllKeys("en");
            System.out.println("\n2. All keys in English (reference): " + allKeys.size() + " keys");
            allKeys.forEach(key -> System.out.println("   - " + key));

            // 3. Find missing keys in French
            Set<String> missingInFrench = lang.findMissingKeys("en", "fr");
            System.out.println("\n3. Missing keys in French: " + missingInFrench.size() + " keys");
            missingInFrench.forEach(key -> System.out.println("   ❌ " + key));

            // 4. Extract missing keys with values
            System.out.println("\n4. Missing keys with reference values (for translators):");
            Map<String, String> missingWithValues = lang.extractMissingKeysWithValues("en", "fr");
            missingWithValues.forEach((key, value) ->
                    System.out.println("   " + key + " = \"" + value + "\" → [NEEDS TRANSLATION]")
            );

            // 5. Extract as JSON (preserves nested structure)
            System.out.println("\n5. Missing keys as JSON (nested structure):");
            JsonObject missingJson = lang.extractMissingKeysAsJson("en", "fr");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(missingJson));

            // 6. Save to file (for translators)
            Path outputFile = tempDir.resolve("missing_fr.json");
            Files.write(outputFile, gson.toJson(missingJson).getBytes(StandardCharsets.UTF_8));
            System.out.println("\n6. Saved missing keys to: " + outputFile.getFileName());
            System.out.println("   File content:");
            System.out.println(new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8));

            System.out.println("\n=== Demo Complete ===");
            System.out.println("✅ Missing keys extracted successfully!");
            System.out.println("📝 Use these methods to maintain translation completeness across locales.");
        }
    }
}
