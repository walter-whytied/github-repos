package com.example.githubrepos.inject

import com.example.githubrepos.api.GithubApi
import com.example.githubrepos.api.GithubApiImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface GithubComponent {

    @Provides
    fun provideGithubApi(bind: GithubApiImpl) : GithubApi = bind
}