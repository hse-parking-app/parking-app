package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.time.TimeData

data class SelectorState(
    val selectedDay: DayData = DayData(),
    val selectedTime: TimeData = TimeData(),
    val selectedSpot: Spot? = null,
)