package com.example.githubrepos.inject

import com.example.githubrepos.presentation.repodetails.GithubRepoDetailStateMachine
import com.example.githubrepos.presentation.repodetails.RepoIdSetter
import com.example.githubrepos.presentation.repodetails.RepoIdSetterImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class GithubDetailsComponent {

    abstract val repoId: (String) -> GithubRepoDetailStateMachine

    @Provides
    fun provideRepoIdSetter(bind: RepoIdSetterImpl) : RepoIdSetter = bind
}