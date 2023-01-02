package com.hse.parkingapp.ui

sealed class AuthenticationEvent {
    object ToggleAuthenticationMode: AuthenticationEvent()
    class UsernameChanged(val username: String): AuthenticationEvent()
    class PasswordChanged(val password: String): AuthenticationEvent()
    object Authenticate: AuthenticationEvent()
    object ErrorDismissed: AuthenticationEvent()
}