package com.rootbly.openpulse.client

import com.rootbly.openpulse.common.exception.GithubClientException
import com.rootbly.openpulse.common.exception.GithubServerException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.core.codec.DecodingException
import reactor.core.publisher.Mono
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GithubEventClientTest {

    companion object {
        @JvmStatic
        fun errorStatusCodes() = Stream.of(
            Arguments.of(HttpStatus.BAD_REQUEST, GithubClientException::class.java, "4xx"),
            Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, GithubServerException::class.java, "5xx")
        )
    }

    private fun createEvent(
        id: Long,
        type: String,
        actorId: Long,
        actorLogin: String,
        repoId: Long,
        repoName: String
    ) = """
        {
          "id": $id,
          "type": "$type",
          "actor": {"id": $actorId, "login": "$actorLogin", "displayLogin": "$actorLogin", "gravatarId": null, "url": "", "avatarUrl": null},
          "repo": {"id": $repoId, "name": "$repoName", "url": ""},
          "payload": {},
          "public": true,
          "createdAt": "2024-12-13T12:00:00"
        }
    """.trimIndent()

    private fun createClient(status: HttpStatus = HttpStatus.OK, body: String) =
        GithubEventClient(webClient(status, body))

    private fun webClient(status: HttpStatus, body: String): WebClient {
        val exchange = ExchangeFunction {
            Mono.just(
                ClientResponse.create(status)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build()
            )
        }
        return WebClient.builder()
            .exchangeFunction(exchange)
            .build()
    }

    @Test
    fun `fetchEvents returns single event on 200`() = runTest {
        val json = "[${createEvent(1, "PushEvent", 2, "octocat", 3, "octo/hello")}]"
        val events = createClient(body = json).fetchEvents()

        assertAll(
            { assertEquals(1, events.size) },
            { assertEquals(1L, events[0].id) },
            { assertEquals("PushEvent", events[0].type) },
            { assertEquals(2L, events[0].actor.id) },
            { assertEquals("octocat", events[0].actor.login) },
            { assertEquals(3L, events[0].repo.id) },
            { assertEquals("octo/hello", events[0].repo.name) }
        )
    }

    @Test
    fun `fetchEvents returns multiple events on 200`() = runTest {
        val event1 = createEvent(1, "PushEvent", 2, "octocat", 3, "octo/hello")
        val event2 = createEvent(10, "IssuesEvent", 20, "developer", 30, "dev/project")
        val json = "[$event1, $event2]"
        val events = createClient(body = json).fetchEvents()

        assertAll(
            { assertEquals(2, events.size) },
            { assertEquals(1L, events[0].id) },
            { assertEquals("PushEvent", events[0].type) },
            { assertEquals(10L, events[1].id) },
            { assertEquals("IssuesEvent", events[1].type) },
            { assertEquals("developer", events[1].actor.login) }
        )
    }

    @Test
    fun `fetchEvents returns empty list on 200`() = runTest {
        val events = createClient(body = "[]").fetchEvents()
        assertEquals(0, events.size)
    }

    @ParameterizedTest(name = "status {0} throws {1}")
    @MethodSource("errorStatusCodes")
    fun `fetchEvents throws appropriate exception on error status`(
        status: HttpStatus,
        expectedExceptionType: Class<out Exception>,
        errorType: String
    ) = runTest {
        val exception = assertFailsWith(expectedExceptionType.kotlin) {
            createClient(status, "{\"error\":\"test error\"}").fetchEvents()
        }

        assertAll(
            {
                assertTrue(
                    exception.message?.contains(errorType) == true,
                    "Expected exception message to contain '$errorType', but was: ${exception.message}"
                )
            },
            {
                assertTrue(
                    exception.message?.contains(status.toString()) == true,
                    "Expected exception message to contain status code '$status', but was: ${exception.message}"
                )
            }
        )
    }

    @Test
    fun `fetchEvents rethrows DecodingException on invalid JSON`() = runTest {
        assertFailsWith<DecodingException> {
            createClient(body = "[{\"id\": \"not-a-number\"}]").fetchEvents()
        }
    }
}

