package ua.kpi.iot_lighting.composables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.iot_lighting.data.LightComfortLevel
import ua.kpi.iot_lighting.data.SensorDataProvider
import ua.kpi.iot_lighting.data.SensorReading
import ua.kpi.iot_lighting.ui.theme.AmberLightPrimary
import ua.kpi.iot_lighting.ui.theme.BorderSlate
import ua.kpi.iot_lighting.ui.theme.CanvasBase
import ua.kpi.iot_lighting.ui.theme.CoralAlert
import ua.kpi.iot_lighting.ui.theme.CyanSensor
import ua.kpi.iot_lighting.ui.theme.EmeraldEco
import ua.kpi.iot_lighting.ui.theme.SurfaceCard
import ua.kpi.iot_lighting.ui.theme.SurfaceCardSecondary
import ua.kpi.iot_lighting.ui.theme.TextMuted
import kotlin.random.Random

@Composable
fun SensorMonitorScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val lightSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    var isHardwareSensor by remember { mutableStateOf(lightSensor != null) }
    var targetLux by remember { mutableIntStateOf(550) }

    DisposableEffect(isHardwareSensor) {
        if (isHardwareSensor && lightSensor != null) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.values?.firstOrNull()?.let { luxFloat ->
                        targetLux = luxFloat.toInt()
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(
                listener,
                lightSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        } else {
            onDispose { }
        }
    }

    val animatedLux by animateIntAsState(
        targetValue = targetLux,
        animationSpec = tween(durationMillis = 400),
        label = "LuxAnimation"
    )

    var history by remember { mutableStateOf(SensorDataProvider.getInitialHistory()) }
    val comfortLevel = LightComfortLevel.fromLux(animatedLux.toFloat())

    val statusColor by animateColorAsState(
        targetValue = when (comfortLevel) {
            LightComfortLevel.TOO_DARK -> CoralAlert
            LightComfortLevel.DIM -> AmberLightPrimary
            LightComfortLevel.COMFORT -> CyanSensor
            LightComfortLevel.OPTIMAL_WORK -> EmeraldEco
            LightComfortLevel.TOO_BRIGHT -> CoralAlert
        },
        label = "ComfortColor"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBase)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Освітленість приміщення",
                style = MaterialTheme.typography.titleLarge,
                color = AmberLightPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${lightSensor?.name ?: "Датчик недоступний"} (${lightSensor?.vendor ?: "N/A"})",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Text(
                text = "Діапазон: до ${lightSensor?.maximumRange?.toInt() ?: 0} Lux • Точність: ${lightSensor?.resolution ?: 0f} Lux",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHardwareSensor) "Фізичний сенсор" else "Демо / Симуляція",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isHardwareSensor) CyanSensor else AmberLightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHardwareSensor) "Зчитування в реальному часі" else "Ручний вибір пресетів та генератор",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isHardwareSensor,
                    onCheckedChange = { isChecked ->
                        if (lightSensor != null) {
                            isHardwareSensor = isChecked
                        }
                    },
                    enabled = lightSensor != null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CyanSensor,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceCardSecondary,
                        disabledUncheckedThumbColor = TextMuted.copy(alpha = 0.4f),
                        disabledUncheckedTrackColor = SurfaceCardSecondary.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Поточний рівень освітленості",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$animatedLux",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = AmberLightPrimary
                    )
                    Text(
                        text = " Lux",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp, start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (animatedLux / 1000f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
                    trackColor = SurfaceCardSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = comfortLevel.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${comfortLevel.description} ${comfortLevel.recommendation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Швидкі пресети симуляції",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isHardwareSensor) TextMuted else Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                if (isHardwareSensor) {
                    Text(
                        text = "Вимкніть фізичний датчик для ручної симуляції",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberLightPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Ніч" to 20,
                        "Кімната" to 250,
                        "Робота" to 550,
                        "Сонце" to 950
                    ).forEach { (label, luxVal) ->
                        val isCurrent = targetLux == luxVal
                        OutlinedButton(
                            onClick = {
                                targetLux = luxVal
                                history = listOf(SensorDataProvider.createReading(luxVal)) + history.take(5)
                            },
                            enabled = !isHardwareSensor,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isCurrent) AmberLightPrimary else TextMuted,
                                disabledContentColor = TextMuted.copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    isHardwareSensor -> BorderSlate.copy(alpha = 0.5f)
                                    isCurrent -> AmberLightPrimary
                                    else -> BorderSlate
                                }
                            )
                        ) {
                            Text(text = label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val randomLux = Random.nextInt(50, 1001)
                        targetLux = randomLux
                        history = listOf(SensorDataProvider.createReading(randomLux)) + history.take(5)
                    },
                    enabled = !isHardwareSensor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberLightPrimary,
                        contentColor = CanvasBase,
                        disabledContainerColor = SurfaceCardSecondary,
                        disabledContentColor = TextMuted.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = "Опитати IoT-датчик (50–1000 Lux)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Історія останніх замірів",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                history.forEach { reading ->
                    SensorHistoryCardRow(reading = reading)
                }
            }
        }
    }
}

@Composable
private fun SensorHistoryCardRow(reading: SensorReading) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardSecondary)
            .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${reading.lux} Lux",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AmberLightPrimary
            )
            Text(
                text = reading.status,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
        Text(
            text = reading.timestamp,
            style = MaterialTheme.typography.bodyMedium,
            color = CyanSensor
        )
    }
}
