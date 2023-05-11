package com.hse.parkingapp.model.level

import com.hse.parkingapp.model.Canvas
import kotlinx.serialization.Serializable

/**
 * Serializable data class representing a level.
 * @param id The ID of the level.
 * @param buildingId The ID of the building to which the level belongs.
 * @param levelNumber The number of the level.
 * @param numberOfSpots The number of spots on the level.
 * @param canvas The canvas information associated with the level.
 */
@Serializable
data class Level(
    val id: String = "",
    val buildingId: String = "",
    val levelNumber: String = "",
    val numberOfSpots: Int = 0,
    val canvas: Canvas = Canvas(),
)
