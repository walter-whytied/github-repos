package com.example.githubrepos.model

import com.example.githubrepos.presentation.FavoriteStatus

data class GithubRepository(
    val id: String,
    val name: String,
    val stargazersCount: Int,
    val favoriteStatus: FavoriteStatus,
)
