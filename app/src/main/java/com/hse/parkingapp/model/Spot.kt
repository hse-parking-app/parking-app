package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Spot(
    val id: String = "",
    val levelId: String = "",
    val buildingId: String = "",
    val parkingNumber: String = "",
    val isAvailable: Boolean = true,
    val isFree: Boolean = true,
    val canvas: Canvas = Canvas(),
    val onCanvasCoords: OnCanvasCoords = OnCanvasCoords()
)
