package com.rootbly.openpulse.common.constants

enum class SortBy {
    RELEVANCE,
    STARS,
    FORKS;

    companion object {
        fun fromString(value: String?): SortBy {
            return when (value?.lowercase()) {
                "stars" -> STARS
                "forks" -> FORKS
                "relevance", "", null -> RELEVANCE
                else -> RELEVANCE
            }
        }
    }
}