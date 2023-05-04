package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class OnCanvasCoords(
    val x: Int = 0,
    val y: Int = 0
)
