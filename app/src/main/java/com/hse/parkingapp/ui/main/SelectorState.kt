package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.spot.Spot
import java.time.Month
import java.time.MonthDay
import java.time.format.TextStyle
import java.util.*

data class SelectorState(
    val currentMonth: Month = MonthDay.now().month,
    val selectedDay: MonthDay = MonthDay.of(MonthDay.now().month, MonthDay.now().dayOfMonth),
    val selectedTime: Int = 0,
    val selectedSpot: Spot? = null,
    val currentMonthName: String = currentMonth
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
        .replaceFirstChar { it.titlecase(Locale.getDefault()) },
    val currentMonthLength: Int = currentMonth.length(false),
    val previousMonthLength: Int = (currentMonth - 1).length(false),
    val nextMonthLength: Int = (currentMonth + 1).length(false)
)