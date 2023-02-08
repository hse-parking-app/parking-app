package com.hse.parkingapp.data.network

import com.hse.parkingapp.model.building.Building
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("building")
    suspend fun getBuildings(): Response<List<Building>>
}