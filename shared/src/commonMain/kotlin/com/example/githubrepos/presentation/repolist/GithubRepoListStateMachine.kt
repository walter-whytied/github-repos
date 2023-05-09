package com.example.githubrepos.presentation.repolist

import com.example.githubrepos.api.GithubApi
import com.example.githubrepos.api.PageResult
import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GithubRepoListStateMachine @Inject constructor(
    private val githubApi: GithubApi,
) : FlowReduxStateMachine<RepoListState, RepoListAction>(LoadFirstPagePaginationState) {
    init {
        spec {

            inState<LoadFirstPagePaginationState> {
                onEnter { loadFirstPage(it) }
            }

            inState<LoadingFirstPageError> {
                on<RetryLoadingFirstPage> { _, state ->
                    state.override { LoadFirstPagePaginationState }
                }
            }

            inState<ShowContentPaginationState> {
                on<LoadNextPage> { _, state ->
                    moveToLoadNextPageStateIfCanLoadNextPage(state)
                }
            }

            inState<ShowContentPaginationState>(additionalIsInState = {
                it.canLoadNextPage && it.nextPageLoadingState == NextPageLoadingState.LOADING
            }) {
                onEnter { loadNextPage(it) }
            }

            inState<ShowContentPaginationState>(additionalIsInState = {
                it.nextPageLoadingState == NextPageLoadingState.ERROR
            }) {
                onEnter { showPaginationErrorFor3SecsThenReset(it) }
            }
        }
    }

    private fun moveToLoadNextPageStateIfCanLoadNextPage(
        state: State<ShowContentPaginationState>,
    ): ChangedState<RepoListState> {
        return if (!state.snapshot.canLoadNextPage) {
            state.noChange()
        } else {
            state.mutate {
                copy(
                    nextPageLoadingState = NextPageLoadingState.LOADING,
                )
            }
        }
    }

    /**
     * Loads the first page
     */
    private suspend fun loadFirstPage(
        state: State<LoadFirstPagePaginationState>,
    ): ChangedState<RepoListState> {
        val nextState = try {
            when (val pageResult: PageResult = githubApi.loadPage(page = 0)) {
                PageResult.NoNextPage -> {
                    ShowContentPaginationState(
                        items = emptyList(),
                        canLoadNextPage = false,
                        currentPage = 1,
                        nextPageLoadingState = NextPageLoadingState.IDLE,
                    )
                }
                is PageResult.Page -> {
                    ShowContentPaginationState(
                        items = pageResult.items,
                        canLoadNextPage = true,
                        currentPage = pageResult.page,
                        nextPageLoadingState = NextPageLoadingState.IDLE,
                    )
                }
            }
        } catch (t: Throwable) {
            LoadingFirstPageError(t)
        }

        return state.override { nextState }
    }

    private suspend fun loadNextPage(
        state: State<ShowContentPaginationState>,
    ): ChangedState<RepoListState> {
        val nextPageNumber = state.snapshot.currentPage + 1
        val nextState: ChangedState<ShowContentPaginationState> = try {
            when (val pageResult = githubApi.loadPage(page = nextPageNumber)) {
                PageResult.NoNextPage -> {
                    state.mutate {
                        copy(
                            nextPageLoadingState = NextPageLoadingState.IDLE,
                            canLoadNextPage = false,
                        )
                    }
                }
                is PageResult.Page -> {
                    state.mutate {
                        copy(
                            items = items + pageResult.items,
                            canLoadNextPage = true,
                            currentPage = nextPageNumber,
                            nextPageLoadingState = NextPageLoadingState.IDLE,
                        )
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            state.mutate {
                copy(
                    nextPageLoadingState = NextPageLoadingState.ERROR,
                )
            }
        }

        return nextState
    }

    private suspend fun showPaginationErrorFor3SecsThenReset(
        state: State<ShowContentPaginationState>,
    ): ChangedState<RepoListState> {
        delay(3000)
        return state.mutate {
            copy(
                nextPageLoadingState = NextPageLoadingState.IDLE,
            )
        }
    }
}

/**
 * A wrapper class around [GithubRepoListStateMachine] so that you dont need to deal with `Flow`
 * and suspend functions from iOS.
 */
class PaginationStateMachine(
    githubApi: GithubApi,
    private val scope: CoroutineScope,
) {
    private val stateMachine = GithubRepoListStateMachine(githubApi = githubApi)

    fun dispatch(action: RepoListAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun start(stateChangeListener: (RepoListState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }
}
