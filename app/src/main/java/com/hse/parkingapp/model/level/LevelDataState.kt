package com.hse.parkingapp.model.level

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Class representing the state of level data.
 * @param levels The list of levels.
 * @param selectedLevelId The ID of the selected level.
 * @param levelDataList The list of level data.
 */
class LevelDataState(
    levels: List<Level> = listOf(),
    val selectedLevelId: String = "",
    val levelDataList: SnapshotStateList<LevelData> = mutableStateListOf(),
) {
    init {
        var id = 0

        levels.forEach { level ->
            levelDataList.add(
                LevelData(
                    id = id++,
                    level = level,
                    isSelected = level.id == selectedLevelId
                )
            )
        }

        levelDataList.sortByDescending { it.level.levelNumber }
    }

    /**
     * Updates the state when a level is selected.
     * @param selectedLevelData The selected level data.
     */
    fun onItemSelected(selectedLevelData: LevelData) {
        val iterator = levelDataList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == selectedLevelData.id) {
                    selectedLevelData.copy(isSelected = true)
                } else {
                    listItem.copy(isSelected = false)
                }
            )
        }
    }
}
