package com.hse.parkingapp.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hse.parkingapp.ui.main.MainScreen
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.ui.signin.SignInScreen
import com.hse.parkingapp.ui.splash.SplashScreen
import com.hse.parkingapp.ui.buildings.BuildingsScreen
import com.hse.parkingapp.utils.errors.ErrorType
import com.hse.parkingapp.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SplashScreen.route
) {
    val context = LocalContext.current
    val currentScreen = viewModel.currentScreen.collectAsState().value.screen
    val errors = viewModel.errors.collectAsState().value.error

    // Block of code which is responsible for
    // navigation between screens and
    // making toast messages
    LaunchedEffect(viewModel, context, currentScreen, errors) {
        when(currentScreen) {
            is Screen.SplashScreen -> navigateTo(Screen.SplashScreen, navController)
            is Screen.BuildingsScreen -> navigateTo(Screen.BuildingsScreen, navController)
            is Screen.SignScreen -> navigateTo(Screen.SignScreen, navController)
            is Screen.MainScreen -> navigateTo(Screen.MainScreen, navController)
        }

        viewModel.authResults.collectLatest { result ->
            when(result) {
                is AuthResult.WrongTime -> showErrorToast(context, "Wrong local time")
                is AuthResult.Unauthorized, is AuthResult.UnknownError -> {
                    showErrorToast(context, "You're not authorized")
                    navigateTo(Screen.SignScreen, navController)
                }
                else -> {  }
            }
        }

        when(errors) {
            is ErrorType.NoCar -> { showErrorToast(context, "You don't have a car!") }
            is ErrorType.UnknownError -> { showErrorToast(context, "Unknown error") }
            is ErrorType.NoError -> {  }
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
        composable(route = Screen.BuildingsScreen.route) {
            BuildingsScreen(
                buildingsState = viewModel.buildingsState.collectAsState().value,
                handleEvent = viewModel::handleBuildingsEvent
            )
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
                dayDataState = viewModel.daysList.collectAsState().value,
                timeDataState = viewModel.timesList.collectAsState().value,
                employee = viewModel.employee.collectAsState().value,
                reservation = viewModel.reservation.collectAsState().value
            )
        }
    }
}

/**
 * Navigates to the specified [screen] using the provided [navController].
 *
 * This function is used to handle navigation between different screens of the app.
 * It takes a [Screen] object as an input, which represents the destination screen, and a
 * [NavHostController] to manage navigation.
 *
 * @param screen The destination [Screen] to navigate to.
 * @param navController The [NavHostController] used for navigation.
 */
private fun navigateTo(screen: Screen, navController: NavHostController) {
    navController.navigate(screen.route) {
        popUpTo(0)
    }
}

private fun showErrorToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
