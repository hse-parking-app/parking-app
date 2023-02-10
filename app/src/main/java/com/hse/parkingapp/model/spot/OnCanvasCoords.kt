package com.hse.parkingapp.model.spot

import kotlinx.serialization.Serializable

@Serializable
data class OnCanvasCoords(
    val x: Int = 0,
    val y: Int = 0
)
