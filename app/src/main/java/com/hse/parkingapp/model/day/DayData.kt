package com.hse.parkingapp.model.day

data class DayData(
    val id: Int = 0,
    val day: Int = 0,
    val monthName: String = "January",
    val isToday: Boolean = false,
    val isLastDayOfMonth: Boolean = false,
    val isSelected: Boolean = false
)
