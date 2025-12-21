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
     * 토픽 리스트를 카테고리별로 분류
     * 
     * @param topics 분류할 토픽 리스트
     * @return 카테고리별로 그룹화된 토픽 맵
     */
    fun categorizeTopics(topics: List<String>): Map<String, List<String>> {
        val categorized = mutableMapOf<String, MutableList<String>>()
        val uncategorized = mutableListOf<String>()
        
        topics.distinct().forEach { topic ->
            // 제외 목록 체크
            if (shouldExclude(topic)) {
                return@forEach
            }
            
            // 카테고리 찾기
            val category = findCategory(topic)
            
            if (category != null) {
                categorized.getOrPut(category) { mutableListOf() }.add(topic)
            } else {
                uncategorized.add(topic)
            }
        }
        
        // 미분류 항목 추가
        if (uncategorized.isNotEmpty()) {
            categorized["Others"] = uncategorized
        }
        
        // 각 카테고리 내 토픽을 알파벳순으로 정렬
        return categorized.mapValues { it.value.sorted() }
            .toSortedMap() // 카테고리도 알파벳순 정렬
    }
    
    /**
     * 토픽 통계와 함께 카테고리 분석
     *
     * @param topicStats 토픽 통계 리스트
     * @return 카테고리별 통계 정보
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
        
        // 카테고리별 통계 생성
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
        
        // Others 카테고리 추가
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
     * 특정 토픽의 카테고리 찾기
     * 
     * @param topic 카테고리를 찾을 토픽
     * @return 매칭된 카테고리 이름, 없으면 null
     */
    fun findCategory(topic: String): String? {
        val topicLower = topic.lowercase().trim()
        
        // 정확한 매칭 우선
        TopicCategories.CATEGORY_KEYWORDS.forEach { (category, keywords) ->
            if (keywords.any { keyword -> topicLower == keyword }) {
                return category
            }
        }
        
        // 부분 매칭 (접두사, 접미사, 포함)
        TopicCategories.CATEGORY_KEYWORDS.forEach { (category, keywords) ->
            if (keywords.any { keyword -> isPartialMatch(topicLower, keyword) }) {
                return category
            }
        }
        
        return null
    }
    
    /**
     * 제외 대상 토픽 확인
     */
    private fun shouldExclude(topic: String): Boolean {
        val topicLower = topic.lowercase().trim()
        
        // 빈 문자열
        if (topicLower.isBlank()) return true
        
        // 제외 목록 정확 매칭
        if (topicLower in TopicCategories.EXCLUDED_TOPICS) return true
        
        // 너무 짧은 토픽 (1-2글자)
        if (topicLower.length <= 2) return true
        
        // 숫자만 있는 토픽
        if (topicLower.all { it.isDigit() }) return true
        
        return false
    }
    
    /**
     * 부분 매칭 확인
     * - 접두사: "react-" 패턴
     * - 접미사: "-bot" 패턴
     * - 포함: "kubernetes" in "kubernetes-operator"
     */
    private fun isPartialMatch(topic: String, keyword: String): Boolean {
        // 정확한 매칭
        if (topic == keyword) return true
        
        // 접두사 매칭: keyword로 시작하고 -나 _가 따라옴
        if (topic.startsWith("$keyword-") || topic.startsWith("${keyword}_")) {
            return true
        }
        
        // 접미사 매칭: -나 _로 시작하고 keyword로 끝남
        if (topic.endsWith("-$keyword") || topic.endsWith("_$keyword")) {
            return true
        }
        
        // 중간 매칭: -keyword- 또는 _keyword_ 패턴
        if (topic.contains("-$keyword-") || topic.contains("_${keyword}_")) {
            return true
        }
        
        return false
    }
    
    /**
     * 카테고리 목록 조회 (메타데이터)
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
     * 카테고리 설명 반환
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