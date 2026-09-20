package ua.kpi.iot_lighting.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.iot_lighting.data.QuickLightMode
import ua.kpi.iot_lighting.data.SmartLightDevice
import ua.kpi.iot_lighting.data.SmartLightRepository
import ua.kpi.iot_lighting.ui.theme.AmberLightPrimary
import ua.kpi.iot_lighting.ui.theme.BorderSlate
import ua.kpi.iot_lighting.ui.theme.CanvasBase
import ua.kpi.iot_lighting.ui.theme.CoralAlert
import ua.kpi.iot_lighting.ui.theme.CyanSensor
import ua.kpi.iot_lighting.ui.theme.EmeraldEco
import ua.kpi.iot_lighting.ui.theme.SurfaceCard
import ua.kpi.iot_lighting.ui.theme.SurfaceCardSecondary
import ua.kpi.iot_lighting.ui.theme.TextMuted
import kotlin.math.roundToInt

private fun kelvinToColor(kelvin: Int): Color {
    return when {
        kelvin >= 5000 -> Color(0xFF90CAF9)
        kelvin >= 3800 -> Color(0xFFFFF9C4)
        kelvin >= 2800 -> Color(0xFFFFD54F)
        else -> Color(0xFFFFB74D)
    }
}

@Composable
fun LightControlScreen(
    modifier: Modifier = Modifier
) {
    val devices = remember {
        mutableStateListOf<SmartLightDevice>().apply {
            addAll(SmartLightRepository.getInitialDevices())
        }
    }

    var selectedId by remember {
        mutableStateOf(devices.firstOrNull()?.id ?: "")
    }

    val selected = devices.firstOrNull { it.id == selectedId } ?: devices.firstOrNull()

    var showAddDialog by remember { mutableStateOf(false) }
    var deviceToEdit by remember { mutableStateOf<SmartLightDevice?>(null) }

    fun updateDevice(id: String, transform: (SmartLightDevice) -> SmartLightDevice) {
        val index = devices.indexOfFirst { it.id == id }
        if (index != -1) {
            devices[index] = transform(devices[index])
        }
    }

    val isPoweredOn = selected?.isPoweredOn == true
    val brightnessFloat = ((selected?.brightnessPercent ?: 80) / 100f).coerceIn(0.05f, 1f)
    val colorTempK = selected?.colorTempK ?: 4000

    val glowColor = if (isPoweredOn) kelvinToColor(colorTempK) else Color(0xFF374151)

    val animatedGlowColor by animateColorAsState(
        targetValue = glowColor,
        animationSpec = tween(500),
        label = "LampGlowColor"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPoweredOn) brightnessFloat.coerceIn(0.2f, 1f) else 0.1f,
        animationSpec = tween(500),
        label = "LampBrightnessAlpha"
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
                text = "Керування освітленням",
                style = MaterialTheme.typography.titleLarge,
                color = AmberLightPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Налаштування спектру, IoT світильників та 2D-карти приміщень",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        if (selected != null) {
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selected.name} (${selected.roomName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { deviceToEdit = selected },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Редагувати світильник",
                                        tint = AmberLightPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isPoweredOn) "СТАТУС: УВІМКНЕНО (${selected.brightnessPercent}%)" else "СТАТУС: ВИМКНЕНО",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPoweredOn) EmeraldEco else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = isPoweredOn,
                            onCheckedChange = { checked ->
                                updateDevice(selected.id) { it.copy(isPoweredOn = checked) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CanvasBase,
                                checkedTrackColor = AmberLightPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SurfaceCardSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            animatedGlowColor.copy(alpha = animatedAlpha * 0.85f),
                                            animatedGlowColor.copy(alpha = animatedAlpha * 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPoweredOn) animatedGlowColor.copy(alpha = animatedAlpha)
                                    else SurfaceCardSecondary
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isPoweredOn) animatedGlowColor else BorderSlate,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isPoweredOn) "💡" else "⭕",
                                    fontSize = 44.sp
                                )
                                Text(
                                    text = if (isPoweredOn) "${selected.brightnessPercent}%" else "OFF",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPoweredOn) Color.Black else TextMuted
                                )
                            }
                        }
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Плавне регулювання спектру",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Яскравість світла", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            Text(
                                text = "${selected.brightnessPercent}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberLightPrimary
                            )
                        }
                        Slider(
                            value = selected.brightnessPercent.toFloat(),
                            onValueChange = { newPercent ->
                                updateDevice(selected.id) {
                                    it.copy(
                                        brightnessPercent = newPercent.roundToInt().coerceIn(5, 100),
                                        isPoweredOn = true,
                                        activePreset = "Користувацький"
                                    )
                                }
                            },
                            valueRange = 5f..100f,
                            enabled = isPoweredOn,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberLightPrimary,
                                activeTrackColor = AmberLightPrimary,
                                inactiveTrackColor = SurfaceCardSecondary
                            )
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Колірна температура", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            Text(
                                text = "${selected.colorTempK} K",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = animatedGlowColor
                            )
                        }
                        Slider(
                            value = selected.colorTempK.toFloat(),
                            onValueChange = { newTemp ->
                                updateDevice(selected.id) {
                                    it.copy(
                                        colorTempK = newTemp.roundToInt().coerceIn(2200, 6500),
                                        isPoweredOn = true,
                                        activePreset = "Користувацький"
                                    )
                                }
                            },
                            valueRange = 2200f..6500f,
                            enabled = isPoweredOn,
                            colors = SliderDefaults.colors(
                                thumbColor = animatedGlowColor,
                                activeTrackColor = animatedGlowColor,
                                inactiveTrackColor = SurfaceCardSecondary
                            )
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Швидкі режими освітлення",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SmartLightRepository.defaultPresets.take(3).forEach { preset ->
                            PresetChipButton(
                                preset = preset,
                                isSelected = isPoweredOn && selected.activePreset == preset.name,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    updateDevice(selected.id) {
                                        it.copy(
                                            isPoweredOn = true,
                                            activePreset = preset.name,
                                            brightnessPercent = preset.brightnessPercent,
                                            colorTempK = preset.colorTempK
                                        )
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SmartLightRepository.defaultPresets.drop(3).forEach { preset ->
                            PresetChipButton(
                                preset = preset,
                                isSelected = isPoweredOn && selected.activePreset == preset.name,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    updateDevice(selected.id) {
                                        it.copy(
                                            isPoweredOn = true,
                                            activePreset = preset.name,
                                            brightnessPercent = preset.brightnessPercent,
                                            colorTempK = preset.colorTempK
                                        )
                                    }
                                }
                            )
                        }
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
                        text = "Статус поточного світильника",
                        style = MaterialTheme.typography.titleMedium,
                        color = AmberLightPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    StatusItemRow(label = "Назва:", value = selected.name)
                    StatusItemRow(label = "Кімната:", value = selected.roomName)
                    StatusItemRow(label = "Стан:", value = if (isPoweredOn) "УВІМКНЕНО" else "ВИМКНЕНО")
                    StatusItemRow(label = "Рівень яскравості:", value = if (isPoweredOn) "${selected.brightnessPercent}%" else "0%")
                    StatusItemRow(label = "Колірна температура:", value = if (isPoweredOn) "${selected.colorTempK} K" else "—")
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Світильники відсутні",
                        style = MaterialTheme.typography.titleMedium,
                        color = AmberLightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Додайте світильник на карту, щоб почати керування освітленням.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberLightPrimary, contentColor = CanvasBase)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Додати світильник", fontWeight = FontWeight.Bold)
                    }
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Інтерактивна карта приміщень",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${devices.size} IoT точок (натисніть для вибору, перетягуйте по карті)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanSensor
                        )
                    }

                    IconButton(
                        onClick = { showAddDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Додати світильник",
                            tint = AmberLightPrimary
                        )
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CanvasBase)
                        .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                ) {
                    val mapWidth = maxWidth
                    val mapHeight = maxHeight
                    val density = LocalDensity.current

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(CanvasBase, SurfaceCardSecondary.copy(alpha = 0.35f))
                                )
                            )
                    )

                    devices.forEach { device ->
                        val isSelected = device.id == selectedId
                        val haloColor = if (device.isPoweredOn) {
                            kelvinToColor(device.colorTempK)
                        } else {
                            Color(0xFF30363D)
                        }

                        val markerSize = 44.dp
                        val halfMarker = markerSize / 2

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (mapWidth * device.mapX) - halfMarker,
                                    y = (mapHeight * device.mapY) - halfMarker
                                )
                                .size(markerSize)
                                .clip(CircleShape)
                                .background(
                                    if (device.isPoweredOn) {
                                        haloColor.copy(alpha = (device.brightnessPercent / 100f).coerceIn(0.25f, 0.75f))
                                    } else {
                                        Color(0xFF30363D).copy(alpha = 0.5f)
                                    }
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isSelected) AmberLightPrimary else if (device.isPoweredOn) haloColor else Color(0xFF30363D),
                                    shape = CircleShape
                                )
                                .pointerInput(device.id) {
                                    detectTapGestures(
                                        onTap = {
                                            selectedId = device.id
                                        }
                                    )
                                }
                                .pointerInput(device.id) {
                                    detectDragGestures(
                                        onDragStart = {
                                            selectedId = device.id
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val widthPx = with(density) { mapWidth.toPx() }
                                            val heightPx = with(density) { mapHeight.toPx() }
                                            if (widthPx > 0f && heightPx > 0f) {
                                                updateDevice(device.id) { current ->
                                                    val newX = (current.mapX + dragAmount.x / widthPx).coerceIn(0.08f, 0.92f)
                                                    val newY = (current.mapY + dragAmount.y / heightPx).coerceIn(0.08f, 0.92f)
                                                    current.copy(mapX = newX, mapY = newY)
                                                }
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (device.isPoweredOn) haloColor else Color(0xFF30363D))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                devices.forEach { device ->
                    val isSelected = device.id == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SurfaceCardSecondary else CanvasBase)
                            .border(1.dp, if (isSelected) AmberLightPrimary else BorderSlate, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedId = device.id
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${device.roomName} • ${device.name}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) AmberLightPrimary else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (device.isPoweredOn) "Активно (${device.brightnessPercent}% • ${device.colorTempK}K)" else "Вимкнено",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { deviceToEdit = device },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Редагувати світильник",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Switch(
                                checked = device.isPoweredOn,
                                onCheckedChange = { checked ->
                                    updateDevice(device.id) { it.copy(isPoweredOn = checked) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CanvasBase,
                                    checkedTrackColor = AmberLightPrimary,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = SurfaceCard
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newRoomName by remember { mutableStateOf("") }
        var hasError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Додати новий світильник",
                    color = AmberLightPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = {
                            newName = it
                            hasError = false
                        },
                        label = { Text("Назва світильника", color = TextMuted) },
                        singleLine = true,
                        isError = hasError && newName.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberLightPrimary,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newRoomName,
                        onValueChange = {
                            newRoomName = it
                            hasError = false
                        },
                        label = { Text("Приміщення", color = TextMuted) },
                        singleLine = true,
                        isError = hasError && newRoomName.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberLightPrimary,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (hasError) {
                        Text(
                            text = "Будь ласка, вкажіть назву світильника та приміщення",
                            color = CoralAlert,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isBlank() || newRoomName.isBlank()) {
                            hasError = true
                        } else {
                            val newDevice = SmartLightDevice(
                                name = newName.trim(),
                                roomName = newRoomName.trim(),
                                isPoweredOn = true,
                                brightnessPercent = 80,
                                colorTempK = 4000,
                                activePreset = "Робота",
                                mapX = 0.5f,
                                mapY = 0.5f
                            )
                            devices.add(newDevice)
                            selectedId = newDevice.id
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberLightPrimary, contentColor = CanvasBase)
                ) {
                    Text("Додати", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Text("Скасувати")
                }
            }
        )
    }

    deviceToEdit?.let { target ->
        var editName by remember(target.id) { mutableStateOf(target.name) }
        var editRoom by remember(target.id) { mutableStateOf(target.roomName) }
        var hasError by remember(target.id) { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { deviceToEdit = null },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Редагування світильника",
                    color = AmberLightPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            hasError = false
                        },
                        label = { Text("Назва світильника", color = TextMuted) },
                        singleLine = true,
                        isError = hasError && editName.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberLightPrimary,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editRoom,
                        onValueChange = {
                            editRoom = it
                            hasError = false
                        },
                        label = { Text("Приміщення", color = TextMuted) },
                        singleLine = true,
                        isError = hasError && editRoom.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberLightPrimary,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (hasError) {
                        Text(
                            text = "Будь ласка, вкажіть назву світильника та приміщення",
                            color = CoralAlert,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val targetId = target.id
                            devices.removeAll { it.id == targetId }
                            if (selectedId == targetId) {
                                selectedId = devices.firstOrNull()?.id ?: ""
                            }
                            deviceToEdit = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralAlert,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Видалити світильник", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank() || editRoom.isBlank()) {
                            hasError = true
                        } else {
                            updateDevice(target.id) {
                                it.copy(
                                    name = editName.trim(),
                                    roomName = editRoom.trim()
                                )
                            }
                            deviceToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberLightPrimary, contentColor = CanvasBase)
                ) {
                    Text("Зберегти", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deviceToEdit = null },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Text("Скасувати")
                }
            }
        )
    }
}

@Composable
private fun PresetChipButton(
    preset: QuickLightMode,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(42.dp),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberLightPrimary,
                contentColor = CanvasBase
            )
        ) {
            Text(
                text = preset.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(42.dp),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberLightPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
        ) {
            Text(
                text = preset.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatusItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
