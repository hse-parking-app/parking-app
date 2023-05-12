package com.hse.parkingapp.ui.main.fab

import androidx.compose.ui.graphics.painter.Painter

/**
 * Data class representing a FAB item.
 * @param icon The icon of the FAB item as a [Painter].
 * @param clickable The click listener for the FAB item.
 */
data class FabItem(
    val icon: Painter,
    val clickable: () -> Unit,
)
