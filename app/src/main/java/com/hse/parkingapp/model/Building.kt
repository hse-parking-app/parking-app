package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Building(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val numberOfLevels: Int = 0,
    val isSelected: Boolean = false
)
