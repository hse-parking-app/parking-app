package com.hse.parkingapp.ui

enum class AuthenticationMode {
    SIGN_UP,
    SIGN_IN
}

enum class PasswordRequirements {
    CAPITAL_LETTER,
    NUMBER,
    EIGHT_CHARACTERS
}

data class AuthenticationState(
    val authenticationMode: AuthenticationMode =
        AuthenticationMode.SIGN_IN,
    val username: String? = null,
    val password: String? = null,
    val passwordRequirements: List<PasswordRequirements> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun isFormValid(): Boolean {
        return password?.isNotEmpty() == true &&
            username?.isNotEmpty() == true &&
            authenticationMode == AuthenticationMode.SIGN_IN
    }
}
