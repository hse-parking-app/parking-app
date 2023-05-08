package com.hse.parkingapp.model

data class Parking(
    val level: Level = Level(),
    val spots: List<Spot> = listOf()
)
