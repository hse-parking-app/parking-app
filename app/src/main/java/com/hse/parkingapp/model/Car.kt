package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val id: String = "",
    val ownerId: String = "",
    val model: String = "",
    val lengthMeters: Double = 0.0,
    val weightTons: Double = 0.0,
    val registryNumber: String = "В007ОР152"
)
