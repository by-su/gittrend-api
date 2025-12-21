package com.rootbly.openpulse.service

import com.rootbly.openpulse.common.constants.TopicCategories
import com.rootbly.openpulse.entity.TopicStatistic
import com.rootbly.openpulse.payload.CategoryMetadata
import com.rootbly.openpulse.payload.CategoryStats
import com.rootbly.openpulse.payload.TopicWithCount
import org.springframework.stereotype.Service
import kotlin.collections.distinctBy

@Service
class TopicCategorizationService {
    
    /**
     * Categorize topic list by category
     * 
     * @param topics Topic list to categorize
     * @return Topic map grouped by category
     */
    fun categorizeTopics(topics: List<String>): Map<String, List<String>> {
        val categorized = mutableMapOf<String, MutableList<String>>()
        val uncategorized = mutableListOf<String>()
        
        topics.distinct().forEach { topic ->
            // Check exclusion list
            if (shouldExclude(topic)) {
                return@forEach
            }
            
            // Find category
            val category = findCategory(topic)
            
            if (category != null) {
                categorized.getOrPut(category) { mutableListOf() }.add(topic)
            } else {
                uncategorized.add(topic)
            }
        }
        
        // Add uncategorized items
        if (uncategorized.isNotEmpty()) {
            categorized["Others"] = uncategorized
        }
        
        // Sort topics alphabetically within each category
        return categorized.mapValues { it.value.sorted() }
            .toSortedMap() // Sort categories alphabetically too
    }
    
    /**
     * Analyze categories with topic statistics
     *
     * @param topicStats Topic statistics list
     * @return Statistics information by category
     */
    fun analyzeCategoryStats(topicStats: List<TopicStatistic>): List<CategoryStats> {
        val categoryMap = mutableMapOf<String, MutableList<TopicStatistic>>()
        val otherStats = mutableListOf<TopicStatistic>()
        
        topicStats.forEach { stat ->
            if (shouldExclude(stat.topic)) {
                return@forEach
            }
            
            val category = findCategory(stat.topic)
            if (category != null) {
                categoryMap.getOrPut(category) { mutableListOf() }.add(stat)
            } else {
                otherStats.add(stat)
            }
        }
        
        // Generate statistics by category
        val results = categoryMap.map { (category, stats) ->
            CategoryStats(
                category = category,
                topicCount = stats.distinctBy { it.topic }.size,
                totalRepoCount = stats.sumOf { it.repoCount },
                topTopics = stats
                    .groupBy { it.topic }
                    .mapValues { it.value.sumOf { stat -> stat.repoCount } }
                    .toList()
                    .sortedByDescending { it.second }
                    .take(10)
                    .map { TopicWithCount(it.first, it.second) }
            )
        }.sortedByDescending { it.totalRepoCount }
        
        // Add Others category
        if (otherStats.isNotEmpty()) {
            val othersResult = CategoryStats(
                category = "Others",
                topicCount = otherStats.distinctBy { it.topic }.size,
                totalRepoCount = otherStats.sumOf { it.repoCount },
                topTopics = otherStats
                    .groupBy { it.topic }
                    .mapValues { it.value.sumOf { stat -> stat.repoCount } }
                    .toList()
                    .sortedByDescending { it.second }
                    .take(10)
                    .map { TopicWithCount(it.first, it.second) }
            )
            return results + othersResult
        }
        
        return results
    }
    
    /**
     * Find category for specific topic
     * 
     * @param topic Topic to find category for
     * @return Matched category name, or null if not found
     */
    fun findCategory(topic: String): String? {
        val topicLower = topic.lowercase().trim()
        
        // Exact match first
        TopicCategories.CATEGORY_KEYWORDS.forEach { (category, keywords) ->
            if (keywords.any { keyword -> topicLower == keyword }) {
                return category
            }
        }
        
        // Partial match (prefix, suffix, contains)
        TopicCategories.CATEGORY_KEYWORDS.forEach { (category, keywords) ->
            if (keywords.any { keyword -> isPartialMatch(topicLower, keyword) }) {
                return category
            }
        }
        
        return null
    }
    
    /**
     * Check if topic should be excluded
     */
    private fun shouldExclude(topic: String): Boolean {
        val topicLower = topic.lowercase().trim()
        
        // Empty string
        if (topicLower.isBlank()) return true
        
        // Exact match in exclusion list
        if (topicLower in TopicCategories.EXCLUDED_TOPICS) return true
        
        // Too short topics (1-2 characters)
        if (topicLower.length <= 2) return true
        
        // Topics with only digits
        if (topicLower.all { it.isDigit() }) return true
        
        return false
    }
    
    /**
     * Check partial match
     * - Prefix: "react-" pattern
     * - Suffix: "-bot" pattern
     * - Contains: "kubernetes" in "kubernetes-operator"
     */
    private fun isPartialMatch(topic: String, keyword: String): Boolean {
        // Exact match
        if (topic == keyword) return true
        
        // Prefix match: starts with keyword followed by - or _
        if (topic.startsWith("$keyword-") || topic.startsWith("${keyword}_")) {
            return true
        }
        
        // Suffix match: starts with - or _ and ends with keyword
        if (topic.endsWith("-$keyword") || topic.endsWith("_$keyword")) {
            return true
        }
        
        // Middle match: -keyword- or _keyword_ pattern
        if (topic.contains("-$keyword-") || topic.contains("_${keyword}_")) {
            return true
        }
        
        return false
    }
    
    /**
     * Get category list (metadata)
     */
    fun getAllCategories(): List<CategoryMetadata> {
        return TopicCategories.CATEGORY_KEYWORDS.keys.map { category ->
            CategoryMetadata(
                name = category,
                keywordCount = TopicCategories.CATEGORY_KEYWORDS[category]?.size ?: 0,
                description = getCategoryDescription(category)
            )
        }
    }
    
    /**
     * Return category description
     */
    private fun getCategoryDescription(category: String): String {
        return when (category) {
            "AI & Machine Learning" -> "AI, LLM, Neural Networks, Computer Vision, NLP"
            "AI Tools & Integration" -> "LangChain, RAG, MCP, AI Agents, Prompt Engineering"
            "Programming Languages" -> "Python, JavaScript, Java, Rust, Go and more"
            "Web Development" -> "React, Vue, Angular, Next.js, Tailwind CSS"
            "Mobile Development" -> "Android, iOS, Flutter, React Native"
            "DevOps & Infrastructure" -> "Docker, Kubernetes, CI/CD, Cloud, Monitoring"
            "Database & Data Storage" -> "PostgreSQL, MongoDB, Redis, Vector Databases"
            "Data Science & Analytics" -> "Pandas, Jupyter, Data Analysis, Visualization"
            "Blockchain & Crypto" -> "Ethereum, Solana, Smart Contracts, DeFi, Trading"
            "Security & Hacking" -> "Pentesting, Malware Analysis, Encryption, OSINT"
            "Proxy & Network Tools" -> "VPN, Shadowsocks, V2Ray, Network Security"
            "Bot & Automation" -> "Telegram, Discord, Chatbots, Workflow Automation"
            "API & Integration" -> "REST API, GraphQL, Microservices, WebSocket"
            "Game Development" -> "Unity, Godot, 3D Graphics, Game Engines"
            "Testing & Quality" -> "Selenium, Pytest, Jest, Code Quality, Linting"
            "Tools & Utilities" -> "CLI Tools, IDEs, Git, Package Managers"
            "Finance & Trading" -> "FinTech, Trading Bots, Payment Processing"
            else -> "Other topics"
        }
    }
}