package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Canvas(
    val width: Int = 0,
    val height: Int = 0
)
