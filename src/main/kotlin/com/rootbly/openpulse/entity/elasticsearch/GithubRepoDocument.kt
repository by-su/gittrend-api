package com.rootbly.openpulse.entity.elasticsearch

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

    @Field(type = FieldType.Text, analyzer = "name_ngram_analyzer")
    val name: String,

    @Field(type = FieldType.Text, analyzer = "github_general_analyzer")
    val description: String?,

    @Field(type = FieldType.Text, analyzer = "name_ngram_analyzer")
    val topics: List<String>,

    @Field(type = FieldType.Text, analyzer = "name_ngram_analyzer")
    val language: String?,

    @Field(type = FieldType.Integer, index = false)
    val starCount: Int,

    @Field(type = FieldType.Integer, index = false)
    val watcherCount: Int,

    @Field(type = FieldType.Integer, index = false)
    val forkCount: Int,

    @Field(type = FieldType.Date)
    val updatedAt: LocalDateTime,
)