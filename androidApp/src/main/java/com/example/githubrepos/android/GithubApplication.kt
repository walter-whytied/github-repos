package com.example.githubrepos.android

import android.app.Application
import com.example.githubrepos.android.inject.ApplicationComponent
import com.example.githubrepos.android.inject.create
import com.example.githubrepos.util.unsafeLazy
import timber.log.Timber

class GithubApplication : Application() {

    val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}