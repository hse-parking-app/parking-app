package com.hse.parkingapp.model.reservation

import kotlinx.serialization.Serializable

/**
 * Represents a reservation request.
 *
 * @property carId The ID of the car for the reservation. Default value is an empty string.
 * @property employeeId The ID of the employee making the reservation. Default value is an empty string.
 * @property parkingSpotId The ID of the parking spot for the reservation. Default value is an empty string.
 * @property startTime The start time of the reservation in string format. Default value is an empty string.
 * @property endTime The end time of the reservation in string format. Default value is an empty string.
 */
@Serializable
data class ReservationRequest(
    val carId: String = "",
    val employeeId: String = "",
    val parkingSpotId: String = "",
    val startTime: String = "",
    val endTime: String = ""
)
