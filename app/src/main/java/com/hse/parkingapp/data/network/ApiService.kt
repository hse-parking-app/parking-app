package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.building.Building
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.spot.Spot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("building")
    suspend fun getBuildings(): Response<List<Building>>

    @GET("building/{buildingId}/levels")
    suspend fun getBuildingLevels(@Path("buildingId") buildingId: String): Response<List<Level>>

    @GET("parkingLevels/{levelId}/spots")
    suspend fun getLevelSpots(@Path("levelId") levelId: String): Response<List<Spot>>
}