package ua.kpi.iot_lighting.data

data class LightingInput(
    val totalLamps: Int,
    val oldLampsCount: Int,
    val newLampsCount: Int,
    val oldPowerW: Double,
    val newPowerW: Double,
    val hoursPerDay: Double,
    val tariff: Double
)

data class LightingCalculationResult(
    val eOldDay: Double,
    val eNewDay: Double,
    val eCurrentDay: Double,
    val eOldMonth: Double,
    val eNewMonth: Double,
    val eCurrentMonth: Double,

    val costDay: Double,
    val costMonth: Double,
    val costYear: Double,
    val costOldMonth: Double,
    val costNewMonth: Double,

    val deltaP: Double,
    val savedKwhDay: Double,
    val savedKwhMonth: Double,
    val savingsDay: Double,
    val savingsMonth: Double,
    val savingsYear: Double
)

object LightingCalculator {
    const val DEFAULT_TARIFF = 4.32

    fun parseInput(
        totalLampsStr: String,
        oldLampsCountStr: String,
        newLampsCountStr: String,
        oldPowerWStr: String,
        newPowerWStr: String,
        hoursPerDayStr: String,
        tariffStr: String = DEFAULT_TARIFF.toString()
    ): LightingInput {
        val oldLamps = oldLampsCountStr.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
        val newLamps = newLampsCountStr.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
        val calculatedTotal = oldLamps + newLamps
        val parsedTotal = totalLampsStr.trim().toIntOrNull()?.coerceAtLeast(0) ?: calculatedTotal
        val total = if (parsedTotal >= calculatedTotal) parsedTotal else calculatedTotal

        val oldPower = oldPowerWStr.trim().replace(',', '.').toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
        val newPower = newPowerWStr.trim().replace(',', '.').toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
        val hours = hoursPerDayStr.trim().replace(',', '.').toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
        val tariff = tariffStr.trim().replace(',', '.').toDoubleOrNull()?.coerceAtLeast(0.0) ?: DEFAULT_TARIFF

        return LightingInput(
            totalLamps = total,
            oldLampsCount = oldLamps,
            newLampsCount = newLamps,
            oldPowerW = oldPower,
            newPowerW = newPower,
            hoursPerDay = hours,
            tariff = tariff
        )
    }

    fun calculate(input: LightingInput): LightingCalculationResult {
        val oldLamps = input.oldLampsCount.coerceAtLeast(0)
        val newLamps = input.newLampsCount.coerceAtLeast(0)
        val oldPower = input.oldPowerW.coerceAtLeast(0.0)
        val newPower = input.newPowerW.coerceAtLeast(0.0)
        val hours = input.hoursPerDay.coerceAtLeast(0.0)
        val tariff = input.tariff.coerceAtLeast(0.0)

        val eOldDay = (oldLamps * oldPower * hours) / 1000.0
        val eNewDay = (newLamps * newPower * hours) / 1000.0
        val eCurrentDay = eOldDay + eNewDay
        val eOldMonth = eOldDay * 30.0
        val eNewMonth = eNewDay * 30.0
        val eCurrentMonth = eCurrentDay * 30.0

        val costDay = eCurrentDay * tariff
        val costMonth = costDay * 30.0
        val costYear = costDay * 365.0
        val costOldMonth = eOldMonth * tariff
        val costNewMonth = eNewMonth * tariff

        val deltaP = if (oldPower > newPower) (oldPower - newPower) else 0.0
        val savedKwhDay = (oldLamps * deltaP * hours) / 1000.0
        val savedKwhMonth = savedKwhDay * 30.0
        val savingsDay = savedKwhDay * tariff
        val savingsMonth = savedKwhMonth * tariff
        val savingsYear = savedKwhDay * 365.0 * tariff

        return LightingCalculationResult(
            eOldDay = eOldDay,
            eNewDay = eNewDay,
            eCurrentDay = eCurrentDay,
            eOldMonth = eOldMonth,
            eNewMonth = eNewMonth,
            eCurrentMonth = eCurrentMonth,
            costDay = costDay,
            costMonth = costMonth,
            costYear = costYear,
            costOldMonth = costOldMonth,
            costNewMonth = costNewMonth,
            deltaP = deltaP,
            savedKwhDay = savedKwhDay,
            savedKwhMonth = savedKwhMonth,
            savingsDay = savingsDay,
            savingsMonth = savingsMonth,
            savingsYear = savingsYear
        )
    }

    fun calculate(
        totalLampsStr: String,
        oldLampsCountStr: String,
        newLampsCountStr: String,
        oldPowerWStr: String,
        newPowerWStr: String,
        hoursPerDayStr: String,
        tariffStr: String = DEFAULT_TARIFF.toString()
    ): LightingCalculationResult {
        val input = parseInput(
            totalLampsStr = totalLampsStr,
            oldLampsCountStr = oldLampsCountStr,
            newLampsCountStr = newLampsCountStr,
            oldPowerWStr = oldPowerWStr,
            newPowerWStr = newPowerWStr,
            hoursPerDayStr = hoursPerDayStr,
            tariffStr = tariffStr
        )
        return calculate(input)
    }

    fun calculateConsumption(
        bulbCount: String,
        powerPerBulb: String,
        hoursPerDay: String
    ): Double {
        val count = bulbCount.trim().toDoubleOrNull() ?: 0.0
        val power = powerPerBulb.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
        val hours = hoursPerDay.trim().replace(',', '.').toDoubleOrNull() ?: 0.0

        if (count <= 0.0 || power <= 0.0 || hours <= 0.0) {
            return 0.0
        }

        return (count * power * hours * 30.0) / 1000.0
    }
}
