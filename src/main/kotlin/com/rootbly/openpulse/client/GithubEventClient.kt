package com.rootbly.openpulse.client

import com.rootbly.openpulse.exception.GithubClientException
import com.rootbly.openpulse.exception.GithubServerException
import com.rootbly.openpulse.payload.GithubEventDto
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class GithubEventClient(
    private val githubWebClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun fetchEvents(): List<GithubEventDto> {
        return try {
            githubWebClient.get()
                .uri("/events")
                .retrieve()
                .onStatus({ it.is4xxClientError }) { response ->
                    response.bodyToMono<String>()
                        .map { body ->
                            GithubClientException(
                                "Github 4xx error: ${response.statusCode()} body=$body"
                            )
                        }
                }
                .onStatus({ it.is5xxServerError }) { response ->
                    response.bodyToMono<String>()
                        .map { body ->
                            GithubServerException(
                                "GitHub 5xx error: ${response.statusCode()} body=$body"
                            )
                        }
                }
                .awaitBody()
        } catch (e: DecodingException) {
            val root = e.cause
            logger.warn(
                "github_decode_error type={} message={}",
                root?.javaClass?.simpleName,
                root?.message
            )
            throw e
        }
    }
}