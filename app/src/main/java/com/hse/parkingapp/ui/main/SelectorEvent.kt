package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.time.TimeData

sealed class SelectorEvent {
    class DayChanged(val day: DayData): SelectorEvent()
    class TimeChanged(val time: TimeData): SelectorEvent()
    class SpotChanged(val spot: Spot): SelectorEvent()
    object SpotBooked: SelectorEvent()
}