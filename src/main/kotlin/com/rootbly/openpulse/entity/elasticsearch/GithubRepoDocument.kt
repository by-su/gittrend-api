package com.rootbly.openpulse.entity.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rootbly.openpulse.entity.GithubRepoMetadata
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting
import java.time.LocalDateTime

@Document(indexName = "github_repo")
@Setting(settingPath = "/elasticsearch/github_repo.json")
class GithubRepoDocument(
    @Id
    val repoId: Long,

    @Field(type = FieldType.Text)
    val name: String,

    @Field(type = FieldType.Text, analyzer = "github_general_analyzer")
    val description: String?,

    @Field(type = FieldType.Text)
    val topics: List<String>,

    @Field(type = FieldType.Keyword)
    val language: String?,

    @Field(type = FieldType.Integer, index = false)
    val starCount: Int,

    @Field(type = FieldType.Integer, index = false)
    val watcherCount: Int,

    @Field(type = FieldType.Integer, index = false)
    val forkCount: Int,

    @Field(type = FieldType.Date, format = [], pattern = ["uuuu-MM-dd'T'HH:mm:ss||uuuu-MM-dd"])
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(metadata: GithubRepoMetadata, objectMapper: ObjectMapper): GithubRepoDocument {
            val topics = try {
                metadata.topics?.let {
                    if (it.isNotBlank()) {
                        objectMapper.readValue<List<String>>(it)
                    } else {
                        emptyList()
                    }
                } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            return GithubRepoDocument(
                repoId = metadata.repoId,
                name = metadata.name,
                description = metadata.description.takeIf { it.isNotBlank() },
                topics = topics,
                language = metadata.language,
                starCount = metadata.starCount,
                watcherCount = metadata.watcherCount,
                forkCount = metadata.forkCount,
                updatedAt = metadata.updatedAt
            )
        }
    }
}