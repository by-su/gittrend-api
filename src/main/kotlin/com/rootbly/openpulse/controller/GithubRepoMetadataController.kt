package com.rootbly.openpulse.controller

import com.rootbly.openpulse.service.GithubRepoMetadataService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repo")
class GithubRepoMetadataController(
    private val githubRepoMetadataService: GithubRepoMetadataService
) {

    @PostMapping("/url")
    fun updateUrl(@RequestParam(defaultValue = "5000") batchSize: Int): String {
        while (true) {
            val updatedCount = githubRepoMetadataService.updateUrlsInBatch(batchSize)
            
            if (updatedCount == 0) {
                break
            }

            Thread.sleep(1)
        }

        return "Success"
    }
}