package com.hse.parkingapp.model.day

import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

/**
 * Represents day-related data.
 *
 * @property id The ID of the day. Default value is 0.
 * @property date The date associated with the day. Default value is the current date and time.
 * @property isToday Indicates whether the day is today. Default value is false.
 * @property isLastDayOfMonth Indicates whether the day is the last day of the month. Default value is determined by checking if the day of the month matches the length of the month.
 * @property isSelected Indicates whether the day is selected. Default value is false.
 *
 * @property day The day of the month. It is derived from the `date` property.
 * @property monthStandalone The standalone month name. It is derived from the `date` property.
 * @property month The month name in genitive case. It is derived from the `date` property.
 * @property dayOfWeek The day of the week name. It is derived from the `date` property.
 */
data class DayData(
    val id: Int = 0,
    val date: ZonedDateTime = ZonedDateTime.now(),
    val isToday: Boolean = false,
    val isLastDayOfMonth: Boolean = date.dayOfMonth == date.month.length(false),
    val isSelected: Boolean = false,
) {
    /**
     * Returns a string representation of the day data.
     * @return The string representation in the format "$dayOfWeek, $day $month".
     */
    override fun toString(): String {
        return "$dayOfWeek, $day $month"
    }

    /**
     * Represents the day of the month.
     */
    val day: Int = date.dayOfMonth

    /**
     * Represents the standalone month name.
     */
    val monthStandalone: String = date.month.getDisplayName(
        TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    ).replaceFirstChar(Char::titlecase)

    /**
     * Represents the month name in genitive case.
     */
    private val month: String = date.month.getDisplayName(
        TextStyle.FULL,
        Locale.getDefault()
    )

    /**
     * Represents the day of the week name.
     */
    private val dayOfWeek: String = date.dayOfWeek.getDisplayName(
        TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )
}
