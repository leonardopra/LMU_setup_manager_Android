package com.lmu.setupmanager.navigation

import android.net.Uri
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
import com.lmu.setupmanager.ui.wizard.WizardScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Editor : Screen("editor/{carId}") {
        fun createRoute(carId: String) = "editor/$carId"
    }
    data object Library : Screen("library")
    data object Wizard : Screen("wizard/{carId}/{currentValues}") {
        fun createRoute(carId: String, currentValuesJson: String): String {
            val encoded = Uri.encode(currentValuesJson)
            return "wizard/$carId/$encoded"
        }
    }
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
            SetupScreen(navController = navController)
        }

        composable(Screen.Library.route) {
            SavedSetupsScreen(
                onSetupSelected = { setup ->
                    navController.navigate(Screen.Editor.createRoute(setup.carId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Wizard.route,
            arguments = listOf(
                navArgument("carId") { type = NavType.StringType },
                navArgument("currentValues") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            val currentValuesJson = Uri.decode(
                backStackEntry.arguments?.getString("currentValues") ?: "{}"
            )
            val currentValues: Map<String, Float> = try {
                Json.decodeFromString(currentValuesJson)
            } catch (e: Exception) {
                emptyMap()
            }

            WizardScreen(
                carId = carId,
                currentValues = currentValues,
                onAdjustmentsApplied = { adjustments ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("adjustments", Json.encodeToString(adjustments))
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
