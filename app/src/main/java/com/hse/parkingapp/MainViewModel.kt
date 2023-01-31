package com.hse.parkingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hse.parkingapp.ui.main.SelectorEvent
import com.hse.parkingapp.ui.main.SelectorState
import com.hse.parkingapp.ui.main.Slot
import com.hse.parkingapp.ui.signin.AuthenticationEvent
import com.hse.parkingapp.ui.signin.AuthenticationMode
import com.hse.parkingapp.ui.signin.AuthenticationState
import com.hse.parkingapp.ui.signin.PasswordRequirements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel factory to create a ViewModel with parameters
class MainViewModelFactory(private val navigationActions: ParkingNavigationActions)
    : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel(navigationActions) as T
}

class MainViewModel(private val navigationActions: ParkingNavigationActions) : ViewModel() {
    val authenticationState = MutableStateFlow(AuthenticationState())
    val selectorState = MutableStateFlow(SelectorState())

    fun handleAuthenticationEvent(authenticationEvent: AuthenticationEvent) {
        when (authenticationEvent) {
            is AuthenticationEvent.ToggleAuthenticationMode -> {
                toggleAuthenticationMode()
            }
            is AuthenticationEvent.UsernameChanged -> {
                updateUsername(authenticationEvent.username)
            }
            is AuthenticationEvent.PasswordChanged -> {
                updatePassword(authenticationEvent.password)
            }
            is AuthenticationEvent.Authenticate -> {
                authenticate()
            }
            is AuthenticationEvent.ErrorDismissed -> {
                dismissError()
            }
        }
    }

    private fun toggleAuthenticationMode() {
        val authenticationMode = authenticationState.value.authenticationMode
        val newAuthenticationMode = if (authenticationMode == AuthenticationMode.SIGN_IN)
            AuthenticationMode.SIGN_UP else AuthenticationMode.SIGN_IN
        authenticationState.value = authenticationState.value.copy(
            authenticationMode = newAuthenticationMode
        )
    }

    private fun updateUsername(username: String) {
        authenticationState.value = authenticationState.value.copy(
            username = username
        )
    }

    private fun updatePassword(password: String) {
        val requirements = mutableListOf<PasswordRequirements>()

        if (password.length > 7) {
            requirements.add(PasswordRequirements.EIGHT_CHARACTERS)
        }
        if (password.any { it.isUpperCase() }) {
            requirements.add(PasswordRequirements.CAPITAL_LETTER)
        }
        if (password.any { it.isDigit() }) {
            requirements.add(PasswordRequirements.NUMBER)
        }

        authenticationState.value = authenticationState.value.copy(
            password = password,
            passwordRequirements = requirements.toList()
        )
    }

    private fun authenticate() {
        authenticationState.value = authenticationState.value.copy(
            isLoading = true
        )
        // TODO: trigger network request
        viewModelScope.launch {
            delay(2000L)

            withContext(Dispatchers.Main) {
                authenticationState.value = authenticationState.value.copy(
                    isLoading = false,
                    error = null  // change error string here to trigger authentication error
                )
            }

            navigationActions.navigateToMain()
        }
    }

    private fun dismissError() {
        authenticationState.value = authenticationState.value.copy(
            error = null
        )
    }

    fun handleSelectorEvent(selectorEvent: SelectorEvent) {
        when (selectorEvent) {
            is SelectorEvent.DayChanged -> {
                // TODO: implement the logic in future
            }
            is SelectorEvent.TimeChanged -> {
                // TODO: implement the logic in future
            }
            is SelectorEvent.SlotChanged -> {
                updateSlot(selectorEvent.slot)
            }
            is SelectorEvent.BookSlot -> {
                // TODO: implement the logic in future
            }
        }
    }

    private fun updateSlot(slot: Slot) {
        selectorState.value = selectorState.value.copy(
            selectedSlot = slot
        )
    }
}