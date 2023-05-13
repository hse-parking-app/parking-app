package com.hse.parkingapp.ui.signin

sealed class AuthenticationEvent {
    class EmailChanged(val email: String) : AuthenticationEvent()
    class PasswordChanged(val password: String) : AuthenticationEvent()
    object SignIn : AuthenticationEvent()
    object Authenticate : AuthenticationEvent()
}
