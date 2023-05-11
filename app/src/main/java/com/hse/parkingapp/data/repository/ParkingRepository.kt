package com.hse.parkingapp.data.repository

import com.hse.parkingapp.data.network.ParkingApi
import com.hse.parkingapp.model.Building
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.reservation.ReservationRequest
import com.hse.parkingapp.model.reservation.ReservationResult
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

    /**
     * Creates a reservation using the specified reservation request.
     * @param reservationRequest The reservation request containing the necessary information for the reservation.
     * @return A Response object containing the result of the reservation.
     */
    suspend fun createReservation(
        reservationRequest: ReservationRequest
    ): Response<ReservationResult> {
        return withContext(Dispatchers.IO) {
            parkingApi.createReservation(
                reservationRequest = reservationRequest
            )
        }
    }

    /**
     * Retrieves employee's reservation by making a suspend network call.
     * @return A response containing the reservation result.
     */
    suspend fun getReservation(): Response<List<ReservationResult>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getReservation()
        }
    }

    /**
     * Retrieves information for about parking spot by its ID.
     *
     * @param spotId The ID of the parking spot.
     * @return A response containing the spot information.
     */
    suspend fun getSpotInformation(spotId: String): Response<Spot> {
        return withContext(Dispatchers.IO) {
            parkingApi.getSpotInformation(spotId)
        }
    }

    /**
     * Deletes a reservation by its ID.
     * @param reservationId The ID of the reservation to delete.
     * @return A response indicating the success or failure of the deletion.
     */
    suspend fun deleteReservation(reservationId: String): Response<Unit> {
        return withContext(Dispatchers.IO) {
            parkingApi.deleteReservation(reservationId)
        }
    }

    /**
     * Retrieves all spots on a level by the level ID.
     * @param levelId The ID of the level.
     * @return A response containing a list of spots on the level.
     */
    suspend fun getAllSpotsOnLevel(levelId: String): Response<List<Spot>> {
        return withContext(Dispatchers.IO) {
            parkingApi.getAllSpotsOnLevel(levelId)
        }
    }
}