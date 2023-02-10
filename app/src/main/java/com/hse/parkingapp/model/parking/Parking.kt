package com.hse.parkingapp.model.parking

import com.hse.parkingapp.model.building.Building
import com.hse.parkingapp.model.level.Level
import com.hse.parkingapp.model.spot.Spot

data class Parking(
    val building: Building = Building(),
    val levels: List<Level> = emptyList(),
    val spots: List<Spot> = emptyList()
)
