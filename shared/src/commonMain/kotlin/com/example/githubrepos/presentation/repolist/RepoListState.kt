package com.example.githubrepos.presentation.repolist

import com.example.githubrepos.model.GithubRepository

/**
 * parent class for all states
 */
sealed class RepoListState

/**
 * State that represents loading the first page
 */
object LoadFirstPagePaginationState : RepoListState()

/**
 * An error has occurred while loading the first page
 */
data class LoadingFirstPageError(val cause: Throwable) : RepoListState()

/**
 * Modeling state for Pull To Refresh and load next state
 */
enum class NextPageLoadingState {
    /**
     * Not doing pull to refresh
     */
    IDLE,

    /**
     * Loading is in Progress
     */
    LOADING,

    /**
     * An error has occurred while loading the next state
     */
    ERROR,
}

/**
 * State that represents displaying a list of  [GithubRepository] items
 */
data class ShowContentPaginationState(
    val items: List<GithubRepository>,
    val nextPageLoadingState: NextPageLoadingState,
    internal val currentPage: Int,
    internal val canLoadNextPage: Boolean,
) : RepoListState()
