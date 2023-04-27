package com.hse.parkingapp

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    object SignScreen : Screen("sign")
    object MainScreen : Screen("main")
}

class NavigateActions(private val navController: NavHostController) {
    fun navigateToMain() {
        navController.navigate(Screen.MainScreen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }
}
