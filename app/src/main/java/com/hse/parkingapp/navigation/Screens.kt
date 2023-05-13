package com.hse.parkingapp.navigation

/**
 * Represents a sealed class for different screens in an application.
 *
 * Each screen is represented by an object with a unique route.
 *
 * @property route The route associated with the screen.
 */
sealed class Screen(val route: String) {
    /**
     * Represents the SplashScreen with its associated route.
     */
    object SplashScreen : Screen("splash")

    /**
     * Represents the BuildingsScreen with its associated route.
     */
    object BuildingsScreen : Screen("buildings")

    /**
     * Represents the SignScreen with its associated route.
     */
    object SignScreen : Screen("sign")

    /**
     * Represents the MainScreen with its associated route.
     */
    object MainScreen : Screen("main")
}

/**
 * Represents the current screen of the application.
 *
 * @property screen The current [Screen] being displayed. Defaults to [Screen.SplashScreen].
 */
data class CurrentScreen(
    val screen: Screen = Screen.SplashScreen,
)
