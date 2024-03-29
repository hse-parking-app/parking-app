package com.hse.parkingapp.data.repository

import com.auth0.android.jwt.JWT
import com.hse.parkingapp.data.network.AuthApi
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.car.Car
import com.hse.parkingapp.model.reservation.ReservationResult
import com.hse.parkingapp.utils.auth.AuthRequest
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.utils.parking.ParkingManager
import com.hse.parkingapp.utils.token.TokenManager
import retrofit2.Response
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val parkingManager: ParkingManager,
) {
    /**
     * Retrieves the current time from the server and calculates
     * the difference between the local time and the server time.
     * If the difference is not within the range of -12 to 12 hours, it returns null.
     * Otherwise, it saves the difference in hours in shared preferences
     * and returns the local time.
     *
     * @return The local time if the difference is in the range of -12 to 12 hours, null otherwise.
     * @throws DateTimeParseException if the server time cannot be parsed.
     * @throws IOException if there's a network issue or the server cannot be reached.
     * @see ZonedDateTime.parse
     * @see ZonedDateTime.now
     */
    suspend fun getCurrentTime(): ZonedDateTime? {
        val response = authApi.getCurrentTime()

        return if (response.isSuccessful) {
            val serverTime = ZonedDateTime.parse(response.body()?.time ?: "")
            val localTime = ZonedDateTime.now()
            val difference = differenceInHours(localTime, serverTime)

            // If time is incorrect...
            if (difference !in -12..12) {
                return null
            } else {
                parkingManager.saveHoursDifference(localTime.offset.totalSeconds / 3600L)
            }

            localTime
        } else {
            null
        }
    }

    private fun differenceInHours(firstTime: ZonedDateTime, secondTime: ZonedDateTime): Long {
        val commonZoneId = ZoneId.systemDefault()
        val firstTimeConverted = firstTime.withZoneSameLocal(commonZoneId)
        val secondTimeConverted = secondTime.withZoneSameLocal(commonZoneId)

        return Duration.between(firstTimeConverted, secondTimeConverted).toHours()
    }

    suspend fun signIn(email: String, password: String): AuthResult<Unit> {
        val response = authApi.signIn(
            request = AuthRequest(
                email = email,
                password = password
            )
        )

        // Check time for validity
        getCurrentTime() ?: return AuthResult.WrongTime()

        return if (response.isSuccessful) {
            tokenManager.saveAccessToken(response.body()?.accessToken)
            tokenManager.saveRefreshToken(response.body()?.refreshToken)

            val employee = getEmployeeInfo()
            AuthResult.Authorized(employee = employee)
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> AuthResult.WrongCredentials()
                else -> AuthResult.UnknownError()
            }
        }
    }

    suspend fun authenticate(): AuthResult<Unit> {
        val response = authApi.authenticate()

        // Check time for validity
        getCurrentTime() ?: return AuthResult.WrongTime()

        return if (response.isSuccessful) {
            val employee = getEmployeeInfo()

            if (parkingManager.getBuildingId().isNullOrEmpty()) {
                AuthResult.Authorized(employee = employee)
            } else {
                AuthResult.Prepared(employee = employee)
            }
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
    }

    private suspend fun getEmployeeInfo(): Employee {
        val jwt = JWT(tokenManager.getAccessToken() ?: "")

        val employeeCars = getEmployeeCars()
        val employeeReservation = getEmployeeReservation()

        if (parkingManager.getCarId() == null && employeeCars.isNotEmpty()) {
            parkingManager.saveCarId(employeeCars.first().id)
        }

        return Employee(
            id = jwt.getClaim("employee_id").asString() ?: "",
            name = jwt.getClaim("name").asString() ?: "",
            email = jwt.getClaim("sub").asString() ?: "",
            selectedCar = employeeCars.find { it.id == parkingManager.getCarId() } ,
            reservation = employeeReservation
        )
    }

    suspend fun getEmployeeCars(): List<Car> {
        val response = authApi.getEmployeeCars()

        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else {
            listOf()
        }
    }

    private suspend fun getEmployeeReservation(): ReservationResult? {
        val response = authApi.getEmployeeReservation()

        return if (response.isSuccessful) {
            response.body()?.firstOrNull()
        } else {
            null
        }
    }

    /**
     * Adds a car to the server.
     *
     * @param car The car to add.
     * @return The response containing the added car.
     */
    suspend fun addCar(car: Car): Response<Car> {
        return authApi.addCar(car)
    }

    /**
     * Deletes a car from the server.
     *
     * @param carId The ID of the car to delete.
     * @return The response indicating the success or failure of the deletion operation.
     */
    suspend fun deleteCar(carId: String): Response<Unit> {
        return authApi.deleteCar(carId)
    }
}
