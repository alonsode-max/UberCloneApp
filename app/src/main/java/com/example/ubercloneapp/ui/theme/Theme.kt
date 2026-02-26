package com.example.ubercloneapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary          = UberGreen,
    onPrimary        = Color.White,
    primaryContainer = UberGreen.copy(alpha = 0.12f),
    surface          = SurfaceLight,
    background       = Color.White,
    error            = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary          = UberGreen,
    onPrimary        = Color.White,
    primaryContainer = UberGreenDark,
    surface          = SurfaceDark,
    background       = Color(0xFF121212),
    onSurface        = OnSurfaceDark,
    onBackground     = OnSurfaceDark,
    error            = ErrorRed,
)

@Composable
fun UberCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}