package com.hse.parkingapp.model.level

import com.hse.parkingapp.model.canvas.Canvas
import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id: String = "",
    val buildingId: String = "",
    val layerName: String = "",
    val numberOfSpots: Int = 0,
    val canvas: Canvas = Canvas(),
)
