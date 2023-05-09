package com.example.githubrepos.api

interface GithubApi {

    suspend fun loadPage(page: Int): PageResult
    suspend fun markAsFavorite(repoId: String, favorite: Boolean)
}


