package com.hse.parkingapp.model.spot

import com.hse.parkingapp.model.canvas.Canvas
import kotlinx.serialization.Serializable

@Serializable
data class Spot(
    val id: String = "",
    val levelId: String = "",
    val buildingId: String = "",
    val parkingNumber: String = "",
    val isFree: Boolean = true,
    val canvas: Canvas = Canvas(),
    val onCanvasCoords: OnCanvasCoords = OnCanvasCoords()
)
