package com.hse.parkingapp.data.repository

import com.auth0.android.jwt.JWT
import com.hse.parkingapp.data.network.AuthApi
import com.hse.parkingapp.model.Car
import com.hse.parkingapp.model.Employee
import com.hse.parkingapp.model.Reservation
import com.hse.parkingapp.utils.auth.AuthRequest
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.utils.auth.RefreshRequest
import com.hse.parkingapp.utils.token.TokenManager

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun signUp(email: String, password: String): AuthResult<Unit> {
        val response = authApi.signUp(
            request = AuthRequest(
                email = email,
                password = password
            )
        )

        return if (response.isSuccessful) {
            signIn(email, password)
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult<Unit> {
        val response = authApi.signIn(
            request = AuthRequest(
                email = email,
                password = password
            )
        )

        return if (response.isSuccessful) {
            tokenManager.saveAccessToken(response.body()?.accessToken)
            tokenManager.saveRefreshToken(response.body()?.refreshToken)

            val employee = getEmployeeInfo()
            AuthResult.Authorized(employee = employee)
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
    }

    suspend fun authenticate(): AuthResult<Unit> {
        val response = authApi.authenticate(
            token = "Bearer ${tokenManager.getAccessToken()}"
        )

        return if (response.isSuccessful) {
            val employee = getEmployeeInfo()
            AuthResult.Authorized(employee = employee)
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> updateAccessToken()
                else -> AuthResult.UnknownError()
            }
        }
    }

    private suspend fun updateAccessToken(): AuthResult<Unit> {
        val response = authApi.updateAccessToken(
            refreshRequest = RefreshRequest(
                refreshToken = tokenManager.getRefreshToken()
            )
        )

        return if (response.isSuccessful) {
            tokenManager.saveAccessToken(response.body()?.accessToken)
            tokenManager.saveRefreshToken(response.body()?.refreshToken)

            val employee = getEmployeeInfo()
            AuthResult.Authorized(employee = employee)
        } else {
            when (response.code()) {
                400, 401, 403, 405 -> updateRefreshToken()
                else -> AuthResult.UnknownError()
            }
        }
    }

    private suspend fun updateRefreshToken(): AuthResult<Unit> {
        val response = authApi.updateRefreshToken(
            refreshRequest = RefreshRequest(
                refreshToken = tokenManager.getRefreshToken()
            )
        )

        return if (response.isSuccessful) {
            tokenManager.saveAccessToken(response.body()?.accessToken)
            tokenManager.saveRefreshToken(response.body()?.refreshToken)

            val employee = getEmployeeInfo()
            AuthResult.Authorized(employee = employee)
        } else {
            when (response.code()) {
                400, 401, 403 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
    }

    private suspend fun getEmployeeInfo(): Employee {
        val jwt = JWT(tokenManager.getAccessToken() ?: "")

        val employeeCars = getEmployeeCars()
        val employeeReservation = getEmployeeReservation()

        return Employee(
            id = jwt.getClaim("employee_id").asString() ?: "",
            name = jwt.getClaim("name").asString() ?: "",
            email = jwt.getClaim("sub").asString() ?: "",
            cars = employeeCars.toMutableList(),
            reservation = employeeReservation
        )
    }

    private suspend fun getEmployeeCars(): List<Car> {
        val response = authApi.getEmployeeCars()

        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else {
            listOf()
        }
    }

    private suspend fun getEmployeeReservation(): Reservation? {
        val response = authApi.getEmployeeReservation()

        return if (response.isSuccessful) {
            response.body()?.firstOrNull()
        } else {
            null
        }
    }
}