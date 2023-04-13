package com.hse.parkingapp.model.building

import kotlinx.serialization.Serializable

@Serializable
data class Building(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val numberOfLevels: Int = 0
)
