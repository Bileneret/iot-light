package ua.kpi.iot_lighting.ui.theme

import androidx.compose.ui.graphics.Color

// Midnight Velvet Slate Theme (Rule 3)
val CanvasBase = Color(0xFF0D1117)
val SurfaceCard = Color(0xFF161B22)
val SurfaceCardSecondary = Color(0xFF21262D)
val BorderSlate = Color(0xFF30363D)

// IoT Lighting Accents
val AmberLightPrimary = Color(0xFFFFB74D) // Warm light accent
val AmberLightSecondary = Color(0xFFFFD54F)
val CyanSensor = Color(0xFF4DD0E1)       // Lux sensor / IoT accent
val EmeraldEco = Color(0xFF4CAF50)        // Energy savings / optimal
val CoralAlert = Color(0xFFFF5252)        // High lux / alerts

// Standard Palette Mappings
val PrimaryDark = AmberLightPrimary
val SecondaryDark = CyanSensor
val TertiaryDark = EmeraldEco
val BackgroundDark = CanvasBase
val SurfaceDark = SurfaceCard
val OnPrimaryDark = Color(0xFF1C1917)
val OnSecondaryDark = Color(0xFF00363D)
val OnBackgroundDark = Color(0xFFF0F6FC)
val OnSurfaceDark = Color(0xFFF0F6FC)
val TextMuted = Color(0xFF8B949E)
