package com.rootbly.openpulse.service

import com.rootbly.openpulse.common.config.GithubProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class GithubTokenRotationService(
    githubProperties: GithubProperties
) {
    private val logger = LoggerFactory.getLogger(GithubTokenRotationService::class.java)
    private val tokens = githubProperties.tokens
    private val currentIndex = AtomicInteger(0)

    fun getNextToken(): String {
        val index = currentIndex.getAndUpdate { current ->
            (current + 1) % tokens.size
        }
        val token = tokens[index]
        logger.debug("Returning token[$index]: ${token.take(10)}...")
        return token
    }
}