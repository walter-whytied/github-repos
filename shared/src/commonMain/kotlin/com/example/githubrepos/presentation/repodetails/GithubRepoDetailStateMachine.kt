package com.example.githubrepos.presentation.repodetails

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GithubRepoDetailStateMachine @Inject constructor(
): FlowReduxStateMachine<GithubDetailsState, GithubDetailsAction>(LoadDetails){

    init {

        spec {
            inState<LoadDetails> {

                on<RetryLoadingDetails> { _, state ->
                    state.override { LoadDetails }
                }
            }

        }
    }
}