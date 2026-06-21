package com.yunqi.app.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CloudPink,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = LeafGreen,
    tertiary = Sky,
    background = SurfaceWarm,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Ink,
    surfaceVariant = MintSoft,
    onSurfaceVariant = Slate,
)

private val DarkColors = darkColorScheme(
    primary = CloudRose,
    secondary = MintSoft,
    tertiary = Sky,
)

@Composable
fun YunqiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = YunqiTypography,
        content = content,
    )
}

