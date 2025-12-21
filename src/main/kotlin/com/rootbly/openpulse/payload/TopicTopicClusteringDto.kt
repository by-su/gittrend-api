package com.rootbly.openpulse.payload

/**
 * 카테고리 통계 정보
 */
data class CategoryStats(
    val category: String,
    val topicCount: Int,
    val totalRepoCount: Int,
    val topTopics: List<TopicWithCount>
)

/**
 * 토픽과 카운트
 */
data class TopicWithCount(
    val topic: String,
    val count: Int
)

/**
 * 카테고리 메타데이터
 */
data class CategoryMetadata(
    val name: String,
    val keywordCount: Int,
    val description: String
)