package com.lmu.setupmanager.navigation

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmu.setupmanager.ui.diff.SetupDiffScreen
import com.lmu.setupmanager.ui.home.HomeScreen
import com.lmu.setupmanager.ui.saved.SavedSetupsScreen
import com.lmu.setupmanager.ui.setup.SetupScreen
import com.lmu.setupmanager.ui.theme.ThemeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Editor : Screen("editor/{carId}") {
        fun createRoute(carId: String) = "editor/$carId"
    }
    data object Library : Screen("library")
    data object Diff : Screen("diff/{setupIdA}/{setupIdB}") {
        fun createRoute(setupIdA: String, setupIdB: String) = "diff/$setupIdA/$setupIdB"
    }
}

private const val TRANSITION_DURATION = 350

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeIn(tween(TRANSITION_DURATION))
            }
        ) {
            HomeScreen(
                onCarSelected = { carId ->
                    navController.navigate(Screen.Editor.createRoute(carId))
                },
                onOpenLibrary = {
                    navController.navigate(Screen.Library.route)
                },
                themeViewModel = themeViewModel
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("carId") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeIn(tween(TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            }
        ) {
            SetupScreen()
        }

        composable(
            route = Screen.Library.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeIn(tween(TRANSITION_DURATION))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            }
        ) {
            SavedSetupsScreen(
                onSetupSelected = { setup ->
                    navController.navigate(Screen.Editor.createRoute(setup.carId))
                },
                onBack = { navController.popBackStack() },
                onCompareTwoSetups = { idA, idB ->
                    navController.navigate(Screen.Diff.createRoute(idA, idB))
                }
            )
        }

        composable(
            route = Screen.Diff.route,
            arguments = listOf(
                navArgument("setupIdA") { type = NavType.StringType },
                navArgument("setupIdB") { type = NavType.StringType }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeIn(tween(TRANSITION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION, easing = EaseInOutCubic)
                ) + fadeOut(tween(TRANSITION_DURATION / 2))
            }
        ) { backStackEntry ->
            val setupIdA = backStackEntry.arguments?.getString("setupIdA") ?: return@composable
            val setupIdB = backStackEntry.arguments?.getString("setupIdB") ?: return@composable
            SetupDiffScreen(
                setupIdA = setupIdA,
                setupIdB = setupIdB,
                onBack = { navController.popBackStack() }
            )
        }
    }
}