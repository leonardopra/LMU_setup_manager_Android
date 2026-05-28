package com.lmu.setupmanager.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.ui.home.HomeScreen
import com.lmu.setupmanager.ui.saved.SavedSetupsScreen
import com.lmu.setupmanager.ui.setup.SetupScreen
import com.lmu.setupmanager.ui.wizard.WizardScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Editor : Screen("editor/{carId}/{trackId}/{conditions}") {
        fun createRoute(carId: String, trackId: String, conditions: Conditions): String {
            val safeTrackId = if (trackId.isEmpty()) "_" else Uri.encode(trackId)
            return "editor/$carId/$safeTrackId/${conditions.name}"
        }
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
                onCarSelected = { carId, trackId, conditions ->
                    navController.navigate(Screen.Editor.createRoute(carId, trackId, conditions))
                },
                onOpenLibrary = {
                    navController.navigate(Screen.Library.route)
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument("carId") { type = NavType.StringType },
                navArgument("trackId") { type = NavType.StringType },
                navArgument("conditions") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            val rawTrackId = backStackEntry.arguments?.getString("trackId") ?: "_"
            val trackId = if (rawTrackId == "_") "" else Uri.decode(rawTrackId)
            val conditionsStr = backStackEntry.arguments?.getString("conditions") ?: "DRY"
            val conditions = try {
                Conditions.valueOf(conditionsStr)
            } catch (e: Exception) {
                Conditions.DRY
            }
            SetupScreen(
                navController = navController,
                carId = carId,
                trackId = trackId,
                conditions = conditions
            )
        }

        composable(Screen.Library.route) {
            SavedSetupsScreen(
                onSetupSelected = { setup ->
                    navController.navigate(
                        Screen.Editor.createRoute(setup.carId, setup.trackId, setup.conditions)
                    )
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
