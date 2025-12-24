package com.rootbly.openpulse.controller

import com.rootbly.openpulse.service.GithubRepoDocumentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RepoDocumentSyncController(
    private val githubRepoDocumentService: GithubRepoDocumentService
) {

    @PostMapping("/github/repos/sync")
    fun syncAll() {
        githubRepoDocumentService.syncAll()
    }
}