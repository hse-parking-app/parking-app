package com.hse.parkingapp.model

import com.hse.parkingapp.model.car.Car
import com.hse.parkingapp.model.reservation.ReservationResult

/**
 * Represents an employee.
 * @property id The ID of the employee. Default value is an empty string.
 * @property name The name of the employee. Default value is "Egor".
 * @property email The email of the employee. Default value is "egor@egor.egor".
 * @property selectedCar The preferred car to park chosen by user.
 * @property reservation The reservation result associated with the employee. Default value is null.
 * @property isLoading Indicates whether there are some active operations with employee. Default value is false.
 */
data class Employee(
    val id: String = "",
    val name: String = "Egor",
    val email: String = "egor@egor.egor",
    val selectedCar: Car? = null,
    val reservation: ReservationResult? = null,
    val isLoading: Boolean = false,
)
