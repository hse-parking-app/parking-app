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
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.level.LevelData
import com.hse.parkingapp.model.level.LevelDataState
import com.hse.parkingapp.model.reservation.Reservation
import com.hse.parkingapp.model.reservation.ReservationRequest
import com.hse.parkingapp.model.time.TimeData
import com.hse.parkingapp.model.time.TimeDataState
import com.hse.parkingapp.navigation.CurrentScreen
import com.hse.parkingapp.navigation.Screen
import com.hse.parkingapp.ui.buildings.BuildingsEvent
import com.hse.parkingapp.ui.buildings.BuildingsState
import com.hse.parkingapp.ui.main.SelectorEvent
import com.hse.parkingapp.ui.main.SelectorState
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.ui.signin.AuthenticationEvent
import com.hse.parkingapp.ui.signin.AuthenticationState
import com.hse.parkingapp.utils.errors.CurrentError
import com.hse.parkingapp.utils.errors.ErrorType
import com.hse.parkingapp.utils.parking.ParkingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.ZonedDateTime
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

    val errors = MutableStateFlow(CurrentError())

    val currentScreen = MutableStateFlow(CurrentScreen())

    val employee = MutableStateFlow(Employee())
    val reservation = MutableStateFlow(Reservation())
    val parking = MutableStateFlow(Parking())

    val daysList = MutableStateFlow(DayDataState())
    val timesList = MutableStateFlow(TimeDataState())
    val levelsList = MutableStateFlow(LevelDataState())

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
                updateEmployeeAndReservation(result.employee)
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
            updateEmployeeAndReservation(result.employee)

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

    private suspend fun inflateDaysRow() {
        daysList.value = DayDataState(
            currentTime = authRepository.getCurrentTime() ?: ZonedDateTime.now()
        )
        updateDay(daysList.value.dayDataList.first())
    }

    private fun inflateTimesRow() {
        timesList.value = TimeDataState(
            currentTime = selectorState.value.selectedDay.date
        )
        if (!timesList.value.timeDataList.isEmpty()) {
            updateTime(timesList.value.timeDataList.first())
        }
        getSpotsList()
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
                updateTime(selectorEvent.time)
            }
            is SelectorEvent.SpotChanged -> {
                updateSpot(selectorEvent.spot)
            }
            is SelectorEvent.SpotBooked -> {
                bookSpot()
            }
            is SelectorEvent.CancelReservation -> {
                cancelReservation()
            }
            is SelectorEvent.LevelChanged -> {
                updateLevel(selectorEvent.level)
            }
        }
    }

    private fun updateLevel(level: LevelData) {
        viewModelScope.launch {
            levelsList.value.onItemSelected(level)
            selectorState.value = selectorState.value.copy(
                selectedLevel = level.level
            )

            parkingManager.saveLevelId(level.level.id)
            inflateParking()
        }
    }

    private fun inflateLevels(levels: List<Level>, selectedLevelId: String? = null) {
        levelsList.value = LevelDataState(
            levels = levels,
            selectedLevelId = selectedLevelId ?: levels.first().id
        )
        parkingManager.saveLevelId(levelsList.value.selectedLevelId)
    }

    private fun cancelReservation() {
        viewModelScope.launch {
            val response = parkingRepository.deleteReservation(
                reservationId = employee.value.reservation?.id ?: ""
            )

            if (response.isSuccessful) {
                authenticate()
            }
        }
    }

    private fun bookSpot() {
        viewModelScope.launch {
            employee.value = employee.value.copy(isLoading = true)

            val reservationRequest = ReservationRequest(
                carId = employee.value.cars.first().id,
                parkingSpotId = selectorState.value.selectedSpot?.id ?: "",
                startTime = prepareTimeForServer(selectorState.value.selectedTime.startTime),
                endTime = prepareTimeForServer(selectorState.value.selectedTime.endTime)
            )

            val response = parkingRepository.createReservation(
                reservationRequest = reservationRequest
            )

            if (!response.isSuccessful) {
                errors.value = errors.value.copy(error = ErrorType.UnknownError)
            }

            updateTime(selectorState.value.selectedTime)

            employee.value = employee.value.copy(isLoading = false)

            authenticate()
        }
    }

    private fun updateTime(time: TimeData) {
        selectorState.value = selectorState.value.copy(
            selectedTime = time
        )
        timesList.value.onItemSelected(time)
        getSpotsList()
    }

    private fun getSpotsList() {
        viewModelScope.launch {
            val spots = if (employee.value.reservation != null || timesList.value.timeDataList.isEmpty()) {
                val rawSpots = parkingRepository.getAllSpotsOnLevel(
                    levelId = parkingManager.getLevelId() ?: ""
                ).body()

                rawSpots?.map{ it.copy(isFree = false) } ?: listOf()
            } else {
                val startTime = prepareTimeForServer(selectorState.value.selectedTime.startTime)
                val endTime = prepareTimeForServer(selectorState.value.selectedTime.endTime)

                val rawSpots = parkingRepository.getFreeSpotsInInterval(
                    levelId = parkingManager.getLevelId() ?: "",
                    startTime = startTime,
                    endTime = endTime
                ).body() ?: listOf()

                rawSpots
            }

            parking.value = parking.value.copy(
                spots = spots
            )
        }
    }

    private fun prepareTimeForServer(time: ZonedDateTime): String {
        return "${time.toLocalDateTime().plusHours(parkingManager.getHoursDifference())}"
    }

    private fun updateSpot(spot: Spot) {
        if (employee.value.cars.isEmpty()) {
            errors.value = errors.value.copy(error = ErrorType.NoCar)
        }

        selectorState.value = selectorState.value.copy(
            selectedSpot = spot
        )
    }

    private fun updateDay(day: DayData) {
        selectorState.value = selectorState.value.copy(
            selectedDay = day
        )
        daysList.value.onItemSelected(day)

        inflateTimesRow()
    }

    private suspend fun updateEmployeeAndReservation(newEmployee: Employee?) {
        employee.value = employee.value.copy(
            id = newEmployee?.id ?: "",
            name = newEmployee?.name ?: "Egor",
            email = newEmployee?.email ?: "egor@egor.egor",
            cars = newEmployee?.cars ?: mutableListOf(),
            reservation = newEmployee?.reservation
        )

        if (employee.value.reservation != null) {
            val reservedSpot = parkingRepository.getSpotInformation(
                spotId = employee.value.reservation?.parkingSpotId ?: ""
            ).body()
            val reservationResult = parkingRepository.getReservation().body()?.first()

            reservation.value = reservation.value.copy(
                spot = reservedSpot ?: Spot(),
                time = TimeData(
                    startTime = ZonedDateTime
                        .parse("${reservationResult?.startTime}Z")
                        .minusHours(parkingManager.getHoursDifference()),
                    endTime = ZonedDateTime
                        .parse("${reservationResult?.endTime}Z")
                        .minusHours(parkingManager.getHoursDifference())
                )
            )
        }
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
            parkingManager.saveLevelId(levels?.get(1)?.id)

            inflateParking()

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
        inflateDaysRow()

        val levels = parkingRepository.getBuildingLevels(
            buildingId = parkingManager.getBuildingId() ?: ""
        ).body()

        inflateLevels(
            levels = levels ?: listOf(),
            selectedLevelId = parkingManager.getLevelId()
        )

        val level = parkingRepository.getLevel(
            levelId = parkingManager.getLevelId() ?: ""
        ).body()

        parking.value = parking.value.copy(
            level = level ?: Level()
        )
    }

    private fun onBuildingClick(building: Building) {
        buildingsState.value.onItemSelected(building)
        buildingsState.value = buildingsState.value.copy(
            selectedBuilding = building
        )
    }
}