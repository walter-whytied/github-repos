package com.example.githubrepos.android.home

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.githubrepos.android.ComposeScreens
import com.example.githubrepos.android.composeables.MyApplicationTheme
import com.example.githubrepos.android.inject.ActivityComponent
import com.example.githubrepos.android.inject.ApplicationComponent
import com.example.githubrepos.inject.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class MainActivity : ComponentActivity() {

    private lateinit var component: MainActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component = MainActivityComponent::class.create(this)
        setContent {
            MyApplicationTheme {
                Home(
                    composeScreens = component.screens,
                )
            }
        }
    }
}

@ActivityScope
@Component
abstract class MainActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: ApplicationComponent = ApplicationComponent.from(activity),
) : ActivityComponent {

    abstract val screens: ComposeScreens
}