package com.hse.parkingapp.model

import com.hse.parkingapp.model.level.Level

/**
 * Data class representing parking information.
 * @param level The level of the parking.
 * @param spots The list of spots in the parking.
 * @param isLoading The boolean flag showing is parking loading at the moment.
 * @param isEmpty The boolean flag showing is parking empty.
 */
data class Parking(
    val level: Level = Level(),
    val spots: List<Spot> = listOf(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
)
