package com.rootbly.openpulse.entity

/**
 * Common interface for topic statistics
 */
interface TopicStatistic {
    val topic: String
    val repoCount: Int
}