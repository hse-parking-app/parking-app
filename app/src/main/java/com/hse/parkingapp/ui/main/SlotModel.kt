package com.hse.parkingapp.ui.main

data class Slot(
    val x: Int = 0,
    val y: Int = 0,
    val parkingNumber: String = "",
    val isVertical: Boolean = false,  // will be done in future
    val isAvailable: Boolean = false
)

val slotsExamples = listOf(
    Slot(0, 0, "A1"),
    Slot(0, 60 + 12, "A2", isAvailable = true),
    Slot(0, 132 + 12, "A3"),
    Slot(0, 204 + 12, "A4"),
    Slot(0, 276 + 12, "A5"),
//    Slot(0, 348 + 12, "A6"),
//    Slot(0, 420 + 12, "A7"),
//    Slot(0, 492 + 12, "A8"),
//    Slot(0, 564 + 12, "A9"),
//    Slot(0, 636 + 12, "A10"),
    Slot(140 + 110, 0, "B1"),
    Slot(140 + 110, 60 + 12, "B2"),
    Slot(140 + 110, 132 + 12, "B3"),
    Slot(140 + 110, 204 + 12, "B4", isAvailable = true),
    Slot(140 + 110, 276 + 12, "B5"),
//    Slot(140 + 110 + 140 + 110, 0, "C1"),
//    Slot(140 + 110 + 140 + 110, 60 + 12, "C2"),
//    Slot(140 + 110 + 140 + 110, 132 + 12, "C3"),
//    Slot(140 + 110 + 140 + 110, 204 + 12, "C4"),
//    Slot(140 + 110 + 140 + 110, 276 + 12, "C5"),
)