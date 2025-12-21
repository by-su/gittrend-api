package com.rootbly.openpulse.service

import com.rootbly.openpulse.common.config.GithubProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GithubTokenRotationServiceTest {

    @Test
    fun `should return first token on first call`() {
        // given
        val githubProperties = GithubProperties(tokens = listOf("token1", "token2"))
        val service = GithubTokenRotationService(githubProperties)

        // when
        val token = service.getNextToken()

        // then
        assertThat(token).isEqualTo("token1")
    }

    @Test
    fun `should return second token on second call`() {
        // given
        val githubProperties = GithubProperties(tokens = listOf("token1", "token2"))
        val service = GithubTokenRotationService(githubProperties)

        // when
        service.getNextToken()
        val token = service.getNextToken()

        // then
        assertThat(token).isEqualTo("token2")
    }

    @Test
    fun `should rotate back to first token after all tokens are used`() {
        // given
        val githubProperties = GithubProperties(tokens = listOf("token1", "token2"))
        val service = GithubTokenRotationService(githubProperties)

        // when
        service.getNextToken() // token1
        service.getNextToken() // token2
        val token = service.getNextToken() // should be token1 again

        // then
        assertThat(token).isEqualTo("token1")
    }
}