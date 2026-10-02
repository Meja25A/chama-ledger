package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChamaGreenPrimaryDark,
    onPrimary = ChamaGreenOnPrimaryDark,
    primaryContainer = ChamaGreenContainerDark,
    onPrimaryContainer = ChamaGreenOnContainerDark,
    secondary = ChamaTealSecondaryDark,
    onSecondary = ChamaTealOnSecondaryDark,
    secondaryContainer = ChamaTealContainerDark,
    onSecondaryContainer = ChamaTealOnContainerDark,
    tertiary = ChamaGoldTertiaryDark,
    onTertiary = ChamaGoldOnTertiaryDark,
    tertiaryContainer = ChamaGoldContainerDark,
    onTertiaryContainer = ChamaGoldOnContainerDark,
    background = ChamaBackgroundDark,
    surface = ChamaSurfaceDark,
    surfaceVariant = ChamaSurfaceVariantDark,
    outline = ChamaOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = ChamaGreenPrimary,
    onPrimary = ChamaGreenOnPrimary,
    primaryContainer = ChamaGreenContainer,
    onPrimaryContainer = ChamaGreenOnContainer,
    secondary = ChamaTealSecondary,
    onSecondary = ChamaTealOnSecondary,
    secondaryContainer = ChamaTealContainer,
    onSecondaryContainer = ChamaTealOnContainer,
    tertiary = ChamaGoldTertiary,
    onTertiary = ChamaGoldOnTertiary,
    tertiaryContainer = ChamaGoldContainer,
    onTertiaryContainer = ChamaGoldOnContainer,
    background = ChamaBackgroundLight,
    surface = ChamaSurfaceLight,
    surfaceVariant = ChamaSurfaceVariantLight,
    outline = ChamaOutlineLight
)

@Composable
fun ChamaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted Kenyan Chama brand palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
