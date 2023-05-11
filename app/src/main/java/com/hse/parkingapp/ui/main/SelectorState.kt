package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.time.TimeData

/**
 * Data class representing the state of the selector.
 * @param selectedDay The selected day data.
 * @param selectedTime The selected time data.
 * @param selectedSpot The selected spot, or null if no spot is selected.
 * @param selectedLevel The selected level.
 */
data class SelectorState(
    val selectedDay: DayData = DayData(),
    val selectedTime: TimeData = TimeData(),
    val selectedSpot: Spot? = null,
    val selectedLevel: Level = Level()
)