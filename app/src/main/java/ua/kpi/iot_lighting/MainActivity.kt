package ua.kpi.iot_lighting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.kpi.iot_lighting.composables.AuthScreen
import ua.kpi.iot_lighting.composables.EnergyCalcScreen
import ua.kpi.iot_lighting.composables.LightControlScreen
import ua.kpi.iot_lighting.composables.SensorMonitorScreen
import ua.kpi.iot_lighting.navigation.ScreenRoute
import ua.kpi.iot_lighting.ui.theme.AmberLightPrimary
import ua.kpi.iot_lighting.ui.theme.BorderSlate
import ua.kpi.iot_lighting.ui.theme.CanvasBase
import ua.kpi.iot_lighting.ui.theme.CyanSensor
import ua.kpi.iot_lighting.ui.theme.IoTLightingTheme
import ua.kpi.iot_lighting.ui.theme.SurfaceCard
import ua.kpi.iot_lighting.ui.theme.SurfaceCardSecondary
import ua.kpi.iot_lighting.ui.theme.TextMuted

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IoTLightingTheme {
                MainAppScaffold()
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    var currentScreen by remember { mutableStateOf(ScreenRoute.SENSOR_MONITOR) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CanvasBase,
        topBar = {
            TopAppBarHeader(currentScreen = currentScreen)
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, BorderSlate)
            ) {
                ScreenRoute.values().forEach { route ->
                    val isSelected = currentScreen == route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = route },
                        icon = {
                            Icon(
                                imageVector = route.icon,
                                contentDescription = route.title
                            )
                        },
                        label = {
                            Text(
                                text = route.shortLabel,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CanvasBase,
                            selectedTextColor = AmberLightPrimary,
                            indicatorColor = AmberLightPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                ScreenRoute.SENSOR_MONITOR -> SensorMonitorScreen()
                ScreenRoute.ENERGY_CALC -> EnergyCalcScreen()
                ScreenRoute.LIGHT_CONTROL -> LightControlScreen()
                ScreenRoute.AUTH -> AuthScreen()
            }
        }
    }
}

@Composable
private fun TopAppBarHeader(currentScreen: ScreenRoute) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .border(1.dp, BorderSlate)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "IoT Lighting System",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AmberLightPrimary
            )
            Text(
                text = currentScreen.title,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceCardSecondary)
                    .border(1.dp, BorderSlate, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentScreen.labNumber,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanSensor,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AmberLightPrimary.copy(alpha = 0.15f))
                    .border(1.dp, AmberLightPrimary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Вар. 13",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberLightPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
