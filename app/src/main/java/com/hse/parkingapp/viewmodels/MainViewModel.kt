package com.hse.parkingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hse.parkingapp.data.repository.AuthRepository
import com.hse.parkingapp.data.repository.ParkingRepository
import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.Parking
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.car.Car
import com.hse.parkingapp.model.car.CarsState
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.day.DayDataState
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
import com.hse.parkingapp.ui.signin.AuthenticationEvent
import com.hse.parkingapp.ui.signin.AuthenticationState
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.utils.errors.CurrentError
import com.hse.parkingapp.utils.errors.ErrorType
import com.hse.parkingapp.utils.parking.ParkingManager
import com.hse.parkingapp.utils.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val parkingRepository: ParkingRepository,
    private val parkingManager: ParkingManager,
    private val tokenManager: TokenManager,
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
    val carsList = MutableStateFlow(CarsState())
    val buildingsList = MutableStateFlow(BuildingsState())

    val authenticationState = MutableStateFlow(AuthenticationState())
    val selectorState = MutableStateFlow(
        SelectorState(
            selectedDay = daysList.value.dayDataList.first()
        )
    )

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

                navigateTo(screen = Screen.BuildingsScreen)
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
                parkingManager.deleteCarId()
                inflateBuildings()
                navigateTo(screen = Screen.BuildingsScreen)
            } else if (result::class == AuthResult.Prepared::class) {
                inflateParking()
                navigateTo(screen = Screen.MainScreen)
            } else {
                parkingManager.deleteCarId()
                navigateTo(screen = Screen.SignScreen)
            }

            resultChannel.send(result)
        }
    }

    private suspend fun inflateDaysRow() {
        daysList.value = DayDataState(
            currentTime = authRepository.getCurrentTime() ?: ZonedDateTime.now()
        )

        val firstDay = daysList.value.dayDataList.first()
        selectorState.value = selectorState.value.copy(selectedDay = firstDay)
        daysList.value.onItemSelected(firstDay)
    }

    private fun inflateTimesRow() {
        timesList.value = TimeDataState(
            currentTime = selectorState.value.selectedDay.date
        )
        if (!timesList.value.timeDataList.isEmpty()) {
            val firstTime = timesList.value.timeDataList.first()
            selectorState.value = selectorState.value.copy(selectedTime = firstTime)
            timesList.value.onItemSelected(firstTime)
        }
    }

    private fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            if (screen == Screen.BuildingsScreen) {
                inflateBuildings()
            }

            currentScreen.value = currentScreen.value.copy(
                screen = screen
            )
        }
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

            is SelectorEvent.Exit -> {
                exit()
            }

            is SelectorEvent.OpenBuildings -> {
                navigateTo(screen = Screen.BuildingsScreen)
            }

            is SelectorEvent.OpenCars -> {
                refreshCarsList()
            }

            is SelectorEvent.SelectCar -> {
                updateCar(selectorEvent.car)
            }

            is SelectorEvent.AddCar -> {
                addNewCar(selectorEvent.model, selectorEvent.registryNumber)
            }
        }
    }

    private fun refreshCarsList() {
        viewModelScope.launch {
            updateEmployeeAndReservation(employee.value)
        }
    }

    private fun addNewCar(model: String, registryNumber: String) {
        viewModelScope.launch {
            employee.value = employee.value.copy(isLoading = true)

            val response = authRepository.addCar(
                Car(
                    model = model,
                    registryNumber = registryNumber
                )
            )
            updateEmployeeAndReservation(employee.value)
            updateCar(response.body() ?: Car())

            employee.value = employee.value.copy(isLoading = false)
        }
    }

    private fun updateCar(car: Car) {
        carsList.value.onItemSelected(car)
        parkingManager.saveCarId(car.id)
        employee.value = employee.value.copy(selectedCar = car)
    }

    private fun exit() {
        tokenManager.deleteAccessToken()
        tokenManager.deleteRefreshToken()

        parkingManager.deleteCarId()

        navigateTo(screen = Screen.SignScreen)
    }

    private fun updateLevel(level: LevelData) {
        viewModelScope.launch {
            levelsList.value.onItemSelected(level)
            selectorState.value = selectorState.value.copy(
                selectedLevel = level.level
            )

            parkingManager.saveLevelId(level.level.id)

            inflateDaysRow()
            inflateTimesRow()
            inflateSpotsList()
        }
    }

    private suspend fun inflateLevels() {
        val buildingLevels = parkingRepository.getBuildingLevels(
            buildingId = parkingManager.getBuildingId() ?: ""
        ).body()

        if (buildingLevels.isNullOrEmpty()) {
            levelsList.value = LevelDataState()  // pass LevelDataState with empty levels list
            parking.value = parking.value.copy(isEmpty = true)
        } else {
            if (parkingManager.getLevelId() !in buildingLevels.map { it.id }
                || parkingManager.getLevelId() == null
            ) {
                parkingManager.saveLevelId(buildingLevels.first().id)
            }

            levelsList.value = LevelDataState(
                levels = buildingLevels,
                selectedLevelId = parkingManager.getLevelId() ?: ""
            )

            val selectedLevel = buildingLevels.find { it.id == parkingManager.getLevelId() }

            parking.value = parking.value.copy(
                level = selectedLevel ?: Level(),
                isEmpty = false
            )
        }
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
                carId = employee.value.selectedCar?.id ?: "",
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

            employee.value = employee.value.copy(isLoading = false)

            authenticate()
        }
    }

    private fun updateTime(time: TimeData) {
        selectorState.value = selectorState.value.copy(
            selectedTime = time
        )
        timesList.value.onItemSelected(time)

        inflateSpotsList()
    }

    private fun inflateSpotsList() {
        viewModelScope.launch {
            parking.value = parking.value.copy(isLoading = true)
            delay(250)
            parking.value = parking.value.copy(spots = listOf())

            val spots =
                if (parking.value.isEmpty) {
                    listOf()
                } else if (employee.value.reservation != null || timesList.value.timeDataList.isEmpty()) {
                    val rawSpots = parkingRepository.getAllSpotsOnLevel(
                        levelId = parkingManager.getLevelId() ?: ""
                    ).body()

                    rawSpots?.map { it.copy(isFree = false) } ?: listOf()
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
                spots = spots,
                isEmpty = spots.isEmpty()
            )

            parking.value = parking.value.copy(isLoading = false)
        }
    }

    private fun prepareTimeForServer(time: ZonedDateTime): String {
        return "${time.toLocalDateTime().plusHours(parkingManager.getHoursDifference())}"
    }

    private fun updateSpot(spot: Spot) {
        if (employee.value.selectedCar == null) {
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
        inflateSpotsList()
    }

    private suspend fun updateEmployeeAndReservation(newEmployee: Employee?) {
        employee.value = newEmployee ?: Employee()

        carsList.value.inflateCarsList(
            authRepository.getEmployeeCars().map {
                it.copy(isSelected = it.id == parkingManager.getCarId())
            }
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
        when (buildingsEvent) {
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
            buildingsList.value = buildingsList.value.copy(isLoading = true)

            parkingManager.saveBuildingId(
                id = buildingsList.value.selectedBuilding?.id
            )

            inflateParking()

            navigateTo(screen = Screen.MainScreen)
            resultChannel.send(authRepository.authenticate())

            buildingsList.value = buildingsList.value.copy(isLoading = false)
        }
    }

    private suspend fun inflateBuildings() {
        val response = parkingRepository.getBuildings()

        if (response.isSuccessful) {
            buildingsList.value.inflateBuildingList(response.body() ?: listOf())

            if (!response.body().isNullOrEmpty()) {
                val firstBuilding = buildingsList.value.buildingList.first()

                buildingsList.value = buildingsList.value.copy(
                    selectedBuilding = firstBuilding
                )
            }
        }
    }

    private suspend fun inflateParking() {
        inflateLevels()
        inflateDaysRow()
        inflateTimesRow()
        inflateSpotsList()
    }

    private fun onBuildingClick(building: Building) {
        buildingsList.value.onItemSelected(building)
        buildingsList.value = buildingsList.value.copy(
            selectedBuilding = building
        )

        parkingManager.saveBuildingId(building.id)
    }
}
