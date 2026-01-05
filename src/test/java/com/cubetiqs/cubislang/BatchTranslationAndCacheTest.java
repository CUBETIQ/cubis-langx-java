package com.cubetiqs.cubislang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for batch translation and caching features.
 */
class BatchTranslationAndCacheTest {

    private GoogleTranslateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GoogleTranslateAdapter(10, true); // With cache enabled
    }

    @Test
    void testBatchTranslation() {
        List<String> texts = Arrays.asList("Hello", "Goodbye", "Thank you");
        
        Map<String, String> results = adapter.translateBatch(texts, "en", "es");
        
        assertNotNull(results);
        assertEquals(3, results.size());
        
        // All texts should have translations
        for (String text : texts) {
            String translation = results.get(text);
            assertNotNull(translation, "Translation missing for: " + text);
            assertFalse(translation.isEmpty());
        }
    }

    @Test
    void testBatchTranslationWithEmptyList() {
        List<String> texts = Arrays.asList();
        
        Map<String, String> results = adapter.translateBatch(texts, "en", "es");
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testBatchTranslationWithNull() {
        Map<String, String> results = adapter.translateBatch(null, "en", "es");
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testBatchTranslationWithNullText() {
        List<String> texts = Arrays.asList("Hello", null, "Goodbye");
        
        Map<String, String> results = adapter.translateBatch(texts, "en", "es");
        
        assertNotNull(results);
        assertEquals(3, results.size());
        
        // Null should be handled
        assertNull(results.get(null));
    }

    @Test
    void testCacheHit() {
        // First translation - cache miss
        String firstResult = adapter.translate("Hello", "en", "es");
        assertNotNull(firstResult);
        
        int cacheSize = adapter.getCacheSize();
        assertTrue(cacheSize > 0, "Cache should have entries");
        
        // Second translation - should hit cache (no actual API call)
        String secondResult = adapter.translate("Hello", "en", "es");
        assertNotNull(secondResult);
        assertEquals(firstResult, secondResult);
        
        // Cache size should remain the same
        assertEquals(cacheSize, adapter.getCacheSize());
    }

    @Test
    void testCacheDifferentLocales() {
        String enToEs = adapter.translate("Hello", "en", "es");
        String enToFr = adapter.translate("Hello", "en", "fr");
        
        assertNotNull(enToEs);
        assertNotNull(enToFr);
        assertNotEquals(enToEs, enToFr);
        
        // Both should be cached
        assertTrue(adapter.getCacheSize() >= 2);
    }

    @Test
    void testCacheClear() {
        adapter.translate("Hello", "en", "es");
        adapter.translate("Goodbye", "en", "es");
        
        assertTrue(adapter.getCacheSize() > 0);
        
        adapter.clearCache();
        
        assertEquals(0, adapter.getCacheSize());
    }

    @Test
    void testCacheCleanup() throws InterruptedException {
        adapter.translate("Hello", "en", "es");
        
        assertTrue(adapter.getCacheSize() > 0);
        
        // Cleanup shouldn't remove non-expired entries
        int removed = adapter.cleanupCache();
        
        // Should still have the entry (not expired with 24h TTL)
        assertTrue(adapter.getCacheSize() > 0);
    }

    @Test
    void testAdapterWithCacheDisabled() {
        GoogleTranslateAdapter noCacheAdapter = new GoogleTranslateAdapter(10, false);
        
        noCacheAdapter.translate("Hello", "en", "es");
        
        assertEquals(0, noCacheAdapter.getCacheSize());
    }

    @Test
    void testBatchTranslationUsesCaching() {
        List<String> texts = Arrays.asList("Hello", "Goodbye");
        
        // First batch - cache miss
        Map<String, String> firstResults = adapter.translateBatch(texts, "en", "es");
        
        int cacheSize = adapter.getCacheSize();
        assertTrue(cacheSize >= 2);
        
        // Second batch with same texts - should use cache
        Map<String, String> secondResults = adapter.translateBatch(texts, "en", "es");
        
        // Results should be the same
        assertEquals(firstResults.get("Hello"), secondResults.get("Hello"));
        assertEquals(firstResults.get("Goodbye"), secondResults.get("Goodbye"));
        
        // Cache size should remain the same
        assertEquals(cacheSize, adapter.getCacheSize());
    }

    @Test
    void testMixedCacheHitAndMiss() {
        // Pre-cache one text
        adapter.translate("Hello", "en", "es");
        
        // Batch with one cached and one new
        List<String> texts = Arrays.asList("Hello", "Goodbye");
        Map<String, String> results = adapter.translateBatch(texts, "en", "es");
        
        assertNotNull(results.get("Hello"));
        assertNotNull(results.get("Goodbye"));
        
        // Should have at least 2 entries in cache
        assertTrue(adapter.getCacheSize() >= 2);
    }

    @Test
    void testSameTextDifferentSourceLocale() {
        String enToEs = adapter.translate("Hello", "en", "es");
        String frToEs = adapter.translate("Hello", "fr", "es");
        
        assertNotNull(enToEs);
        assertNotNull(frToEs);
        
        // Both should be cached separately
        assertTrue(adapter.getCacheSize() >= 2);
    }
}
