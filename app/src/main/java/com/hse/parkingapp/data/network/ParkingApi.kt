package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.reservation.ReservationRequest
import com.hse.parkingapp.model.reservation.ReservationResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ParkingApi {
    @GET("building")
    suspend fun getBuildings(): Response<List<Building>>

    @GET("building/{buildingId}/levels")
    suspend fun getBuildingLevels(@Path("buildingId") buildingId: String): Response<List<Level>>

    @GET("parkingLevels/{levelId}")
    suspend fun getLevel(@Path("levelId") levelId: String): Response<Level>

    @GET("parkingLevels/{levelId}/freeSpotsInInterval")
    suspend fun getFreeSpotsInInterval(
        @Path("levelId") levelId: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String,
    ): Response<List<Spot>>

    @POST("reservations/employee")
    suspend fun createReservation(
        @Body reservationRequest: ReservationRequest,
    ): Response<ReservationResult>

    @GET("reservations/employee")
    suspend fun getReservation(): Response<List<ReservationResult>>

    @GET("parkingSpots/{spotId}")
    suspend fun getSpotInformation(@Path("spotId") spotId: String): Response<Spot>

    @DELETE("reservations/{reservationId}/employee")
    suspend fun deleteReservation(
        @Path("reservationId") reservationId: String,
    ): Response<Unit>

    @GET("parkingLevels/{levelId}/spots")
    suspend fun getAllSpotsOnLevel(
        @Path("levelId") levelId: String,
    ): Response<List<Spot>>
}
