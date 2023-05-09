package com.example.githubrepos.presentation.repolist

/**
 * Parent class for all actions
 */
sealed interface RepoListAction

/**
 * Triggers reloading the first page. Should be only used while in [LoadingFirstPageError]
 */
object RetryLoadingFirstPage : RepoListAction

/**
 * Triggers loading the next page. This is typically triggered if the user scrolls until the end
 * of the list and want to load the next page.
 */
object LoadNextPage : RepoListAction

/**
 * Mark a repository as favorite
 */
data class ToggleFavoriteAction(val id: String) : RepoListAction

/**
 * If an error has occurred while Toggling Favorite status, then you can retry with this action
 */
data class RetryToggleFavoriteAction(val id: String) : RepoListAction

