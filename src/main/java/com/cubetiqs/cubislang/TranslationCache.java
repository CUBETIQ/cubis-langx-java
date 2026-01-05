package com.cubetiqs.cubislang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for storing translation results with TTL (Time To Live).
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class TranslationCache {
    
    private final Map<String, CacheEntry> cache;
    private final long ttlMillis;
    private final int maxSize;
    
    /**
     * Creates a new translation cache with the specified TTL and max size.
     * 
     * @param ttlHours Time to live in hours (0 = no expiration)
     * @param maxSize Maximum number of entries (0 = unlimited)
     */
    public TranslationCache(int ttlHours, int maxSize) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlHours > 0 ? ttlHours * 3600L * 1000L : Long.MAX_VALUE;
        this.maxSize = maxSize;
    }
    
    /**
     * Creates a new translation cache with default settings.
     * Default: TTL = 24 hours, unlimited size
     */
    public TranslationCache() {
        this(24, 0);
    }
    
    /**
     * Gets a cached translation.
     * 
     * @param text Source text
     * @param sourceLocale Source locale
     * @param targetLocale Target locale
     * @return Cached translation, or null if not found or expired
     */
    public String get(String text, String sourceLocale, String targetLocale) {
        String key = buildKey(text, sourceLocale, targetLocale);
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            return null;
        }
        
        // Check if expired
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(key);
            return null;
        }
        
        return entry.translation;
    }
    
    /**
     * Stores a translation in the cache.
     * 
     * @param text Source text
     * @param sourceLocale Source locale
     * @param targetLocale Target locale
     * @param translation Translated text
     */
    public void put(String text, String sourceLocale, String targetLocale, String translation) {
        if (translation == null) {
            return;
        }
        
        // Check size limit
        if (maxSize > 0 && cache.size() >= maxSize) {
            // Remove oldest entry (simple LRU approximation)
            removeOldestEntry();
        }
        
        String key = buildKey(text, sourceLocale, targetLocale);
        cache.put(key, new CacheEntry(translation, System.currentTimeMillis()));
    }
    
    /**
     * Clears all cached translations.
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Gets the current cache size.
     * 
     * @return Number of cached entries
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Removes expired entries from the cache.
     * 
     * @return Number of entries removed
     */
    public int cleanupExpired() {
        long now = System.currentTimeMillis();
        final int[] removed = {0};
        
        cache.entrySet().removeIf(entry -> {
            if (now - entry.getValue().timestamp > ttlMillis) {
                removed[0]++;
                return true;
            }
            return false;
        });
        
        return removed[0];
    }
    
    private String buildKey(String text, String sourceLocale, String targetLocale) {
        return sourceLocale + ":" + targetLocale + ":" + text;
    }
    
    private void removeOldestEntry() {
        if (cache.isEmpty()) {
            return;
        }
        
        // Find and remove the oldest entry
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }
    
    /**
     * Cache entry with translation and timestamp.
     */
    private static class CacheEntry {
        final String translation;
        final long timestamp;
        
        CacheEntry(String translation, long timestamp) {
            this.translation = translation;
            this.timestamp = timestamp;
        }
    }
}
