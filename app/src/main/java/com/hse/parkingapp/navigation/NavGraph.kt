package com.hse.parkingapp.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hse.parkingapp.ui.main.MainScreen
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.ui.signin.SignInScreen
import com.hse.parkingapp.ui.splash.SplashScreen
import com.hse.parkingapp.viewmodels.MainViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SplashScreen.route
) {
    val context = LocalContext.current

    // Block of code which is responsible for
    // initial navigation between splash and other screens
    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized -> {
                    navigateToMainScreen(navController)
                }
                is AuthResult.Unauthorized, is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "You're not authorized",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToSignScreen(navController)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen()
        }
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

private fun navigateToMainScreen(navController: NavHostController) {
    navController.navigate(Screen.MainScreen.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
    }
}

fun navigateToSignScreen(navController: NavHostController) {
    navController.navigate(Screen.SignScreen.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
    }
}
