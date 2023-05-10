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

    /**
     * Retrieves the list of free spots within a specified time interval for a given level.
     *
     * @param levelId The ID of the level.
     * @param startTime The start time of the interval in string format.
     * @param endTime The end time of the interval in string format.
     * @return A Response object containing the list of free spots.
     */
    suspend fun getFreeSpotsInInterval(
        levelId: String,
        startTime: String,
        endTime: String
    ): Response<List<Spot>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getFreeSpotsInInterval(
                levelId = levelId,
                startTime = startTime,
                endTime = endTime
            )
        }
    }
}