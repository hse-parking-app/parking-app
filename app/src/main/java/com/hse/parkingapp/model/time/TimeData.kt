package com.hse.parkingapp.model.time

import java.time.ZonedDateTime

/**
 * Represents time data with an ID, start time, end time, and selection status.
 *
 * @property id The unique identifier for the time data. Default value is 0.
 * @property startTime The start time of the period represented by this time data.
 *                     Default value is the current date and time.
 * @property endTime The end time of the period represented by this time data.
 *                   Default value is the current date and time.
 * @property isSelected Indicates whether this time data is selected or not.
 *                      Default value is false.
 */
data class TimeData(
    val id: Int = 0,
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    val isSelected: Boolean = false
) {
    /**
     * Returns a string representation of the time data.
     * The format is "${startTime.hour}:00 - ${endTime.hour}:00".
     *
     * @return The string representation of the time data.
     */
    override fun toString(): String {
        return "${startTime.hour}:00 - ${endTime.hour}:00"
    }

    /**
     * Returns a period string indicating the start and end time of the time data.
     * The format is "с ${startTime.hour}:00 до ${endTime.hour}:00".
     *
     * @return The period string.
     */
    fun getPeriod(): String {
        return "с ${startTime.hour}:00 до ${endTime.hour}:00"
    }
}
