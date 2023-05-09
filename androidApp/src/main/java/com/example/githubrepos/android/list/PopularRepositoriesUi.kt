package com.example.githubrepos.android.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.githubrepos.android.R
import com.example.githubrepos.android.composeables.ErrorUi
import com.example.githubrepos.android.composeables.LoadingUi
import com.example.githubrepos.model.GithubRepository
import com.example.githubrepos.presentation.FavoriteStatus
import com.example.githubrepos.presentation.repodetails.GithubRepoDetailStateMachine
import com.example.githubrepos.presentation.repodetails.RepoIdSetter
import com.example.githubrepos.presentation.repolist.GithubRepoListStateMachine
import com.example.githubrepos.presentation.repolist.LoadFirstPagePaginationState
import com.example.githubrepos.presentation.repolist.LoadNextPage
import com.example.githubrepos.presentation.repolist.LoadingFirstPageError
import com.example.githubrepos.presentation.repolist.NextPageLoadingState
import com.example.githubrepos.presentation.repolist.RepoListAction
import com.example.githubrepos.presentation.repolist.RepoListState
import com.example.githubrepos.presentation.repolist.RetryLoadingFirstPage
import com.example.githubrepos.presentation.repolist.RetryToggleFavoriteAction
import com.example.githubrepos.presentation.repolist.ShowContentPaginationState
import com.example.githubrepos.presentation.repolist.ToggleFavoriteAction
import com.freeletics.flowredux.compose.rememberStateAndDispatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import timber.log.Timber

typealias PopularRepositoriesUi = @Composable (
    openRepoDetails: (repoId: String) -> Unit,
) -> Unit

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Inject
@Composable
fun PopularRepositoriesUi(
    @Assisted openRepoDetails: (repoId: String) -> Unit,
    idCreator: (String) -> GithubRepoDetailStateMachine,
    stateMachine: GithubRepoListStateMachine
) {
    val (state, dispatch) = stateMachine.rememberStateAndDispatch()

    PopularRepositoriesScreen(
        state = state.value,
        openRepoDetails = {
            openRepoDetails(it)
            idCreator(it)
        },
        dispatch = dispatch
    )

    Timber.d("State: ${state.value}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PopularRepositoriesScreen(
    state: RepoListState?,
    openRepoDetails: (showId: String) -> Unit,
    dispatch: (RepoListAction) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (state) {
            null, // null means state machine did not emit yet the first state --> in mean time show Loading
            is LoadFirstPagePaginationState,
            -> LoadingUi(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            )

            is LoadingFirstPageError -> ErrorUi(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                dispatch = { dispatch(RetryLoadingFirstPage) },
            )

            is ShowContentPaginationState -> {
                val showLoadNextPageUi = state.shouldShowLoadMoreIndicator()
                val showErrorSnackBar = state.shouldShowErrorSnackbar()

                ReposListUi(
                    repos = state.items,
                    loadMore = showLoadNextPageUi,
                    dispatch = dispatch,
                    openRepoDetails = openRepoDetails,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                )

                val errorMessage = stringResource(R.string.unexpected_error)
                if (showErrorSnackBar) {
                    LaunchedEffect(scaffoldState.snackbarHostState) {
                        launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                errorMessage,
                                duration = SnackbarDuration.Indefinite, // Will be dismissed by changing state
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReposListUi(
    modifier: Modifier,
    repos: List<GithubRepository>,
    loadMore: Boolean,
    dispatch: (RepoListAction) -> Unit,
    openRepoDetails: (showId: String) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = modifier) {
        itemsIndexed(repos) { index, repo ->
            GithubRepoUi(repo, openRepoDetails, dispatch)
            if (index == repos.size - 1) { // user scrolls until the end of the list.
                // identifying if user scrolled until the end can be done differently
                dispatch(LoadNextPage)
            }
        }

        if (loadMore) {
            item {
                LoadNextPageUi()
            }
        }
    }

    if (loadMore) {
        LaunchedEffect(loadMore) {
            listState.animateScrollToItem(repos.size)
        }
    }
}

@Composable
fun GithubRepoUi(
    repo: GithubRepository,
    openRepoDetails: (showId: String) -> Unit,
    dispatch: (RepoListAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { openRepoDetails(repo.id) },
    ) {
        Text(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .fillMaxWidth(),
            text = repo.name,
        )
        when (repo.favoriteStatus) {
            FavoriteStatus.FAVORITE, FavoriteStatus.NOT_FAVORITE ->
                Image(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable(enabled = true) { dispatch(ToggleFavoriteAction(repo.id)) },
                    painter = painterResource(
                        if (repo.favoriteStatus == FavoriteStatus.FAVORITE) {
                            R.drawable.ic_star_yellow_24dp
                        } else {
                            R.drawable.ic_star_black_24dp
                        },
                    ),
                    contentDescription = "Stars icon",
                )

            FavoriteStatus.OPERATION_IN_PROGRESS -> LoadingUi(
                Modifier
                    .width(24.dp)
                    .height(24.dp),
            )

            FavoriteStatus.OPERATION_FAILED -> Image(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .wrapContentSize()
                    .clickable(enabled = true) { dispatch(RetryToggleFavoriteAction(repo.id)) },
                painter = painterResource(R.drawable.ic_warning),
                contentDescription = "Stars icon error",
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.width(50.dp),
            text = repo.stargazersCount.toString(),
        )
    }
}

@Composable
fun LoadNextPageUi() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
        )
    }
}


private fun ShowContentPaginationState.shouldShowLoadMoreIndicator(): Boolean =
    when (this.nextPageLoadingState) {
        NextPageLoadingState.LOADING -> true
        else -> false
    }

private fun ShowContentPaginationState.shouldShowErrorSnackbar(): Boolean =
    when (this.nextPageLoadingState) {
        NextPageLoadingState.ERROR -> true
        else -> false
    }
