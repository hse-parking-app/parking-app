package com.hse.parkingapp.model

data class Employee(
    val id: String = "",
    val name: String = "Egor",
    val email: String = "egor@egor.egor",
    val cars: MutableList<Car> = mutableListOf(),
    val reservation: Reservation? = null
)
