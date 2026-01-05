package com.cubetiqs.cubislang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Utility to automatically generate translation files from a source language.
 * This tool reads a source translation file (e.g., en.json) and generates
 * translations for other locales using a TranslationAdapter.
 */
public class TranslationFileGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(TranslationFileGenerator.class);
    private final TranslationAdapter adapter;
    private final Gson gson;
    
    /**
     * Creates a new translation file generator.
     * 
     * @param adapter The translation adapter to use for translations
     */
    public TranslationFileGenerator(TranslationAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Translation adapter cannot be null");
        }
        this.adapter = adapter;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * Generates a translation file for the target locale.
     * 
     * @param sourceFile Path to the source translation file (e.g., "lang/en.json")
     * @param sourceLocale Source locale (e.g., "en")
     * @param targetLocale Target locale (e.g., "km", "zh", "es")
     * @param outputFile Path for the output file (e.g., "lang/km.json")
     * @return true if generation was successful, false otherwise
     */
    public boolean generateTranslationFile(String sourceFile, String sourceLocale, String targetLocale, String outputFile) {
        try {
            // Read source file
            JsonObject sourceJson = readJsonFile(sourceFile);
            if (sourceJson == null) {
                logger.error("Failed to read source file: {}", sourceFile);
                return false;
            }
            
            logger.info("Generating translations from {} to {}...", sourceLocale, targetLocale);
            
            // Translate all values
            JsonObject translatedJson = translateJsonObject(sourceJson, sourceLocale, targetLocale);
            
            // Write output file
            writeJsonFile(outputFile, translatedJson);
            
            logger.info("Successfully generated translation file: {}", outputFile);
            return true;
            
        } catch (Exception e) {
            logger.error("Error generating translation file: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Generates translation files for multiple target locales.
     * 
     * @param sourceFile Path to the source translation file
     * @param sourceLocale Source locale
     * @param targetLocales List of target locales
     * @param outputDir Output directory for generated files
     * @return Map of locale to generation success status
     */
    public Map<String, Boolean> generateMultipleTranslationFiles(
            String sourceFile, 
            String sourceLocale, 
            List<String> targetLocales, 
            String outputDir) {
        
        Map<String, Boolean> results = new LinkedHashMap<>();
        
        for (String targetLocale : targetLocales) {
            String outputFile = new File(outputDir, targetLocale + ".json").getPath();
            boolean success = generateTranslationFile(sourceFile, sourceLocale, targetLocale, outputFile);
            results.put(targetLocale, success);
        }
        
        return results;
    }
    
    /**
     * Generates translations using batch translation for better performance.
     * 
     * @param sourceFile Path to the source translation file
     * @param sourceLocale Source locale
     * @param targetLocale Target locale
     * @param outputFile Path for the output file
     * @return true if generation was successful, false otherwise
     */
    public boolean generateTranslationFileBatch(String sourceFile, String sourceLocale, String targetLocale, String outputFile) {
        try {
            // Read source file
            JsonObject sourceJson = readJsonFile(sourceFile);
            if (sourceJson == null) {
                logger.error("Failed to read source file: {}", sourceFile);
                return false;
            }
            
            logger.info("Generating translations (batch mode) from {} to {}...", sourceLocale, targetLocale);
            
            // Collect all text values
            List<String> allTexts = new ArrayList<>();
            Map<String, String> pathToText = new LinkedHashMap<>();
            collectTexts(sourceJson, "", allTexts, pathToText);
            
            logger.info("Found {} texts to translate", allTexts.size());
            
            // Batch translate
            Map<String, String> translations = adapter.translateBatch(allTexts, sourceLocale, targetLocale);
            
            // Build translated JSON
            JsonObject translatedJson = new JsonObject();
            for (Map.Entry<String, String> entry : pathToText.entrySet()) {
                String path = entry.getKey();
                String originalText = entry.getValue();
                String translatedText = translations.get(originalText);
                
                if (translatedText != null) {
                    setNestedValue(translatedJson, path, translatedText);
                }
            }
            
            // Write output file
            writeJsonFile(outputFile, translatedJson);
            
            logger.info("Successfully generated translation file: {}", outputFile);
            return true;
            
        } catch (Exception e) {
            logger.error("Error generating translation file: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Merges new translations into an existing translation file.
     * Only translates keys that are missing in the target file.
     * 
     * @param sourceFile Path to the source translation file
     * @param targetFile Path to the existing target translation file
     * @param sourceLocale Source locale
     * @param targetLocale Target locale
     * @return true if merge was successful, false otherwise
     */
    public boolean mergeTranslationFile(String sourceFile, String targetFile, String sourceLocale, String targetLocale) {
        try {
            JsonObject sourceJson = readJsonFile(sourceFile);
            JsonObject targetJson = readJsonFile(targetFile);
            
            if (sourceJson == null) {
                logger.error("Failed to read source file: {}", sourceFile);
                return false;
            }
            
            if (targetJson == null) {
                targetJson = new JsonObject();
            }
            
            logger.info("Merging translations from {} to {}...", sourceLocale, targetLocale);
            
            // Find missing keys and translate them
            int addedCount = 0;
            JsonObject merged = mergeJsonObjects(sourceJson, targetJson, sourceLocale, targetLocale);
            
            // Write updated file
            writeJsonFile(targetFile, merged);
            
            logger.info("Successfully merged translations into: {}", targetFile);
            return true;
            
        } catch (Exception e) {
            logger.error("Error merging translation file: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private JsonObject readJsonFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", filePath, e);
            return null;
        }
    }
    
    private void writeJsonFile(String filePath, JsonObject jsonObject) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        }
    }
    
    private JsonObject translateJsonObject(JsonObject source, String sourceLocale, String targetLocale) {
        JsonObject result = new JsonObject();
        
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                // Translate string value
                String originalText = value.getAsString();
                String translatedText = adapter.translate(originalText, sourceLocale, targetLocale);
                
                if (translatedText != null) {
                    result.addProperty(key, translatedText);
                    logger.debug("Translated '{}': {} -> {}", key, originalText, translatedText);
                } else {
                    // Keep original if translation fails
                    result.addProperty(key, originalText);
                    logger.warn("Translation failed for '{}', keeping original", key);
                }
            } else if (value.isJsonObject()) {
                // Recursively translate nested object
                JsonObject translatedNested = translateJsonObject(value.getAsJsonObject(), sourceLocale, targetLocale);
                result.add(key, translatedNested);
            } else {
                // Copy non-string values as-is
                result.add(key, value);
            }
        }
        
        return result;
    }
    
    private void collectTexts(JsonObject json, String pathPrefix, List<String> allTexts, Map<String, String> pathToText) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            String path = pathPrefix.isEmpty() ? key : pathPrefix + "." + key;
            JsonElement value = entry.getValue();
            
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                String text = value.getAsString();
                allTexts.add(text);
                pathToText.put(path, text);
            } else if (value.isJsonObject()) {
                collectTexts(value.getAsJsonObject(), path, allTexts, pathToText);
            }
        }
    }
    
    private void setNestedValue(JsonObject root, String path, String value) {
        String[] parts = path.split("\\.");
        JsonObject current = root;
        
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                current.add(part, new JsonObject());
            }
            current = current.get(part).getAsJsonObject();
        }
        
        current.addProperty(parts[parts.length - 1], value);
    }
    
    private JsonObject mergeJsonObjects(JsonObject source, JsonObject target, String sourceLocale, String targetLocale) {
        JsonObject result = target.deepCopy();
        
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement sourceValue = entry.getValue();
            
            if (!result.has(key)) {
                // Key doesn't exist in target, translate it
                if (sourceValue.isJsonPrimitive() && sourceValue.getAsJsonPrimitive().isString()) {
                    String originalText = sourceValue.getAsString();
                    String translatedText = adapter.translate(originalText, sourceLocale, targetLocale);
                    
                    if (translatedText != null) {
                        result.addProperty(key, translatedText);
                        logger.debug("Added translation for '{}': {}", key, translatedText);
                    }
                } else if (sourceValue.isJsonObject()) {
                    JsonObject translatedNested = translateJsonObject(sourceValue.getAsJsonObject(), sourceLocale, targetLocale);
                    result.add(key, translatedNested);
                } else {
                    result.add(key, sourceValue);
                }
            } else if (sourceValue.isJsonObject() && result.get(key).isJsonObject()) {
                // Recursively merge nested objects
                JsonObject merged = mergeJsonObjects(
                    sourceValue.getAsJsonObject(),
                    result.get(key).getAsJsonObject(),
                    sourceLocale,
                    targetLocale
                );
                result.add(key, merged);
            }
        }
        
        return result;
    }
}
