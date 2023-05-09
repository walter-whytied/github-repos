package com.example.githubrepos.android.inject

import android.app.Activity
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import me.tatarka.inject.annotations.Provides

interface ActivityComponent {
    @Provides
    fun provideActivityLocale(activity: Activity): Locale {
        return ConfigurationCompat.getLocales(activity.resources.configuration)
            .get(0) ?: Locale.getDefault()
    }
}
