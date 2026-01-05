package com.cubetiqs.cubislang;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Google Translate adapter using the free (unofficial) API.
 * This adapter uses the public Google Translate endpoint without requiring API keys.
 * 
 * Note: This is an unofficial method and may be subject to rate limiting or changes.
 * For production use with high volume, consider using the official Google Cloud Translation API.
 */
public class GoogleTranslateAdapter implements TranslationAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleTranslateAdapter.class);
    private static final String TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";
    
    private final OkHttpClient httpClient;
    private final int timeout;
    
    /**
     * Create a new Google Translate adapter with default timeout (10 seconds).
     */
    public GoogleTranslateAdapter() {
        this(10);
    }
    
    /**
     * Create a new Google Translate adapter with custom timeout.
     * 
     * @param timeoutSeconds Timeout in seconds for translation requests
     */
    public GoogleTranslateAdapter(int timeoutSeconds) {
        this.timeout = timeoutSeconds;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Create a Google Translate adapter with a custom OkHttpClient.
     * 
     * @param httpClient Custom OkHttpClient instance
     */
    public GoogleTranslateAdapter(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        this.timeout = 10;
    }
    
    @Override
    public String translate(String text, String sourceLocale, String targetLocale) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        if (sourceLocale == null || targetLocale == null) {
            logger.warn("Source or target locale is null, cannot translate");
            return null;
        }
        
        // If source and target are the same, no translation needed
        if (sourceLocale.equals(targetLocale)) {
            return text;
        }
        
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            
            // Build the URL with query parameters
            String url = TRANSLATE_URL + 
                    "?client=gtx" +
                    "&sl=" + sourceLocale +
                    "&tl=" + targetLocale +
                    "&dt=t" +
                    "&q=" + encodedText;
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("Translation request failed with code: {}", response.code());
                    return null;
                }
                
                String responseBody = response.body().string();
                return parseTranslationResponse(responseBody);
            }
            
        } catch (IOException e) {
            logger.error("Error translating text: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Parse the Google Translate API response.
     * The response format is a nested JSON array.
     * 
     * @param response The raw JSON response
     * @return The translated text, or null if parsing fails
     */
    private String parseTranslationResponse(String response) {
        try {
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            
            if (jsonArray.size() == 0) {
                return null;
            }
            
            JsonArray translations = jsonArray.get(0).getAsJsonArray();
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < translations.size(); i++) {
                JsonArray translation = translations.get(i).getAsJsonArray();
                if (translation.size() > 0) {
                    result.append(translation.get(0).getAsString());
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("Error parsing translation response: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean isAvailable() {
        // Simple availability check - try to translate a simple word
        try {
            String result = translate("hello", "en", "es");
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            logger.warn("Google Translate adapter availability check failed: {}", e.getMessage());
            return false;
        }
    }
}
