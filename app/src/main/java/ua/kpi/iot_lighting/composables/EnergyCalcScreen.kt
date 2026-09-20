package ua.kpi.iot_lighting.composables

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.kpi.iot_lighting.data.LightingCalculationResult
import ua.kpi.iot_lighting.data.LightingCalculator
import ua.kpi.iot_lighting.ui.theme.AmberLightPrimary
import ua.kpi.iot_lighting.ui.theme.BorderSlate
import ua.kpi.iot_lighting.ui.theme.CanvasBase
import ua.kpi.iot_lighting.ui.theme.CyanSensor
import ua.kpi.iot_lighting.ui.theme.EmeraldEco
import ua.kpi.iot_lighting.ui.theme.SurfaceCard
import ua.kpi.iot_lighting.ui.theme.SurfaceCardSecondary
import ua.kpi.iot_lighting.ui.theme.TextMuted
import java.util.Locale

@Composable
fun EnergyCalcScreen(
    modifier: Modifier = Modifier
) {
    var oldLamps by remember { mutableStateOf("4") }
    var newLamps by remember { mutableStateOf("1") }
    var totalLamps by remember { mutableStateOf("5") }
    var oldPower by remember { mutableStateOf("75") }
    var newPower by remember { mutableStateOf("10") }
    var hours by remember { mutableStateOf("6.0") }
    var tariff by remember { mutableStateOf("4.32") }

    var result by remember {
        mutableStateOf<LightingCalculationResult?>(
            LightingCalculator.calculate(
                totalLampsStr = "5",
                oldLampsCountStr = "4",
                newLampsCountStr = "1",
                oldPowerWStr = "75",
                newPowerWStr = "10",
                hoursPerDayStr = "6.0",
                tariffStr = "4.32"
            )
        )
    }

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
                text = "Енергетичний калькулятор",
                style = MaterialTheme.typography.headlineSmall,
                color = AmberLightPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Модель енергоефективності та модернізації освітлення (ПЗ №2)",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        result?.let { calc ->
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Поточні витрати на електроенергію",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.1f", calc.costMonth),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = " грн/місяць",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmberLightPrimary,
                            modifier = Modifier.padding(bottom = 6.dp, start = 6.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "День",
                            value = String.format(Locale.US, "%.2f грн", calc.costDay),
                            valueColor = Color.White
                        )
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "Місяць",
                            value = String.format(Locale.US, "%.1f грн", calc.costMonth),
                            valueColor = Color.White
                        )
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "Рік",
                            value = String.format(Locale.US, "%.0f грн", calc.costYear),
                            valueColor = Color.White
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldEco.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Потенційна економія коштів (100% LED)",
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldEco,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "+%.1f", calc.savingsMonth),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = EmeraldEco
                        )
                        Text(
                            text = " грн/місяць",
                            style = MaterialTheme.typography.titleMedium,
                            color = EmeraldEco,
                            modifier = Modifier.padding(bottom = 6.dp, start = 6.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "День",
                            value = String.format(Locale.US, "+%.2f грн", calc.savingsDay),
                            valueColor = EmeraldEco
                        )
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "Місяць",
                            value = String.format(Locale.US, "+%.1f грн", calc.savingsMonth),
                            valueColor = EmeraldEco
                        )
                        CompactPeriodBox(
                            modifier = Modifier.weight(1f),
                            label = "Рік",
                            value = String.format(Locale.US, "+%.0f грн", calc.savingsYear),
                            valueColor = EmeraldEco
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Вхідні параметри системи освітлення",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = totalLamps,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Загальна кількість світильників (шт)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(isReadOnly = true)
                )

                OutlinedTextField(
                    value = oldLamps,
                    onValueChange = { newValue ->
                        oldLamps = newValue
                        val oldVal = newValue.trim().toIntOrNull() ?: 0
                        val newVal = newLamps.trim().toIntOrNull() ?: 0
                        totalLamps = (oldVal + newVal).toString()
                    },
                    label = { Text("Кількість старих ламп (шт)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = newLamps,
                    onValueChange = { newValue ->
                        newLamps = newValue
                        val oldVal = oldLamps.trim().toIntOrNull() ?: 0
                        val newVal = newValue.trim().toIntOrNull() ?: 0
                        totalLamps = (oldVal + newVal).toString()
                    },
                    label = { Text("Кількість нових LED ламп (шт)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = oldPower,
                    onValueChange = { oldPower = it },
                    label = { Text("Потужність старих ламп (Вт)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = newPower,
                    onValueChange = { newPower = it },
                    label = { Text("Потужність нових LED ламп (Вт)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Годин роботи на добу") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = tariff,
                    onValueChange = { tariff = it },
                    label = { Text("Тариф на електроенергію (грн/кВт·год)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        result = LightingCalculator.calculate(
                            totalLampsStr = totalLamps,
                            oldLampsCountStr = oldLamps,
                            newLampsCountStr = newLamps,
                            oldPowerWStr = oldPower,
                            newPowerWStr = newPower,
                            hoursPerDayStr = hours,
                            tariffStr = tariff
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberLightPrimary,
                        contentColor = CanvasBase
                    )
                ) {
                    Text(
                        text = "Розрахувати витрати та економію",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        result?.let { calc ->
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Детальний баланс споживання",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    ConsumptionDetailRow(
                        title = "Лампи старого типу",
                        detailText = String.format(
                            Locale.US,
                            "%.1f кВт·год/міс • %.1f грн/міс",
                            calc.eOldMonth,
                            calc.costOldMonth
                        ),
                        accentColor = Color(0xFFEF5350)
                    )

                    ConsumptionDetailRow(
                        title = "Лампи нового типу (LED)",
                        detailText = String.format(
                            Locale.US,
                            "%.1f кВт·год/міс • %.1f грн/міс",
                            calc.eNewMonth,
                            calc.costNewMonth
                        ),
                        accentColor = CyanSensor
                    )

                    ConsumptionDetailRow(
                        title = "Потенційна економія",
                        detailText = String.format(
                            Locale.US,
                            "+%.1f кВт·год/міс • +%.1f грн/міс заощадження",
                            calc.savedKwhMonth,
                            calc.savingsMonth
                        ),
                        accentColor = EmeraldEco
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CompactPeriodBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceCardSecondary, RoundedCornerShape(10.dp))
            .border(1.dp, BorderSlate, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun ConsumptionDetailRow(
    title: String,
    detailText: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCardSecondary, RoundedCornerShape(10.dp))
            .border(1.dp, BorderSlate.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = detailText,
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun fieldColors(isReadOnly: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (isReadOnly) BorderSlate else AmberLightPrimary,
    unfocusedBorderColor = BorderSlate,
    focusedLabelColor = if (isReadOnly) TextMuted else AmberLightPrimary,
    unfocusedLabelColor = TextMuted,
    cursorColor = AmberLightPrimary,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = if (isReadOnly) SurfaceCardSecondary.copy(alpha = 0.5f) else Color.Transparent,
    unfocusedContainerColor = if (isReadOnly) SurfaceCardSecondary.copy(alpha = 0.5f) else Color.Transparent
)
