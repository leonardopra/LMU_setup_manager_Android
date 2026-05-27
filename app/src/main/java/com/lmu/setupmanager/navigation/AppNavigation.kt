package com.lmu.setupmanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmu.setupmanager.ui.home.HomeScreen
import com.lmu.setupmanager.ui.saved.SavedSetupsScreen
import com.lmu.setupmanager.ui.setup.SetupScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Editor : Screen("editor/{carId}") {
        fun createRoute(carId: String) = "editor/$carId"
    }
    data object Library : Screen("library")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCarSelected = { carId ->
                    navController.navigate(Screen.Editor.createRoute(carId))
                },
                onOpenLibrary = {
                    navController.navigate(Screen.Library.route)
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) {
            SetupScreen()
        }

        composable(Screen.Library.route) {
            SavedSetupsScreen(
                onSetupSelected = { setup ->
                    navController.navigate(Screen.Editor.createRoute(setup.carId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
