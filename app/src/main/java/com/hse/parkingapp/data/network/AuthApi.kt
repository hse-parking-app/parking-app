package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.Car
import com.hse.parkingapp.model.CurrentTime
import com.hse.parkingapp.model.Reservation
import com.hse.parkingapp.utils.auth.AuthRequest
import com.hse.parkingapp.utils.auth.RefreshRequest
import com.hse.parkingapp.utils.token.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun signIn(
        @Body request: AuthRequest
    ): Response<TokenResponse>

    @GET("auth/whoami")
    suspend fun authenticate(): Response<Unit>

    @POST("auth/update/access")
    suspend fun updateAccessToken(
        @Body refreshRequest: RefreshRequest
    ): Response<TokenResponse>

    @GET("cars/employee")
    suspend fun getEmployeeCars(): Response<List<Car>>

    @GET("reservations/employee")
    suspend fun getEmployeeReservation(): Response<List<Reservation>>

    @GET("time/current")
    suspend fun getCurrentTime(): Response<CurrentTime>
}