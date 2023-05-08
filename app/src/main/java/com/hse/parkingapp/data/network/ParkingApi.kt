package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.Level
import com.hse.parkingapp.model.Spot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ParkingApi {
    @GET("building")
    suspend fun getBuildings(): Response<List<Building>>

    @GET("building/{buildingId}/levels")
    suspend fun getBuildingLevels(@Path("buildingId") buildingId: String): Response<List<Level>>

    @GET("parkingLevels/{levelId}")
    suspend fun getLevel(@Path("levelId") levelId: String): Response<Level>

    @GET("parkingLevels/{levelId}/spots")
    suspend fun getLevelSpots(@Path("levelId") levelId: String): Response<List<Spot>>
}