package com.rootbly.openpulse.payload

/**
 * Category statistics information
 */
data class CategoryStats(
    val category: String,
    val topicCount: Int,
    val totalRepoCount: Int,
    val topTopics: List<TopicWithCount>
)

/**
 * Topic with count
 */
data class TopicWithCount(
    val topic: String,
    val count: Int
)

/**
 * Category metadata
 */
data class CategoryMetadata(
    val name: String,
    val keywordCount: Int,
    val description: String
)