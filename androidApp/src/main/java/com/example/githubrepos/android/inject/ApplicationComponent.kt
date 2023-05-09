package com.example.githubrepos.android.inject

import android.content.Context
import com.example.githubrepos.android.GithubApplication
import com.example.githubrepos.inject.ApplicationScope
import com.example.githubrepos.inject.GithubComponent
import com.example.githubrepos.inject.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
    @get:Provides val context: Context,
) : GithubComponent {

    companion object {
        fun from(context: Context): ApplicationComponent {
            return (context.applicationContext as GithubApplication).component
        }
    }
}
