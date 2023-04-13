package com.hse.parkingapp.model.canvas

import kotlinx.serialization.Serializable

@Serializable
data class Canvas(
    val width: Int = 0,
    val height: Int = 0
)
