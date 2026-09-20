package ua.kpi.iot_lighting.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SensorReading(
    val lux: Int,
    val timestamp: String,
    val status: String
)

enum class LightComfortLevel(
    val title: String,
    val description: String,
    val recommendation: String
) {
    TOO_DARK(
        title = "Темно",
        description = "< 100 Люкс. Недостатній рівень.",
        recommendation = "Увімкніть основне освітлення"
    ),
    DIM(
        title = "Тьмяно",
        description = "100–300 Люкс. Зона релаксу або коридор.",
        recommendation = "Підходить для орієнтації, недостатньо для читання"
    ),
    COMFORT(
        title = "Комфорт",
        description = "300–500 Люкс. Базовий житловий комфорт.",
        recommendation = "Комфортний рівень для відпочинку та спілкування"
    ),
    OPTIMAL_WORK(
        title = "Оптимально для роботи",
        description = "500–750 Люкс. Стандарт ДСТУ EN 12464-1.",
        recommendation = "Ідеально для навчання, креслення та роботи за ПК"
    ),
    TOO_BRIGHT(
        title = "Занадто яскраво",
        description = "> 750 Люкс. Надмірна інтенсивність.",
        recommendation = "Зменшіть яскравість або приглушіть світло для економії"
    );

    companion object {
        fun fromLux(lux: Float): LightComfortLevel = when {
            lux < 100f -> TOO_DARK
            lux < 300f -> DIM
            lux < 500f -> COMFORT
            lux <= 750f -> OPTIMAL_WORK
            else -> TOO_BRIGHT
        }
    }
}

object SensorDataProvider {
    const val DEVICE_ID = "ESP32-BH1750-V13"
    const val PROTOCOL = "MQTT / Zigbee 3.0"

    fun createReading(lux: Int): SensorReading {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val level = LightComfortLevel.fromLux(lux.toFloat())
        return SensorReading(
            lux = lux,
            timestamp = formatter.format(Date()),
            status = level.title
        )
    }

    fun getInitialHistory(): List<SensorReading> = listOf(
        SensorReading(550, "12:15:30", "Оптимально для роботи"),
        SensorReading(420, "12:14:10", "Комфорт"),
        SensorReading(250, "12:12:00", "Тьмяно"),
        SensorReading(780, "12:10:05", "Занадто яскраво")
    )
}
