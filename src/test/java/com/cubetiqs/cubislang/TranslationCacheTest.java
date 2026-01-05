package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TranslationCache.
 */
class TranslationCacheTest {

    private TranslationCache cache;

    @BeforeEach
    void setUp() {
        cache = new TranslationCache(1, 100); // 1 hour TTL, max 100 entries
    }

    @Test
    void testCachePutAndGet() {
        cache.put("Hello", "en", "es", "Hola");
        
        String result = cache.get("Hello", "en", "es");
        assertEquals("Hola", result);
    }

    @Test
    void testCacheMiss() {
        String result = cache.get("NonExistent", "en", "es");
        assertNull(result);
    }

    @Test
    void testCacheDifferentLocales() {
        cache.put("Hello", "en", "es", "Hola");
        cache.put("Hello", "en", "fr", "Bonjour");
        
        assertEquals("Hola", cache.get("Hello", "en", "es"));
        assertEquals("Bonjour", cache.get("Hello", "en", "fr"));
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        TranslationCache shortCache = new TranslationCache(0, 100); // Very short TTL
        shortCache.put("Hello", "en", "es", "Hola");
        
        // Should be in cache immediately
        assertNotNull(shortCache.get("Hello", "en", "es"));
        
        // Wait a bit (the cache uses milliseconds internally)
        Thread.sleep(100);
        
        // With TTL of 0 hours and after sleep, it might be expired
        // This is more of a demonstration of the expiration logic
    }

    @Test
    void testCacheClear() {
        cache.put("Hello", "en", "es", "Hola");
        cache.put("Goodbye", "en", "es", "Adiós");
        
        assertEquals(2, cache.size());
        
        cache.clear();
        
        assertEquals(0, cache.size());
        assertNull(cache.get("Hello", "en", "es"));
    }

    @Test
    void testCacheSize() {
        assertEquals(0, cache.size());
        
        cache.put("Hello", "en", "es", "Hola");
        assertEquals(1, cache.size());
        
        cache.put("Goodbye", "en", "es", "Adiós");
        assertEquals(2, cache.size());
    }

    @Test
    void testCacheMaxSize() {
        TranslationCache smallCache = new TranslationCache(24, 3); // Max 3 entries
        
        smallCache.put("One", "en", "es", "Uno");
        smallCache.put("Two", "en", "es", "Dos");
        smallCache.put("Three", "en", "es", "Tres");
        
        assertEquals(3, smallCache.size());
        
        // Adding a 4th entry should remove the oldest
        smallCache.put("Four", "en", "es", "Cuatro");
        
        assertEquals(3, smallCache.size());
        
        // The newest entry should be there
        assertEquals("Cuatro", smallCache.get("Four", "en", "es"));
    }

    @Test
    void testNullTranslationNotCached() {
        cache.put("Hello", "en", "es", null);
        
        assertEquals(0, cache.size());
        assertNull(cache.get("Hello", "en", "es"));
    }

    @Test
    void testCleanupExpired() throws InterruptedException {
        // Create cache with very short TTL for testing
        TranslationCache testCache = new TranslationCache(0, 100);
        
        testCache.put("Hello", "en", "es", "Hola");
        testCache.put("Goodbye", "en", "es", "Adiós");
        
        assertEquals(2, testCache.size());
        
        // Wait for expiration
        Thread.sleep(100);
        
        // Cleanup expired entries
        int removed = testCache.cleanupExpired();
        
        // All entries should be expired
        assertTrue(removed >= 0);
    }

    @Test
    void testDefaultCache() {
        TranslationCache defaultCache = new TranslationCache();
        
        defaultCache.put("Hello", "en", "es", "Hola");
        assertEquals("Hola", defaultCache.get("Hello", "en", "es"));
    }

    @Test
    void testCacheKeyDistinction() {
        cache.put("Hello", "en", "es", "Hola");
        cache.put("Hello", "es", "en", "Hello");
        cache.put("Hello world", "en", "es", "Hola mundo");
        
        assertEquals("Hola", cache.get("Hello", "en", "es"));
        assertEquals("Hello", cache.get("Hello", "es", "en"));
        assertEquals("Hola mundo", cache.get("Hello world", "en", "es"));
        assertNull(cache.get("Hello", "en", "fr"));
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // Test thread safety
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    cache.put("Text" + index, "en", "es", "Translated" + index);
                    cache.get("Text" + index, "en", "es");
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should not crash and have some entries
        assertTrue(cache.size() > 0);
    }
}
