package com.cubetiqs.cubislang.example;

import com.cubetiqs.cubislang.TranslationAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Example of a custom translation adapter.
 * This demonstrates how to create your own adapter for any translation service.
 * 
 * In this example, we create a simple dictionary-based adapter,
 * but you could integrate with any translation API:
 * - Microsoft Translator
 * - DeepL
 * - AWS Translate
 * - Your own translation service
 */
public class CustomTranslationAdapter implements TranslationAdapter {
    
    private final Map<String, Map<String, String>> translationDatabase;
    
    public CustomTranslationAdapter() {
        this.translationDatabase = new HashMap<>();
        initializeTranslations();
    }
    
    /**
     * Initialize a simple translation database.
     * In a real implementation, you would call an actual translation API.
     */
    private void initializeTranslations() {
        // English to Spanish
        Map<String, String> enToEs = new HashMap<>();
        enToEs.put("Hello", "Hola");
        enToEs.put("Goodbye", "Adiós");
        enToEs.put("Thank you", "Gracias");
        enToEs.put("Welcome", "Bienvenido");
        translationDatabase.put("en-es", enToEs);
        
        // English to French
        Map<String, String> enToFr = new HashMap<>();
        enToFr.put("Hello", "Bonjour");
        enToFr.put("Goodbye", "Au revoir");
        enToFr.put("Thank you", "Merci");
        enToFr.put("Welcome", "Bienvenue");
        translationDatabase.put("en-fr", enToFr);
        
        // English to German
        Map<String, String> enToDe = new HashMap<>();
        enToDe.put("Hello", "Hallo");
        enToDe.put("Goodbye", "Auf Wiedersehen");
        enToDe.put("Thank you", "Danke");
        enToDe.put("Welcome", "Willkommen");
        translationDatabase.put("en-de", enToDe);
    }
    
    @Override
    public String translate(String text, String sourceLocale, String targetLocale) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        if (sourceLocale == null || targetLocale == null) {
            return null;
        }
        
        // If source and target are the same, no translation needed
        if (sourceLocale.equals(targetLocale)) {
            return text;
        }
        
        // Build the translation key
        String translationKey = sourceLocale + "-" + targetLocale;
        
        // Look up in our database
        Map<String, String> translations = translationDatabase.get(translationKey);
        if (translations != null && translations.containsKey(text)) {
            return translations.get(text);
        }
        
        // If not found, return null (or you could call another service as fallback)
        return null;
    }
    
    @Override
    public boolean isAvailable() {
        return true; // This adapter is always available
    }
    
    /**
     * Example method showing how you might integrate with a real API.
     * Uncomment and modify for actual API integration.
     */
    /*
    private String translateViaAPI(String text, String sourceLocale, String targetLocale) {
        try {
            // Example: Call Microsoft Translator API
            String endpoint = "https://api.cognitive.microsofttranslator.com/translate";
            String params = "?api-version=3.0&from=" + sourceLocale + "&to=" + targetLocale;
            
            // Build request
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "[{\"Text\":\"" + text + "\"}]"
            );
            
            Request request = new Request.Builder()
                .url(endpoint + params)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", "YOUR_API_KEY")
                .addHeader("Content-Type", "application/json")
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse JSON and extract translation
                    // Return translated text
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */
}
