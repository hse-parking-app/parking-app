package com.hse.parkingapp.navigation

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash")
    object BuildingsScreen : Screen("buildings")
    object SignScreen : Screen("sign")
    object MainScreen : Screen("main")
}

data class CurrentScreen(
    val screen: Screen = Screen.SplashScreen
)
