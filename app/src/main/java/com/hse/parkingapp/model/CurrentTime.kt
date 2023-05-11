package com.hse.parkingapp.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentTime(
    val time: String = ""
)
