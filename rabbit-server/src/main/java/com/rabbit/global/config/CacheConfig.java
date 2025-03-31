package com.rabbit.global.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 캐시 설정 클래스
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 캐시 매니저 빈 설정
     * @return 캐시 매니저
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

        // 캐시 이름 설정
        cacheManager.setCacheNames(Arrays.asList(
                "codes",           // 공통 코드 캐시
                "menus",           // 메뉴 캐시
                "translations"     // 다국어 캐시
        ));

        return cacheManager;
    }
}
