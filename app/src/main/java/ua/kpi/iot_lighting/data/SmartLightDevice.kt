package ua.kpi.iot_lighting.data

import java.util.UUID

data class QuickLightMode(
    val name: String,
    val brightnessPercent: Int,
    val colorTempK: Int
)

data class SmartLightDevice(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val roomName: String,
    val isPoweredOn: Boolean = true,
    val brightnessPercent: Int = 80,
    val colorTempK: Int = 4000,
    val activePreset: String? = "Робота",
    val mapX: Float = 0.5f,
    val mapY: Float = 0.5f
)

object SmartLightRepository {
    val defaultPresets: List<QuickLightMode> = listOf(
        QuickLightMode(
            name = "Робота",
            brightnessPercent = 100,
            colorTempK = 5000
        ),
        QuickLightMode(
            name = "Читання",
            brightnessPercent = 70,
            colorTempK = 4000
        ),
        QuickLightMode(
            name = "Відпочинок",
            brightnessPercent = 45,
            colorTempK = 3000
        ),
        QuickLightMode(
            name = "Нічник",
            brightnessPercent = 15,
            colorTempK = 2200
        ),
        QuickLightMode(
            name = "Еко",
            brightnessPercent = 50,
            colorTempK = 3500
        )
    )

    fun getInitialDevices(): List<SmartLightDevice> = listOf(
        SmartLightDevice(
            id = "d1",
            name = "Люстра основна",
            roomName = "Вітальня",
            isPoweredOn = true,
            brightnessPercent = 80,
            colorTempK = 4000,
            activePreset = "Читання",
            mapX = 0.35f,
            mapY = 0.35f
        ),
        SmartLightDevice(
            id = "d2",
            name = "Торшер робочий",
            roomName = "Кабінет",
            isPoweredOn = true,
            brightnessPercent = 100,
            colorTempK = 5000,
            activePreset = "Робота",
            mapX = 0.75f,
            mapY = 0.30f
        ),
        SmartLightDevice(
            id = "d3",
            name = "Бра приліжкове",
            roomName = "Спальня",
            isPoweredOn = false,
            brightnessPercent = 25,
            colorTempK = 2700,
            activePreset = "Відпочинок",
            mapX = 0.25f,
            mapY = 0.75f
        ),
        SmartLightDevice(
            id = "d4",
            name = "Підсвітка кухні",
            roomName = "Кухня",
            isPoweredOn = true,
            brightnessPercent = 60,
            colorTempK = 4000,
            activePreset = "Еко",
            mapX = 0.75f,
            mapY = 0.75f
        )
    )
}
