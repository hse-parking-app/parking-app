package com.hse.parkingapp.ui.main

import com.hse.parkingapp.model.Spot
import com.hse.parkingapp.model.car.Car
import com.hse.parkingapp.model.day.DayData
import com.hse.parkingapp.model.level.LevelData
import com.hse.parkingapp.model.time.TimeData

/**
 * Sealed class representing events related to the selector.
 */
sealed class SelectorEvent {
    /**
     * Event indicating that the selected day has changed.
     * @param day The new selected day.
     */
    class DayChanged(val day: DayData) : SelectorEvent()

    /**
     * Event indicating that the selected time has changed.
     * @param time The new selected time.
     */
    class TimeChanged(val time: TimeData) : SelectorEvent()

    /**
     * Event indicating that the selected spot has changed.
     * @param spot The new selected spot.
     */
    class SpotChanged(val spot: Spot) : SelectorEvent()

    /**
     * Event indicating that a spot has been booked.
     */
    object SpotBooked : SelectorEvent()

    /**
     * Event indicating a cancellation of a reservation.
     */
    object CancelReservation : SelectorEvent()

    /**
     * Event indicating that the selected level has changed.
     * @param level The new selected level.
     */
    class LevelChanged(val level: LevelData) : SelectorEvent()

    /**
     * Event indicating a comeback to login screen
     */
    object Exit : SelectorEvent()

    /**
     * Event indicating a comeback to buildings selection screen
     */
    object OpenBuildings : SelectorEvent()

    /**
     * Event indicating an event to launch car selection menu
     */
    object OpenCars : SelectorEvent()

    /**
     * Event indicating a comeback to buildings selection screen
     */
    class SelectCar(val car: Car) : SelectorEvent()

    /**
     * Event indicating a process of car addition
     */
    class AddCar(
        val model: String,
        val registryNumber: String
    ) : SelectorEvent()
}
