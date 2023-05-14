package com.hse.parkingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hse.parkingapp.R

val sfProRounded = FontFamily(
    Font(R.font.sfpro_rounded_regular),
    Font(
        R.font.sfpro_rounded_semibold,
        weight = FontWeight.SemiBold
    ),
    Font(
        R.font.sfpro_rounded_medium,
        weight = FontWeight.Medium
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = sfProRounded,
        fontWeight = FontWeight.Medium,
        fontSize = 100.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = sfProRounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = sfProRounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = sfProRounded,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = sfProRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = sfProRounded,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = sfProRounded,
        fontSize = 13.sp
    ),
    labelMedium = TextStyle(
        fontFamily = sfProRounded,
        fontSize = 17.sp
    ),
    displayMedium = TextStyle(
        fontFamily = sfProRounded,
        fontSize = 24.sp
    )
)
