package com.hse.parkingapp.model.car

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Represents the state of the cars list.
 *
 * @property carsList The mutable list of cars.
 */
data class CarsState(
    val carsList: SnapshotStateList<Car> = mutableStateListOf(),
) {
    /**
     * Inflates the cars list with the provided list of cars.
     *
     * @param list The list of cars to inflate.
     */
    fun inflateCarsList(list: List<Car>) {
        carsList.clear()
        carsList.addAll(list)
    }

    /**
     * Handles the selection of a car.
     *
     * @param car The selected car.
     */
    fun onItemSelected(car: Car) {
        val iterator = carsList.listIterator()

        while (iterator.hasNext()) {
            val listItem = iterator.next()

            iterator.set(
                if (listItem.id == car.id) {
                    car.copy(isSelected = true)
                } else {
                    listItem.copy(isSelected = false)
                }
            )
        }
    }
}
