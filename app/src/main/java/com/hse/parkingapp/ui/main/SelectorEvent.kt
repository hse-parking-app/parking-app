package com.hse.parkingapp.ui.main

sealed class SelectorEvent {
    class DayChanged(val day: String): SelectorEvent()
    class TimeChanged(val time: String): SelectorEvent()
    class SlotChanged(val slot: Slot): SelectorEvent()
    object SlotBooked: SelectorEvent()
}