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
        var tempDate = LocalDateTime.now()

        var id = 0
        while (tempDate.month < todayDate.month + 2) {
            dayDataList.add(
                DayData(
                    id = id++,
                    day = tempDate.dayOfMonth,
                    monthName = getMonthName(tempDate),
                    isToday = isToday(todayDate, tempDate),
                    isLastDayOfMonth = isLastDayOfMonth(tempDate),
                    isSelected = isToday(todayDate, tempDate)
                )
            )

            tempDate = tempDate.plusDays(1)
        }
    }

    // were updating the entire list in a single pass using its iterator
    fun onItemSelected(selectedDayData: DayData) {
        val iterator = dayDataList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == selectedDayData.id) {
                    selectedDayData
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

    private fun isToday(todayDate: LocalDateTime, tempDate: LocalDateTime): Boolean {
        return tempDate.dayOfMonth == todayDate.dayOfMonth &&
                tempDate.month == todayDate.month
    }

    private fun isLastDayOfMonth(tempDate: LocalDateTime): Boolean {
        return tempDate.dayOfMonth == tempDate.month.length(false)
    }
}