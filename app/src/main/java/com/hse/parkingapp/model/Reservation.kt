package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    val id: String = "",
    val carId: String = "",
    val employeeId: String = "",
    val parkingSpotId: String = "",
    val startTime: String = "",
    val endTime: String = ""
)
