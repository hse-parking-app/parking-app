package com.hse.parkingapp.model

import com.hse.parkingapp.model.level.Level

/**
 * Data class representing parking information.
 * @param level The level of the parking.
 * @param spots The list of spots in the parking.
 */
data class Parking(
    val level: Level = Level(),
    val spots: List<Spot> = listOf(),
)
