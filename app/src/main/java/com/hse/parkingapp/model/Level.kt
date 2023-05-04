package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id: String = "",
    val buildingId: String = "",
    val layerName: String = "",
    val numberOfSpots: Int = 0,
    val canvas: Canvas = Canvas(),
)
