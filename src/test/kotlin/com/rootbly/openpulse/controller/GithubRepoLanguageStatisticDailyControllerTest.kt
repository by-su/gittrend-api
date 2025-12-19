package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubRepoLanguageStatisticDailyControllerTest @Autowired constructor(
    private val restTemplate: TestRestTemplate
) {

    @Test
    fun `GET github repos languages statistic daily should return 200 OK`() {
        val response = restTemplate.getForEntity("/github/repos/languages/statistic/daily", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `GET github repos languages statistic daily should return list of daily language statistics`() {
        val response = restTemplate.exchange(
            "/github/repos/languages/statistic/daily",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<GithubRepoLanguageStatisticDaily>>() {}
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }
}