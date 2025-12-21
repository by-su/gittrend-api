package com.rootbly.openpulse.common.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

/**
 * Utility class for parsing JSON data
 */
object JsonParsingUtil {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Parses topics JSON string, handling double-encoded JSON from MySQL
     *
     * MySQL JSON column may return double-encoded JSON string.
     * If the value starts and ends with quotes, it's double-encoded, so decode once.
     */
    fun parseTopics(topicsJson: String, objectMapper: ObjectMapper): List<String> {
        return try {
            var json = topicsJson

            // If the value starts and ends with quotes, it's double-encoded, so decode once
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = objectMapper.readValue(json, String::class.java)
            }

            objectMapper.readValue(json, object : TypeReference<List<String>>() {})
        } catch (e: Exception) {
            logger.warn("Failed to parse topics JSON. Topics value: '$topicsJson', Error: ${e.message}")
            emptyList()
        }
    }
}
