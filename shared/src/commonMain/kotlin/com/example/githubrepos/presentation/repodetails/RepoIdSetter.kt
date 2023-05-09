package com.example.githubrepos.presentation.repodetails

import me.tatarka.inject.annotations.Inject

interface RepoIdSetter {
  fun create(id: String): String
}

@Inject
class RepoIdSetterImpl : RepoIdSetter {
  override fun create(id: String): String = id
}
