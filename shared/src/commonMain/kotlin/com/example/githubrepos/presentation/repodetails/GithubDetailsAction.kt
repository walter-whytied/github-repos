package com.example.githubrepos.presentation.repodetails

sealed interface GithubDetailsAction

/**
 * Triggers reloading the repoDetails.
 */
object RetryLoadingDetails : GithubDetailsAction