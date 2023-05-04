package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.Car
import com.hse.parkingapp.model.Reservation
import com.hse.parkingapp.utils.auth.AuthRequest
import com.hse.parkingapp.utils.auth.RefreshRequest
import com.hse.parkingapp.utils.token.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/signUp")
    suspend fun signUp(
        @Body request: AuthRequest
    ): Response<Unit>

    @POST("auth/login")
    suspend fun signIn(
        @Body request: AuthRequest
    ): Response<TokenResponse>

    @GET("auth/whoami")
    suspend fun authenticate(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("auth/update/access")
    suspend fun updateAccessToken(
        @Body refreshRequest: RefreshRequest
    ): Response<TokenResponse>

    @POST("auth/update/refresh")
    suspend fun updateRefreshToken(
        @Body refreshRequest: RefreshRequest
    ): Response<TokenResponse>

    @GET("cars/employee")
    suspend fun getEmployeeCars(): Response<List<Car>>

    @GET("reservations/employee")
    suspend fun getEmployeeReservation(): Response<List<Reservation>>
}