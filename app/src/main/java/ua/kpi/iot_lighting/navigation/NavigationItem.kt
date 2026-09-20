package ua.kpi.iot_lighting.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenRoute(
    val route: String,
    val title: String,
    val shortLabel: String,
    val icon: ImageVector,
    val labNumber: String
) {
    SENSOR_MONITOR(
        route = "sensor_monitor",
        title = "Моніторинг сенсора",
        shortLabel = "Сенсор",
        icon = Icons.Default.Info,
        labNumber = "ПЗ 1"
    ),
    ENERGY_CALC(
        route = "energy_calc",
        title = "Енергетичний калькулятор",
        shortLabel = "Калькулятор",
        icon = Icons.Default.Settings,
        labNumber = "ПЗ 2"
    ),
    LIGHT_CONTROL(
        route = "light_control",
        title = "Смарт-світильник",
        shortLabel = "Керування",
        icon = Icons.Default.Home,
        labNumber = "ПЗ 3"
    ),
    AUTH(
        route = "auth",
        title = "Авторизація",
        shortLabel = "Профіль",
        icon = Icons.Default.Person,
        labNumber = "ПЗ 4"
    )
}
