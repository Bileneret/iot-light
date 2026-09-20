package ua.kpi.iot_lighting

import org.junit.Assert.assertEquals
import org.junit.Test
import ua.kpi.iot_lighting.data.LightComfortLevel
import ua.kpi.iot_lighting.data.LightingCalculator
import ua.kpi.iot_lighting.data.LightingInput

class EnergyCalculatorTest {

    @Test
    fun testCurrentConsumptionAndCosts() {
        val input = LightingInput(
            totalLamps = 5,
            oldLampsCount = 4,
            newLampsCount = 1,
            oldPowerW = 75.0,
            newPowerW = 10.0,
            hoursPerDay = 6.0,
            tariff = 4.32
        )
        val result = LightingCalculator.calculate(input)

        // Поточне споживання:
        // E_old_day = (4 * 75 * 6) / 1000.0 = 1.8 кВт·год
        assertEquals(1.8, result.eOldDay, 0.0001)
        // E_new_day = (1 * 10 * 6) / 1000.0 = 0.06 кВт·год
        assertEquals(0.06, result.eNewDay, 0.0001)
        // E_current_day = 1.8 + 0.06 = 1.86 кВт·год
        assertEquals(1.86, result.eCurrentDay, 0.0001)
        // E_old_month = 1.8 * 30 = 54.0 кВт·год
        assertEquals(54.0, result.eOldMonth, 0.0001)
        // E_new_month = 0.06 * 30 = 1.8 кВт·год
        assertEquals(1.8, result.eNewMonth, 0.0001)
        // E_current_month = 1.86 * 30 = 55.8 кВт·год
        assertEquals(55.8, result.eCurrentMonth, 0.0001)

        // Поточні витрати:
        // Cost_day = 1.86 * 4.32 = 8.0352 грн
        assertEquals(8.0352, result.costDay, 0.0001)
        // Cost_month = 8.0352 * 30 = 241.056 грн
        assertEquals(241.056, result.costMonth, 0.0001)
        // Cost_year = 8.0352 * 365 = 2932.848 грн
        assertEquals(2932.848, result.costYear, 0.0001)
        // Cost_old_month = 54.0 * 4.32 = 233.28 грн
        assertEquals(233.28, result.costOldMonth, 0.0001)
        // Cost_new_month = 1.8 * 4.32 = 7.776 грн
        assertEquals(7.776, result.costNewMonth, 0.0001)
    }

    @Test
    fun testPotentialSavingsCalculation() {
        val input = LightingInput(
            totalLamps = 5,
            oldLampsCount = 4,
            newLampsCount = 1,
            oldPowerW = 75.0,
            newPowerW = 10.0,
            hoursPerDay = 6.0,
            tariff = 4.32
        )
        val result = LightingCalculator.calculate(input)

        // Delta_P = 75.0 - 10.0 = 65.0 Вт
        assertEquals(65.0, result.deltaP, 0.0001)
        // Saved_kWh_day = (4 * 65.0 * 6.0) / 1000.0 = 1.56 кВт·год
        assertEquals(1.56, result.savedKwhDay, 0.0001)
        // Saved_kWh_month = 1.56 * 30.0 = 46.8 кВт·год
        assertEquals(46.8, result.savedKwhMonth, 0.0001)
        // Savings_day = 1.56 * 4.32 = 6.7392 грн
        assertEquals(6.7392, result.savingsDay, 0.0001)
        // Savings_month = 46.8 * 4.32 = 202.176 грн
        assertEquals(202.176, result.savingsMonth, 0.0001)
        // Savings_year = 1.56 * 365.0 * 4.32 = 2459.808 грн
        assertEquals(2459.808, result.savingsYear, 0.0001)
    }

    @Test
    fun testStringParsingWithCommasAndSpaces() {
        val result = LightingCalculator.calculate(
            totalLampsStr = " 5 ",
            oldLampsCountStr = " 4 ",
            newLampsCountStr = " 1 ",
            oldPowerWStr = " 75,0 ",
            newPowerWStr = " 10,0 ",
            hoursPerDayStr = " 6,0 ",
            tariffStr = " 4,32 "
        )

        assertEquals(1.86, result.eCurrentDay, 0.0001)
        assertEquals(241.056, result.costMonth, 0.0001)
        assertEquals(202.176, result.savingsMonth, 0.0001)
    }

    @Test
    fun testNegativeAndZeroProtection() {
        val result = LightingCalculator.calculate(
            totalLampsStr = "-5",
            oldLampsCountStr = "-4",
            newLampsCountStr = "-1",
            oldPowerWStr = "-75",
            newPowerWStr = "-10",
            hoursPerDayStr = "-6",
            tariffStr = "-4.32"
        )

        assertEquals(0.0, result.eCurrentDay, 0.0001)
        assertEquals(0.0, result.costDay, 0.0001)
        assertEquals(0.0, result.costMonth, 0.0001)
        assertEquals(0.0, result.savingsMonth, 0.0001)
    }

    @Test
    fun testLegacyCalculateConsumption() {
        val consumption = LightingCalculator.calculateConsumption(
            bulbCount = "5",
            powerPerBulb = "10",
            hoursPerDay = "6"
        )
        // (5 * 10 * 6 * 30) / 1000.0 = 9.0 kWh
        assertEquals(9.0, consumption, 0.001)

        val invalid = LightingCalculator.calculateConsumption("abc", "10", "6")
        assertEquals(0.0, invalid, 0.001)
    }

    @Test
    fun testLightComfortLevelClassificationDSTY() {
        // Темно < 100, Тьмяно 100-300, Комфорт 300-500, Оптимально для роботи 500-750, Занадто яскраво > 750
        assertEquals(LightComfortLevel.TOO_DARK, LightComfortLevel.fromLux(50f))
        assertEquals(LightComfortLevel.DIM, LightComfortLevel.fromLux(200f))
        assertEquals(LightComfortLevel.COMFORT, LightComfortLevel.fromLux(400f))
        assertEquals(LightComfortLevel.OPTIMAL_WORK, LightComfortLevel.fromLux(600f))
        assertEquals(LightComfortLevel.TOO_BRIGHT, LightComfortLevel.fromLux(850f))
    }
}
