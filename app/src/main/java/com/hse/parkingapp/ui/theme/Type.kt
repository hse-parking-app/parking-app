package com.hse.parkingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hse.parkingapp.R

val sfProDisplay = FontFamily(
    Font(R.font.sfpro_display_regular),
    Font(
        R.font.sfpro_display_semibold,
        weight = FontWeight.SemiBold
    ),
    Font(
        R.font.sfpro_display_bold,
        weight = FontWeight.Bold
    )
)

val sfProText = FontFamily(
    Font(R.font.sfpro_regular),
    Font(
        R.font.sfpro_semibold,
        weight = FontWeight.SemiBold
    ),
    Font(
        R.font.sfpro_medium,
        weight = FontWeight.Medium
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = sfProText,
        fontWeight = FontWeight.Medium,
        fontSize = 100.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = sfProDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = sfProText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = sfProDisplay,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = sfProDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = sfProDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = sfProText,
        fontSize = 13.sp
    ),
    labelMedium = TextStyle(
        fontFamily = sfProText,
        fontSize = 17.sp
    ),
    displayMedium = TextStyle(
        fontFamily = sfProDisplay,
        fontSize = 28.sp
    )
)
