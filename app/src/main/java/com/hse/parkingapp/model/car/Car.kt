package com.hse.parkingapp.model.car

import kotlinx.serialization.Serializable

/**
 * Represents a car entity.
 *
 * @property id The unique identifier of the car.
 * @property ownerId The identifier of the car owner.
 * @property model The model of the car.
 * @property lengthMeters The length of the car in meters.
 * @property weightTons The weight of the car in tons.
 * @property registryNumber The registry number of the car.
 * @property isSelected Indicates whether the car is selected or not.
 */
@Serializable
data class Car(
    val id: String = "",
    val ownerId: String = "",
    val model: String = "",
    val lengthMeters: Double = 1.0,
    val weightTons: Double = 1.0,
    val registryNumber: String = "В007ОР152",
    val isSelected: Boolean = false
)
