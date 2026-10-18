package com.comp3074.assignment1

import org.junit.Assert.assertEquals
import org.junit.Test

/** Checks the pay math against hand-worked examples. Run with: ./gradlew test */
class PayCalculatorTest {

    private val delta = 0.0001 // allowed rounding difference for Double comparisons

    @Test
    fun underFortyHours_noOvertime() {
        val r = PayCalculator.calculate(hoursWorked = 30.0, hourlyRate = 20.0, taxRatePercent = 13.0)
        assertEquals(600.0, r.pay, delta)
        assertEquals(0.0, r.overtimePay, delta)
        assertEquals(600.0, r.totalPay, delta)
        assertEquals(78.0, r.tax, delta)
    }

    @Test
    fun exactlyFortyHours_noOvertime() {
        val r = PayCalculator.calculate(40.0, 20.0, 13.0)
        assertEquals(800.0, r.pay, delta)
        assertEquals(0.0, r.overtimePay, delta)
        assertEquals(800.0, r.totalPay, delta)
        assertEquals(104.0, r.tax, delta)
    }

    @Test
    fun overFortyHours_overtimeAtTimeAndAHalf() {
        val r = PayCalculator.calculate(45.0, 20.0, 13.0)
        assertEquals(800.0, r.pay, delta)        // 40 * 20
        assertEquals(150.0, r.overtimePay, delta) // 5 * 20 * 1.5
        assertEquals(950.0, r.totalPay, delta)
        assertEquals(104.0, r.tax, delta)         // spec: tax on base pay only, not 950
    }

    @Test
    fun fractionalHours() {
        val r = PayCalculator.calculate(40.5, 18.0, 10.0)
        assertEquals(720.0, r.pay, delta)
        assertEquals(13.5, r.overtimePay, delta)  // 0.5 * 18 * 1.5
        assertEquals(733.5, r.totalPay, delta)
        assertEquals(72.0, r.tax, delta)
    }

    @Test
    fun zeroInputs_allZero() {
        val r = PayCalculator.calculate(0.0, 0.0, 0.0)
        assertEquals(0.0, r.totalPay, delta)
        assertEquals(0.0, r.tax, delta)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeHours_rejected() {
        PayCalculator.calculate(-1.0, 20.0, 13.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun taxOver100_rejected() {
        PayCalculator.calculate(40.0, 20.0, 150.0)
    }
}
