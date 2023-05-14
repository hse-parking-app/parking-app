package com.hse.parkingapp.ui.signin

data class AuthenticationState(
    val email: String? = "",
    val password: String? = "",
    val isLoading: Boolean = false,
)
