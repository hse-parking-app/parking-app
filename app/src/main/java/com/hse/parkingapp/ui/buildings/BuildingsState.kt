package com.hse.parkingapp.ui.buildings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.hse.parkingapp.model.Building

/**
 * Data class representing the state of the buildings.
 * @param buildingList The list of buildings as a [SnapshotStateList].
 * @param selectedBuilding The currently selected building, or null if no building is selected.
 * @param isLoading A flag indicating whether the data is being loaded or not.
 */
data class BuildingsState(
    val buildingList: SnapshotStateList<Building> = mutableStateListOf(),
    val selectedBuilding: Building? = null,
    val isLoading: Boolean = false,
) {
    /**
     * Clears the building list and then inflates it with the provided [list] of buildings.
     * @param list The list of buildings to inflate.
     */
    fun inflateBuildingList(list: List<Building>) {
        buildingList.clear()

        buildingList.addAll(list)
        buildingList[0] = buildingList.first().copy(
            isSelected = true
        )
    }

    /**
     * Handles the selection of a building.
     * @param building The selected building.
     */
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
