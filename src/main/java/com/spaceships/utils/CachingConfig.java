package com.spaceships.utils;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration using Caffeine cache (in memory)
 * 
 */
@Slf4j
@Configuration
@EnableCaching
public class CachingConfig {

    // Cache names
    public static final String GET_THRESHOLDS = "get_thresholds";
    public static final String DASHBOARD_KPIS = "dashboard_kpis";
    public static final String LOSSES_CHART = "losses_chart";
    public static final String FIND_ALL_HIERARCHIES = "find_all_hierarchies";
    public static final String FIND_QUERY_HIERARCHIES = "find_query_hierarchies";

    /**
     * Declares CacheManafer bean
     *
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {

        ArrayList<Cache> caches = new ArrayList<>();

        // add caches to the cache list
        caches.add(buildCaffeineCache(GET_THRESHOLDS));
        caches.add(buildCaffeineCache(DASHBOARD_KPIS));
        caches.add(buildCaffeineCache(LOSSES_CHART));
        caches.add(buildCaffeineCache(FIND_ALL_HIERARCHIES));
        caches.add(buildCaffeineCache(FIND_QUERY_HIERARCHIES));

        log.info("[cache_config] Caffeine caches create...");

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return new ConcurrentMapCacheManager("spaceshipsCache");
    }

    /**
     * Builds CaffeineCache with an expiration of 24 hours
     *
     * @param cacheName Unic name for the cache
     * @return cache instance
     */
    private CaffeineCache buildCaffeineCache(String cacheName) {

        // Expiration can be modified or set by properties
        return new CaffeineCache(cacheName,
                Caffeine.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).build());
    }

    /**
     * Declares a "keyGenerator" Bean<br>
     * Default intercepts the method and uses target+method+params.<br>
     * This adds TenantId from TenantContext to key. Therefore, same values from
     * different tenants
     * will create different keys.
     * 
     * @return KeyGenerator instances
     */

}