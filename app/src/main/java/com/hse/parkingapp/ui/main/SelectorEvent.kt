package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.spot.Spot

sealed class SelectorEvent {
    class DayChanged(val day: DayData): SelectorEvent()
    class TimeChanged(val time: String): SelectorEvent()
    class SpotChanged(val spot: Spot): SelectorEvent()
    object SpotBooked: SelectorEvent()
}