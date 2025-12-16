package com.rootbly.openpulse.client

import com.rootbly.openpulse.exception.GithubClientException
import com.rootbly.openpulse.exception.GithubServerException
import com.rootbly.openpulse.payload.GithubEventDto
import com.rootbly.openpulse.payload.GithubRepoResponse
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder

/**
 * GitHub API HTTP client
 */
@Service
class GithubEventClient(
    private val githubWebClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Fetches GitHub public events
     *
     * @return List of GitHub event DTOs
     * @throws GithubClientException on 4xx client errors
     * @throws GithubServerException on 5xx server errors
     * @throws DecodingException on JSON decoding failure
     */
    suspend fun fetchEvents(): List<GithubEventDto> {
        return try {
            logger.info("Fetching GitHub events")

            githubWebClient.get()
                .uri("/events")
                .retrieve()
                .applyErrorHandling("events")  // 공통 함수 사용
                .awaitBody<List<GithubEventDto>>()
        } catch (e: DecodingException) {
            handleDecodingError(e, "events")
            throw e
        }
    }

    /**
     * Fetches repository metadata
     *
     * @param repoName Full repository name (e.g., "owner/repository")
     * @return Repository metadata response
     * @throws GithubClientException on 4xx errors or decoding failure
     * @throws GithubServerException on 5xx errors
     */
    suspend fun fetchRepoMetadata(repoName: String): GithubRepoResponse {
        return try {
            logger.debug("Fetching repository metadata for: {}", repoName)

            val encodedUri = UriComponentsBuilder
                .fromPath("/repos/{repoName}")
                .buildAndExpand(repoName)
                .toUriString()

            githubWebClient.get()
                .uri(encodedUri)
                .retrieve()
                .applyErrorHandling("repo: $repoName")
                .awaitBody<GithubRepoResponse>()
        } catch (e: DecodingException) {
            handleDecodingError(e, "repo: $repoName")
            throw GithubClientException("Failed to decode repository metadata for '$repoName': ${e.cause?.message}")
        }
    }

    /**
     * Applies common error handling to WebClient responses
     *
     * Creates custom exceptions and logs 4xx/5xx errors.
     *
     * @param context Context for error logging (e.g., "events", "repo: owner/repo")
     * @return ResponseSpec with error handling applied
     */
    private fun WebClient.ResponseSpec.applyErrorHandling(
        context: String
    ): WebClient.ResponseSpec {
        return this
            .onStatus({ it.is4xxClientError }) { response ->
                response.bodyToMono<String>()
                    .map { body ->
                        logger.warn(
                            "GitHub 4xx error [{}]: {} body={}",
                            context,
                            response.statusCode(),
                            body
                        )
                        GithubClientException(
                            "Github 4xx error [$context]: ${response.statusCode()} body=$body"
                        )
                    }
            }
            .onStatus({ it.is5xxServerError }) { response ->
                response.bodyToMono<String>()
                    .map { body ->
                        logger.error(
                            "GitHub 5xx error [{}]: {} body={}",
                            context,
                            response.statusCode(),
                            body
                        )
                        GithubServerException(
                            "GitHub 5xx error [$context]: ${response.statusCode()} body=$body"
                        )
                    }
            }
    }

    private fun handleDecodingError(e: DecodingException, context: String) {
        val root = e.cause
        logger.warn(
            "github_decode_error context={} type={} message={}",
            context,
            root?.javaClass?.simpleName,
            root?.message
        )
    }
}