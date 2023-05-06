package com.hse.parkingapp.navigation

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash")
    object SignScreen : Screen("sign")
    object MainScreen : Screen("main")
}
