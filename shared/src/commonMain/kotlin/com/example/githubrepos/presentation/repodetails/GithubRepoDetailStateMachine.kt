package com.example.githubrepos.presentation.repodetails

import co.touchlab.kermit.Logger
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GithubRepoDetailStateMachine @Inject constructor(
    @Assisted private val id: String
): FlowReduxStateMachine<GithubDetailsState, GithubDetailsAction>(LoadDetails){

    init {
        Logger.withTag("@DetailStateMachine").d { id }

        spec {
            inState<LoadDetails> {

                on<RetryLoadingDetails> { _, state ->
                    state.override { LoadDetails }
                }
            }

        }
    }
}