package com.hse.parkingapp.model.day

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

class DayDataState(
    val dayDataList: SnapshotStateList<DayData> = mutableStateListOf()
) {
    init {
        val todayDate = LocalDateTime.now()
        var id = 0

        // add first day in date selector
        dayDataList.add(
            DayData(
                id = id++,
                date = todayDate,
                isToday = true,
                isSelected = true
            )
        )

        // shift to the next day
        var tempDate = LocalDateTime.of(
            todayDate.year, todayDate.month, todayDate.dayOfMonth + 1, 0, 0
        )
        while (tempDate.month < todayDate.month + 2) {
            dayDataList.add(
                DayData(
                    id = id++,
                    date = tempDate
                )
            )
            tempDate = tempDate.plusDays(1)
        }
    }

    /**
     * Function can be optimized, just by storing the position of the last selected day.
     * Unselect previous selected day and update the position variable.
     * But i don't want to lose clarity of code inside handlers, so won't implement it now.
     * At this moment, we update the entire list in a single pass using its iterator.
     */
    fun onItemSelected(selectedDayData: DayData) {
        val iterator = dayDataList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == selectedDayData.id) {
                    selectedDayData.copy(isSelected = true)
                } else {
                    listItem.copy(isSelected = false)
                }
            )
        }
    }

    fun getCurrentMonthName(): String {
        return getMonthName(LocalDateTime.now())
    }

    fun getNextMonthName(): String {
        return getMonthName(LocalDateTime.now().plusMonths(1))
    }

    private fun getMonthName(date: LocalDateTime): String {
        return date.month.getDisplayName(
            TextStyle.FULL_STANDALONE,
            Locale.getDefault()
        )
    }
}