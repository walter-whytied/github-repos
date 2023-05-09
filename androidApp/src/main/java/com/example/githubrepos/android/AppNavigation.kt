package com.example.githubrepos.android

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost

internal sealed class RootScreen(val route: String) {
    object Popular : RootScreen("popular")
    object Favorite : RootScreen("favorite")
}

private sealed class Screen(
    private val route: String,
) {
    fun createRoute(root: RootScreen) = "${root.route}/$route"

    object Popular : Screen("popular")
    object Favorite : Screen("favorite")

    object RepoDetails : Screen("repo/{repoId}") {
        fun createRoute(root: RootScreen, repoId: String): String {
            return "${root.route}/repo/$repoId"
        }
    }
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    composeScreens: ComposeScreens,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = RootScreen.Popular.route,
        modifier = modifier,
    ) {
        addPopularTopLevel(navController, composeScreens)
        addFavoriteTopLevel(navController, composeScreens)
    }
}
@ExperimentalAnimationApi
private fun NavGraphBuilder.addPopularTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
) {
    navigation(
        route = RootScreen.Popular.route,
        startDestination = Screen.Popular.createRoute(RootScreen.Popular),
    ) {
        addPopular(navController, composeScreens, RootScreen.Popular)
        addShowDetails(navController, composeScreens, RootScreen.Popular)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addFavoriteTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
) {
    navigation(
        route = RootScreen.Favorite.route,
        startDestination = Screen.Favorite.createRoute(RootScreen.Favorite),
    ) {
        addFavorite(navController, composeScreens, RootScreen.Favorite)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addPopular(
    navController: NavController,
    composeScreens: ComposeScreens,
    root: RootScreen,
) {
    composable(
        route = Screen.Popular.createRoute(root),
    ) {
        composeScreens.popularList(
            openRepoDetails = { id ->
                navController.navigateToShow(root, id)
            }
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addShowDetails(
    navController: NavController,
    composeScreens: ComposeScreens,
    root: RootScreen,
) {
    composable(
        route = Screen.RepoDetails.createRoute(root),
        arguments = listOf(
            navArgument("repoId") { type = NavType.LongType },
        ),
    ) {
        composeScreens.repoDetails(
            navigateUp = navController::navigateUp,
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addFavorite(
    navController: NavController,
    composeScreens: ComposeScreens,
    root: RootScreen,
) {
    composable(
        route = Screen.Favorite.createRoute(root),
    ) {

    }
}



private fun NavController.navigateToShow(
    root: RootScreen,
    showId: String,
) {
    navigate(Screen.RepoDetails.createRoute(root, showId))
}