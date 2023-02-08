package com.hse.parkingapp.data.repository

import com.hse.parkingapp.model.building.Building
import com.hse.parkingapp.data.network.RetrofitInstance
import retrofit2.Response

class Repository {
    suspend fun getBuildings(): Response<List<Building>> {
        return RetrofitInstance.api.getBuildings()
    }
}