package com.example.memori.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.memori.R

val notoSansSC = FontFamily(
    Font(R.font.notosans_sc_thin, FontWeight.Thin),
    Font(R.font.notosans_sc_extralight, FontWeight.ExtraLight),
    Font(R.font.notosans_sc_light, FontWeight.Light),
    Font(R.font.notosans_sc_regular, FontWeight.Normal),
    Font(R.font.notosans_sc_medium, FontWeight.Medium),
    Font(R.font.notosans_sc_semibold, FontWeight.SemiBold),
    Font(R.font.notosans_sc_bold, FontWeight.Bold),
    Font(R.font.notosans_sc_extrabold, FontWeight.ExtraBold),
    Font(R.font.notosans_sc_black, FontWeight.Black),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = notoSansSC,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)