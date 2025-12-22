package com.rootbly.openpulse.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class CacheConfigTest @Autowired constructor(
    private val cacheManager: CacheManager
) {

    @Test
    fun `should configure Caffeine cache manager bean`() {
        assertNotNull(cacheManager)
        val cache = cacheManager.getCache("dailyStatistics")
        assertNotNull(cache)
        assertTrue(cache is CaffeineCache)
    }

    @Test
    fun `should define dailyStatistics cache expiring at midnight`() {
        val cache = cacheManager.getCache("dailyStatistics") as CaffeineCache
        assertNotNull(cache)

        // Put a value to trigger expiry calculation
        cache.put("test", "value")

        val caffeineCache = cache.nativeCache as com.github.benmanes.caffeine.cache.Cache<Any, Any>
        val policy = caffeineCache.policy().expireVariably().get()
        val expiryNanos: Long = policy.getExpiresAfter("test", TimeUnit.NANOSECONDS).asLong

        // Calculate expected time until next midnight
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusSeconds(30)
        val expectedNanos: Long = Duration.between(now, nextMidnight).toNanos()

        // Allow 1 second tolerance for test execution time
        val tolerance: Long = TimeUnit.SECONDS.toNanos(1)
        val lowerBound: Long = expectedNanos - tolerance
        val upperBound: Long = expectedNanos + tolerance
        assertTrue(expiryNanos >= lowerBound && expiryNanos <= upperBound,
            "Expected expiry around $expectedNanos nanos, but got $expiryNanos nanos")
    }

    @Test
    fun `should define hourlyStatistics cache expiring at next hour`() {
        val cache = cacheManager.getCache("hourlyStatistics") as CaffeineCache
        assertNotNull(cache)

        // Put a value to trigger expiry calculation
        cache.put("test", "value")

        val caffeineCache = cache.nativeCache as com.github.benmanes.caffeine.cache.Cache<Any, Any>
        val policy = caffeineCache.policy().expireVariably().get()
        val expiryNanos: Long = policy.getExpiresAfter("test", TimeUnit.NANOSECONDS).asLong

        // Calculate expected time until next hour
        val now = LocalDateTime.now()
        val nextHour = now.toLocalDate().atTime(now.hour + 1, 0, 30)
        val expectedNanos: Long = Duration.between(now, nextHour).toNanos()

        // Allow 1 second tolerance for test execution time
        val tolerance: Long = TimeUnit.SECONDS.toNanos(1)
        val lowerBound: Long = expectedNanos - tolerance
        val upperBound: Long = expectedNanos + tolerance
        assertTrue(expiryNanos >= lowerBound && expiryNanos <= upperBound,
            "Expected expiry around $expectedNanos nanos, but got $expiryNanos nanos")
    }
}