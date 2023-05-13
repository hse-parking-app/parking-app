package com.hse.parkingapp.model.reservation

/**
 * Represents the result of a reservation.
 *
 * @property id The ID of the reservation. Default value is an empty string.
 * @property carId The ID of the car associated with the reservation. Default value is an empty string.
 * @property employeeId The ID of the employee who made the reservation. Default value is an empty string.
 * @property parkingSpotId The ID of the parking spot reserved. Default value is an empty string.
 * @property startTime The start time of the reservation in string format. Default value is an empty string.
 * @property endTime The end time of the reservation in string format. Default value is an empty string.
 */
data class ReservationResult(
    val id: String = "",
    val carId: String = "",
    val employeeId: String = "",
    val parkingSpotId: String = "",
    val startTime: String = "",
    val endTime: String = "",
)
