package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.spot.Spot

data class SelectorState(
    val selectedDay: DayData = DayData(),
    val selectedTime: Int = 0,
    val selectedSpot: Spot? = null,
)