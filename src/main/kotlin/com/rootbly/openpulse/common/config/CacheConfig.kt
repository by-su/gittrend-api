package com.rootbly.openpulse.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.LocalDateTime

@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val dailyCache = CaffeineCache(
            "dailyStatistics",
            Caffeine.newBuilder()
                .expireAfter(object : Expiry<Any, Any> {
                    override fun expireAfterCreate(key: Any, value: Any, currentTime: Long): Long {
                        val now = LocalDateTime.now()
                        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                        return Duration.between(now, nextMidnight).toNanos()
                    }

                    override fun expireAfterUpdate(key: Any, value: Any, currentTime: Long, currentDuration: Long): Long {
                        return currentDuration
                    }

                    override fun expireAfterRead(key: Any, value: Any, currentTime: Long, currentDuration: Long): Long {
                        return currentDuration
                    }
                })
                .build()
        )

        val hourlyCache = CaffeineCache(
            "hourlyStatistics",
            Caffeine.newBuilder()
                .expireAfter(object : Expiry<Any, Any> {
                    override fun expireAfterCreate(key: Any, value: Any, currentTime: Long): Long {
                        val now = LocalDateTime.now()
                        val nextHour = now.toLocalDate()
                            .atTime(now.hour + 1, 0, 0)
                        return Duration.between(now, nextHour).toNanos()
                    }

                    override fun expireAfterUpdate(key: Any, value: Any, currentTime: Long, currentDuration: Long): Long {
                        return currentDuration
                    }

                    override fun expireAfterRead(key: Any, value: Any, currentTime: Long, currentDuration: Long): Long {
                        return currentDuration
                    }
                })
                .build()
        )

        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(listOf(dailyCache, hourlyCache))
        return cacheManager
    }
}