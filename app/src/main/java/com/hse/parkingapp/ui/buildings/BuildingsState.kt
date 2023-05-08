package com.hse.parkingapp.ui.buildings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.hse.parkingapp.model.Building

data class BuildingsState(
    val buildingList: SnapshotStateList<Building> = mutableStateListOf(),
    val selectedBuilding: Building? = null,
    val isLoading: Boolean = false
) {
    fun inflateBuildingList(list: List<Building>) {
        buildingList.addAll(list)
        buildingList[0] = buildingList.first().copy(
            isSelected = true
        )
    }

    fun onItemSelected(building: Building) {
        val iterator = buildingList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == building.id) {
                    building.copy(isSelected = true)
                } else {
                    listItem.copy(isSelected = false)
                }
            )
        }
    }
}