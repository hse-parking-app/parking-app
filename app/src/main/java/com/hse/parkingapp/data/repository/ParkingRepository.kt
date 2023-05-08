package com.hse.parkingapp.data.repository

import com.hse.parkingapp.data.network.ParkingApi
import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.Level
import com.hse.parkingapp.model.Spot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ParkingRepository(
    private val parkingApi: ParkingApi
) {
    suspend fun getBuildings(): Response<List<Building>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getBuildings()
        }
    }

    suspend fun getLevel(levelId: String): Response<Level> {
        return withContext(Dispatchers.IO) {
            parkingApi.getLevel(levelId)
        }
    }

    suspend fun getBuildingLevels(buildingId: String): Response<List<Level>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getBuildingLevels(buildingId)
        }
    }

    suspend fun getLevelSpots(levelId: String): Response<List<Spot>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getLevelSpots(levelId)
        }
    }
}