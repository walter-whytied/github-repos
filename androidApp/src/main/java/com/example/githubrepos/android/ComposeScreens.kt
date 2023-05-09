package com.example.githubrepos.android

import com.example.githubrepos.android.detail.RepoDetails
import com.example.githubrepos.android.list.PopularRepositoriesUi
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import me.tatarka.inject.annotations.Inject

@Inject
class ComposeScreens(
    val popularList: PopularRepositoriesUi,
    val repoDetails: RepoDetails
)