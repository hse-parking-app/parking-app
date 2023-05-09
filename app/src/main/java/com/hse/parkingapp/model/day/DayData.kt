package com.hse.parkingapp.model.day

import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

// TODO: think about leap year processing
data class DayData(
    val id: Int = 0,
    val date: ZonedDateTime = ZonedDateTime.now(),
    val isToday: Boolean = false,
    val isLastDayOfMonth: Boolean = date.dayOfMonth == date.month.length(false),
    val isSelected: Boolean = false
) {
    override fun toString(): String {
        return "$dayOfWeek, $day $month"
    }

    val day: Int = date.dayOfMonth
    val month: String = date.month.getDisplayName(
        TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )
    private val dayOfWeek: String = date.dayOfWeek.getDisplayName(
        TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )
}
