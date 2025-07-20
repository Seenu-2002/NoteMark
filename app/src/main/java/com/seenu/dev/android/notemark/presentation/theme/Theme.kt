package com.seenu.dev.android.notemark.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    background = Background,
    primary = Primary,
    secondary = Secondary,
    tertiary = Secondary,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onSurfaceVariant = OnSurfaceVariant,
    surface = Surface,
    onSurface = OnSurface,
    surfaceContainerLowest = SurfaceLowest,
    error = Error,
)

val ColorScheme.onboardingBackground: Color
    get() = OnboardingBackground

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(5.dp),
    medium = RoundedCornerShape(15.dp)
)

@Composable
fun NoteMarkTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}