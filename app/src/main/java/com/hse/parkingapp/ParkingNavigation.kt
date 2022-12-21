package com.hse.parkingapp

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.hse.parkingapp.ParkingScreens.MAIN_SCREEN
import com.hse.parkingapp.ParkingScreens.SIGN_IN_SCREEN

private object ParkingScreens {

    const val SIGN_IN_SCREEN = "signin"
    const val MAIN_SCREEN = "main"
}

object ParkingDestinationsArgs {
    // For the moment when we need args
}

object ParkingDestinations {

    const val SIGN_IN_ROUTE = SIGN_IN_SCREEN
    const val MAIN_ROUTE = MAIN_SCREEN
}

class ParkingNavigationActions(private val navController: NavHostController) {

    fun navigateToMain() {
        navController.navigate(ParkingDestinations.MAIN_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }
}
