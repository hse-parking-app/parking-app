package com.hse.parkingapp.ui.main

import java.time.Month
import java.time.MonthDay

data class SelectorState(
    val currentMonth: Month = MonthDay.now().month,
    val selectedDay: MonthDay = MonthDay.of(MonthDay.now().month, MonthDay.now().dayOfMonth),
    val selectedTime: Int = 0,
    val selectedSlot: Slot? = null
)