package com.rootbly.openpulse.common.config

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.ssl.SSLContextBuilder
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext

@Configuration
class ElasticsearchConfig {

    @Value("\${elasticsearch.host:localhost}")
    private lateinit var host: String

    @Value("\${elasticsearch.port:9200}")
    private var port: Int = 9200

    @Value("\${elasticsearch.username:elastic}")
    private lateinit var username: String

    @Value("\${elasticsearch.password}")
    private lateinit var password: String

    @Value("\${elasticsearch.ssl.enabled:true}")
    private var sslEnabled: Boolean = true

    @Value("\${elasticsearch.ssl.ca-cert-path:ca.crt}")
    private lateinit var caCertPath: String

    @Bean
    fun elasticsearchClient(): ElasticsearchClient {
        val credentialsProvider = BasicCredentialsProvider().apply {
            setCredentials(
                AuthScope.ANY,
                UsernamePasswordCredentials(username, password)
            )
        }

        val scheme = if (sslEnabled) "https" else "http"
        val restClientBuilder = RestClient.builder(HttpHost(host, port, scheme))
            .setHttpClientConfigCallback { httpClientBuilder ->
                if (sslEnabled) {
                    val sslContext = createSSLContext()
                    httpClientBuilder.setSSLContext(sslContext)
                }
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            }

        val restClient = restClientBuilder.build()
        val transport = RestClientTransport(restClient, JacksonJsonpMapper())
        return ElasticsearchClient(transport)
    }

    private fun createSSLContext(): SSLContext {
        return try {
            val caCertResource = ClassPathResource(caCertPath)
            
            caCertResource.inputStream.use { inputStream ->
                val cf = CertificateFactory.getInstance("X.509")
                val caCert = cf.generateCertificate(inputStream)

                val trustStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                    load(null, null)
                    setCertificateEntry("elasticsearch-ca", caCert)
                }

                SSLContextBuilder.create()
                    .loadTrustMaterial(trustStore, null)
                    .build()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            createTrustAllContext()
        }
    }

    private fun createTrustAllContext(): SSLContext {
        return SSLContextBuilder.create()
            .loadTrustMaterial(null) { _, _ -> true }
            .build()
    }
}