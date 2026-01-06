package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.CubisLang;
import com.cubetiqs.cubislang.CubisLangOptions;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Example demonstrating the write missing keys to file feature.
 * This example shows how missing translation keys are automatically
 * written to locale files with empty values for translators to fill in.
 */
public class WriteMissingKeysExample {
    public static void main(String[] args) throws Exception {
        // Create temporary directory for demo
        String tempDir = Files.createTempDirectory("cubislang_demo").toString();
        String langDir = tempDir + "/lang/";
        new File(langDir).mkdirs();

        // Create initial en.json with some existing translations
        String initialEnContent = "{\n" +
                "  \"hello\": \"Hello\",\n" +
                "  \"world\": \"World\",\n" +
                "  \"welcome\": \"Welcome\"\n" +
                "}";
        Files.write(Paths.get(langDir + "en.json"), initialEnContent.getBytes(StandardCharsets.UTF_8));

        System.out.println("=".repeat(70));
        System.out.println("CubisLang - Write Missing Keys to File Demo");
        System.out.println("=".repeat(70));
        System.out.println("\nInitial en.json content:");
        System.out.println(initialEnContent);

        // Initialize CubisLang with write missing keys enabled
        try (CubisLang lang = new CubisLang(
                CubisLangOptions.builder()
                        .setDefaultLocale("en")
                        .setResourcePath(langDir)
                        .setWriteMissingKeysToFile(true) // Enable the feature
                        .setMissingKeysBatchSize(3) // Small batch for demo
                        .setMissingKeysFlushIntervalSeconds(10)
                        .setDebugMode(true) // See what's happening
                        .build()
        )) {
            System.out.println("\n" + "-".repeat(70));
            System.out.println("Requesting translations (some exist, some are missing):");
            System.out.println("-".repeat(70));

            // Request existing translations
            System.out.println("hello: " + lang.get("hello"));
            System.out.println("world: " + lang.get("world"));

            // Request missing translations
            System.out.println("goodbye: " + lang.get("goodbye")); // Missing - will be added
            System.out.println("thank_you: " + lang.get("thank_you")); // Missing - will be added
            System.out.println("please: " + lang.get("please")); // Missing - triggers batch flush (3rd key)

            // Wait for async write to complete
            System.out.println("\nWaiting for async write to complete...");
            Thread.sleep(2000);

            // Read and display the updated file
            String updatedContent = new String(Files.readAllBytes(Paths.get(langDir + "en.json")), StandardCharsets.UTF_8);
            System.out.println("\n" + "=".repeat(70));
            System.out.println("Updated en.json content (missing keys added with empty values):");
            System.out.println("=".repeat(70));
            System.out.println(updatedContent);

            // Now demonstrate with another locale
            System.out.println("\n" + "-".repeat(70));
            System.out.println("Switching to Khmer locale (will create km.json):");
            System.out.println("-".repeat(70));

            lang.setLocale("km");
            lang.get("greeting_km");
            lang.get("farewell_km");
            lang.get("button_save_km"); // Triggers batch flush

            Thread.sleep(2000);

            String kmContent = new String(Files.readAllBytes(Paths.get(langDir + "km.json")), StandardCharsets.UTF_8);
            System.out.println("\nCreated km.json content:");
            System.out.println("=".repeat(70));
            System.out.println(kmContent);

        } // Automatic flush on close

        System.out.println("\n" + "=".repeat(70));
        System.out.println("Demo completed!");
        System.out.println("Files location: " + langDir);
        System.out.println("=".repeat(70));
    }
}
