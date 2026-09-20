package ua.kpi.iot_lighting.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MidnightVelvetSlateColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = SurfaceCardSecondary,
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SurfaceCardSecondary,
    onSecondaryContainer = SecondaryDark,
    tertiary = TertiaryDark,
    background = CanvasBase,
    onBackground = OnBackgroundDark,
    surface = SurfaceCard,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceCardSecondary,
    onSurfaceVariant = TextMuted,
    outline = BorderSlate
)

@Composable
fun IoTLightingTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MidnightVelvetSlateColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CanvasBase.toArgb()
                window.navigationBarColor = SurfaceCard.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
