package com.hse.parkingapp.model.reservation

import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.time.TimeData

/**
 * Represents a reservation.
 * @property spot The spot associated with the reservation. Default value is an instance of Spot with default values.
 * @property time The time data for the reservation. Default value is an instance of TimeData with default values.
 */
data class Reservation(
    val spot: Spot = Spot(),
    val time: TimeData = TimeData(),
)
