package com.rootbly.openpulse.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubEventStatisticDailyControllerTest @Autowired constructor(
    private val restTemplate: TestRestTemplate
) {

    @Test
    fun `GET github events statistic daily should return 200 OK`() {
        val response = restTemplate.getForEntity("/github/events/statistic/daily", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}