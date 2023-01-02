package com.hse.parkingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hse.parkingapp.ui.MainViewModel
import com.hse.parkingapp.ui.main.MainScreen
import com.hse.parkingapp.ui.signin.SignInScreen

@Composable
fun ParkingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ParkingDestinations.SIGN_IN_ROUTE,
    navActions: ParkingNavigationActions = remember(navController) {
        ParkingNavigationActions(navController)
    },
) {
    val viewModel : MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ParkingDestinations.SIGN_IN_ROUTE) {
            SignInScreen(
                onAuthClick = { navActions.navigateToMain() }
            )
        }
        composable(ParkingDestinations.MAIN_ROUTE) {
            MainScreen()
        }
    }
}
