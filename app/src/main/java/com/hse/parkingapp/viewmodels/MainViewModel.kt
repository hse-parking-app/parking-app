package com.hse.parkingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.parkingapp.data.repository.AuthRepository
import com.hse.parkingapp.data.repository.ParkingRepository
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
import com.hse.parkingapp.model.parking.Parking
import com.hse.parkingapp.model.spot.Spot
import com.hse.parkingapp.navigation.Screen
import com.hse.parkingapp.ui.main.SelectorEvent
import com.hse.parkingapp.ui.main.SelectorState
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.ui.signin.AuthenticationEvent
import com.hse.parkingapp.ui.signin.AuthenticationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val parkingRepository: ParkingRepository
) : ViewModel() {

    // Add current screen to control application flow in future
    val currentScreen = MutableStateFlow(Screen.SignScreen)
    val isAppLaunching = MutableStateFlow(true)

    val parking = MutableStateFlow(Parking())
    val daysList = MutableStateFlow(DayDataState())

    val authenticationState = MutableStateFlow(AuthenticationState())
    val selectorState = MutableStateFlow(SelectorState(
        selectedDay = daysList.value.dayDataList.first()
    ))

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    init {
        authenticate()
    }

    fun handleAuthenticationEvent(authenticationEvent: AuthenticationEvent) {
        when (authenticationEvent) {
            is AuthenticationEvent.EmailChanged -> {
                updateEmail(authenticationEvent.email)
            }
            is AuthenticationEvent.PasswordChanged -> {
                updatePassword(authenticationEvent.password)
            }
            is AuthenticationEvent.SignIn -> {
                signIn()
            }
            is AuthenticationEvent.Authenticate -> {
                authenticate()
            }
        }
    }

    private fun updateEmail(email: String) {
        authenticationState.value = authenticationState.value.copy(
            email = email
        )
    }

    private fun updatePassword(password: String) {
        authenticationState.value = authenticationState.value.copy(
            password = password
        )
    }

    private fun signIn() {
        viewModelScope.launch {
            authenticationState.value = authenticationState.value.copy(isLoading = true)

            val result = authRepository.signIn(
                email = authenticationState.value.email ?: "",
                password = authenticationState.value.password ?: ""
            )

            if (result::class == AuthResult.Authorized::class) {
                inflateParking()
            }
            resultChannel.send(result)

            authenticationState.value = authenticationState.value.copy(isLoading = false)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            authenticationState.value = authenticationState.value.copy(isLoading = true)

            val result = authRepository.authenticate()
            if (result::class == AuthResult.Authorized::class) {
                inflateParking()
            }
            resultChannel.send(result)
            delay(50)  // hahaha, classic...

            isAppLaunching.value = false

            authenticationState.value = authenticationState.value.copy(isLoading = false)
        }
    }

    fun handleSelectorEvent(selectorEvent: SelectorEvent) {
        when (selectorEvent) {
            is SelectorEvent.DayChanged -> {
                updateDay(selectorEvent.day)
            }
            is SelectorEvent.TimeChanged -> {
                // TODO: implement the logic in future
            }
            is SelectorEvent.SpotChanged -> {
                updateSlot(selectorEvent.spot)
            }
            is SelectorEvent.SpotBooked -> {
                // TODO: implement the logic in future
            }
        }
    }

    private fun updateSlot(spot: Spot) {
        selectorState.value = selectorState.value.copy(
            selectedSpot = spot
        )
    }

    private fun updateDay(day: DayData) {
        selectorState.value = selectorState.value.copy(
            selectedDay = day
        )
        daysList.value.onItemSelected(day)
    }

    private suspend fun inflateParking() {
        val building = parkingRepository.getBuildings().body()!![0]
        val levels = parkingRepository.getBuildingLevels(
            buildingId = building.id
        ).body()!!
        val spots = parkingRepository.getLevelSpots(levels[1].id).body()!!

        parking.value = parking.value.copy(
            building = building,
            levels = levels,
            spots = spots
        )
    }
}