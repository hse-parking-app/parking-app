package com.hse.parkingapp.model

data class Parking(
    val building: Building = Building(),
    val levels: List<Level> = emptyList(),
    val spots: List<Spot> = emptyList()
)
