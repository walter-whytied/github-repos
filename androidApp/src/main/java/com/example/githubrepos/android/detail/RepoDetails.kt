package com.example.githubrepos.android.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.githubrepos.android.composeables.ErrorUi
import com.example.githubrepos.android.composeables.LoadingUi
import com.example.githubrepos.presentation.repodetails.GithubDetailsAction
import com.example.githubrepos.presentation.repodetails.GithubDetailsState
import com.example.githubrepos.presentation.repodetails.GithubRepoDetailStateMachine
import com.example.githubrepos.presentation.repodetails.LoadDetails
import com.example.githubrepos.presentation.repodetails.LoadingError
import com.example.githubrepos.presentation.repodetails.RetryLoadingDetails
import com.freeletics.flowredux.compose.rememberStateAndDispatch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias RepoDetails = @Composable (
    navigateUp: () -> Unit
) -> Unit

@Inject
@Composable
fun RepoDetails(
    @Assisted navigateUp: () -> Unit,
    stateMachine: GithubRepoDetailStateMachine
) {

    val (state, dispatch) = stateMachine.rememberStateAndDispatch()

    RepoDetails(
        state = state.value,
        navigateUp = navigateUp,
        dispatch = dispatch
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RepoDetails(
    state: GithubDetailsState?,
    navigateUp: () -> Unit,
    dispatch: (GithubDetailsAction) -> Unit
) {

    Scaffold(
        topBar = {
            ShowDetailsAppBar(
                title = "Repo Details",
                onNavigateUp = navigateUp,
                onRefresh = { }
            )
        }
    ) {
        when(state){
            null ,// null means state machine did not emit yet the first state --> in mean time show Loading
            LoadDetails -> LoadingUi(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            )

            is LoadingError -> ErrorUi(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                dispatch = { dispatch(RetryLoadingDetails) },
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowDetailsAppBar(
    title: String,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {

    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
        },
        modifier = modifier,
    )
}