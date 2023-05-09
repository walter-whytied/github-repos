package com.example.githubrepos.presentation.repodetails

sealed class GithubDetailsState

object LoadDetails : GithubDetailsState()

data class LoadingError(val cause: Throwable) : GithubDetailsState()