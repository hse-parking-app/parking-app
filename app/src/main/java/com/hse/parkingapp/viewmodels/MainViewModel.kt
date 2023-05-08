package com.hse.parkingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.parkingapp.data.repository.AuthRepository
import com.hse.parkingapp.data.repository.ParkingRepository
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
import com.hse.parkingapp.model.Parking
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.Level
import com.hse.parkingapp.navigation.CurrentScreen
import com.hse.parkingapp.navigation.Screen
import com.hse.parkingapp.ui.buildings.BuildingsEvent
import com.hse.parkingapp.ui.buildings.BuildingsState
import com.hse.parkingapp.ui.main.SelectorEvent
import com.hse.parkingapp.ui.main.SelectorState
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.ui.signin.AuthenticationEvent
import com.hse.parkingapp.ui.signin.AuthenticationState
import com.hse.parkingapp.utils.parking.ParkingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val parkingRepository: ParkingRepository,
    private val parkingManager: ParkingManager
) : ViewModel() {

    // These channels are responsible for network error handling
    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    val currentScreen = MutableStateFlow(CurrentScreen())

    val employee = MutableStateFlow(Employee())
    val parking = MutableStateFlow(Parking())
    val daysList = MutableStateFlow(DayDataState())

    val authenticationState = MutableStateFlow(AuthenticationState())
    val selectorState = MutableStateFlow(SelectorState(
        selectedDay = daysList.value.dayDataList.first()
    ))

    val buildingsState = MutableStateFlow(BuildingsState())

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
                updateEmployee(result.employee)
                inflateBuildings()

                changeCurrentScreen(newScreen = Screen.BuildingsScreen)
            }
            resultChannel.send(result)

            authenticationState.value = authenticationState.value.copy(isLoading = false)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = authRepository.authenticate()
            updateEmployee(result.employee)

            if (result::class == AuthResult.Authorized::class) {
                inflateBuildings()
                changeCurrentScreen(newScreen = Screen.BuildingsScreen)
            } else if (result::class == AuthResult.Prepared::class) {
                inflateParking()
                changeCurrentScreen(newScreen = Screen.MainScreen)
            } else {
                changeCurrentScreen(newScreen = Screen.SignScreen)
            }

            resultChannel.send(result)
        }
    }

    private fun changeCurrentScreen(newScreen: Screen) {
        currentScreen.value = currentScreen.value.copy(
            screen = newScreen
        )
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
                updateSpot(selectorEvent.spot)
            }
            is SelectorEvent.SpotBooked -> {
                // TODO: implement the logic in future
            }
        }
    }

    private fun updateSpot(spot: Spot) {
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

    private fun updateEmployee(newEmployee: Employee?) {
        employee.value = employee.value.copy(
            id = newEmployee?.id ?: "",
            name = newEmployee?.name ?: "Egor",
            email = newEmployee?.email ?: "egor@egor.egor",
            cars = newEmployee?.cars ?: mutableListOf(),
            reservation = newEmployee?.reservation
        )
    }

    fun handleBuildingsEvent(buildingsEvent: BuildingsEvent) {
        when(buildingsEvent) {
            is BuildingsEvent.OnBuildingClick -> {
                onBuildingClick(buildingsEvent.building)
            }
            is BuildingsEvent.OnContinueClick -> {
                onContinueClick()
            }
        }
    }

    private fun onContinueClick() {
        viewModelScope.launch {
            buildingsState.value = buildingsState.value.copy(isLoading = true)

            parkingManager.saveBuildingId(
                id = buildingsState.value.selectedBuilding?.id
            )

            val levels = parkingRepository.getBuildingLevels(
                buildingId = parkingManager.getBuildingId() ?: ""
            ).body()
            parkingManager.saveLevelId(levels?.first()?.id)

            val spots = parkingRepository.getLevelSpots(
                levelId = levels?.first()?.id ?: ""
            ).body()

            val firstLevel = levels?.first() ?: Level()
            parking.value = parking.value.copy(
                level = firstLevel,
                spots = spots ?: listOf()
            )

            changeCurrentScreen(newScreen = Screen.MainScreen)
            resultChannel.send(authRepository.authenticate())

            buildingsState.value = buildingsState.value.copy(isLoading = false)
        }
    }

    private suspend fun inflateBuildings() {
        val response = parkingRepository.getBuildings()

        if (response.isSuccessful) {
            buildingsState.value.inflateBuildingList(response.body() ?: listOf())

            if (!response.body().isNullOrEmpty()) {
                val firstBuilding = buildingsState.value.buildingList.first()

                buildingsState.value = buildingsState.value.copy(
                    selectedBuilding = firstBuilding
                )
            }
        }
    }

    private suspend fun inflateParking() {
        parkingManager.saveBuildingId(
            id = buildingsState.value.selectedBuilding?.id
        )

        val level = parkingRepository.getLevel(
            levelId = parkingManager.getLevelId() ?: ""
        ).body()

        val spots = parkingRepository.getLevelSpots(
            levelId = level?.id ?: ""
        ).body()

        parking.value = parking.value.copy(
            level = level ?: Level(),
            spots = spots ?: listOf()
        )
    }

    private fun onBuildingClick(building: Building) {
        buildingsState.value.onItemSelected(building)
        buildingsState.value = buildingsState.value.copy(
            selectedBuilding = building
        )
    }
}