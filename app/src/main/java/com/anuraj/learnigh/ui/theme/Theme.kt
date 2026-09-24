package com.anuraj.learnigh.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat

private val LearnighDarkScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = IndigoLight,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A1D6A),
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
    outlineVariant = Color(0xFF3D3660),
    error = DangerRose,
    onError = Color.White,
)

@Composable
fun LearnighTheme(
    darkTheme: Boolean = true, // always premium dark
    content: @Composable () -> Unit,
) {
    val colorScheme = LearnighDarkScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.parseColor("#0D0B1A")
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LearnighTypography,
        content = content,
    )
}
