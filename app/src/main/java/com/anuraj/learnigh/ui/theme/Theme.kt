package com.anuraj.learnigh.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LearnighDarkScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = IndigoLight,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4B2468),
    onSecondaryContainer = MagentaAccent,
    tertiary = InfoCyan,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkMuted,
    outline = DarkOutline,
    outlineVariant = Color(0xFF40385F),
    error = DangerRose,
    onError = Color(0xFF3B0714),
)

private val LearnighShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun LearnighTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity() ?: return@SideEffect
            activity.window.statusBarColor = android.graphics.Color.TRANSPARENT
            activity.window.navigationBarColor = DarkBg.toArgb()
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = LearnighDarkScheme,
        typography = LearnighTypography,
        shapes = LearnighShapes,
        content = content,
    )
}

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)
