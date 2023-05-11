package com.hse.parkingapp.model.time

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.DayOfWeek
import java.time.ZonedDateTime

/**
 * Represents the state of time data, including the current time and a list of time data.
 *
 * @property currentTime The current time. Default value is the current date and time.
 * @property timeDataList The list of time data. Default value is an empty mutable list.
 */
class TimeDataState(
    currentTime: ZonedDateTime = ZonedDateTime.now(),
    val timeDataList: SnapshotStateList<TimeData> = mutableStateListOf()
) {
    /**
     * Initializes the TimeDataState object with the provided current time.
     * Generates and populates the timeDataList based on the provided current time.
     *
     * @param currentTime The current time.
     */
    init {
        if (!(currentTime.dayOfWeek == DayOfWeek.SATURDAY || currentTime.dayOfWeek == DayOfWeek.SUNDAY)) {
            var id = 0
            var tempTime = currentTime

            // Adjust the tempTime to the nearest hour boundary
            tempTime = if (tempTime.hour < 10) {
                tempTime.withHour(10)
            } else if (tempTime.hour in 10..18) {
                tempTime.plusHours(1)
            } else {
                tempTime
            }
            tempTime = tempTime.withMinute(0).withSecond(0).withNano(0)

            // Generate time data for each hour starting from tempTime
            while (tempTime.hour < 19) {
                // Add selected time data for the current hour
                timeDataList.add(
                    TimeData(
                        id = id++,
                        startTime = tempTime,
                        endTime = tempTime.plusHours(1),
                        isSelected = true
                    )
                )

                tempTime = tempTime.plusHours(1)

                // Add unselected time data for the remaining hours in the day
                while (tempTime.hour < 19) {
                    timeDataList.add(
                        TimeData(
                            id = id++,
                            startTime = tempTime,
                            endTime = tempTime.plusHours(1),
                            isSelected = false
                        )
                    )
                    tempTime = tempTime.plusHours(1)
                }
            }
        }
    }

    /**
     * Updates the selection state of the time data items in the timeDataList.
     * Sets the provided selectedTimeData as selected and unselects the rest.
     *
     * @param selectedTimeData The time data item to be selected.
     */
    fun onItemSelected(selectedTimeData: TimeData) {
        val iterator = timeDataList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == selectedTimeData.id) {
                    selectedTimeData.copy(isSelected = true)
                } else {
                    listItem.copy(isSelected = false)
                }
            )
        }
    }
}
