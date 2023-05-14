package com.hse.parkingapp.model.level

/**
 * Data class representing level data.
 * @property id The ID of the level.
 * @property level The level information.
 * @property isSelected Indicates whether the level is selected.
 */
data class LevelData(
    val id: Int = 0,
    val level: Level = Level(),
    val isSelected: Boolean = false,
)
