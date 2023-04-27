package com.hse.parkingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hse.parkingapp.ui.main.MainScreen
import com.hse.parkingapp.ui.signin.SignInScreen

@Composable
fun ParkingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SignScreen.route
) {
    val viewModel = MainViewModel(NavigateActions(navController))

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.SignScreen.route) {
            SignInScreen(
                authenticationState = viewModel.authenticationState.collectAsState().value,
                handleEvent = viewModel::handleAuthenticationEvent
            )
        }
        composable(route = Screen.MainScreen.route) {
            MainScreen(
                selectorState = viewModel.selectorState.collectAsState().value,
                handleEvent = viewModel::handleSelectorEvent,
                parking = viewModel.parking.collectAsState().value,
                dayDataState = viewModel.daysList.collectAsState().value
            )
        }
    }
}
