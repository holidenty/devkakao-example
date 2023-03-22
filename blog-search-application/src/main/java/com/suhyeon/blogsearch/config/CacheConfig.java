package com.suhyeon.blogsearch.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.suhyeon.blogsearch.core.exception.BlogSearchException;

import lombok.Getter;

import static com.suhyeon.blogsearch.core.exception.BlogSearchExceptionCode.CACHE_TYPE_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration(proxyBeanMethods = false)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager() {
            @Override
            protected CaffeineCache createCaffeineCache(String name) {
                var cacheType = CacheType.getCacheType(name);

                return new CaffeineCache(name, Caffeine.newBuilder()
                                                       .maximumSize(cacheType.getMaxSize())
                                                       .expireAfterWrite(cacheType.getExpireTime(), SECONDS)
                                                       .build());
            }
        };

        cacheManager.setCacheNames(Arrays.stream(CacheType.values())
                                         .map(CacheType::getCacheName)
                                         .toList());

        return cacheManager;
    }

    @Getter
    private enum CacheType {
        BLOG_SEARCH("blogSearch", 60, 500),
        POPULAR_KEYWORD("popularKeyword", 1, 500),
        ;

        private String cacheName;
        private Integer expireTime;
        private Integer maxSize;

        CacheType(String cacheName, Integer expireTime, Integer maxSize) {
            this.cacheName = cacheName;
            this.expireTime = expireTime;
            this.maxSize = maxSize;
        }

        public static CacheType getCacheType(String cacheName) {
            return Arrays.stream(CacheType.values())
                         .filter(cacheType -> cacheType.getCacheName().equals(cacheName))
                         .findFirst()
                         .orElseThrow(() -> new BlogSearchException(CACHE_TYPE_NOT_FOUND));
        }
    }
}
