package com.comp3074.sensorlab

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Plain JVM tests for the temperature sanity check. Run: gradlew test */
class TemperatureReadingTest {

    @Test
    fun normalRoomTemperature_isPlausible() {
        assertTrue(TemperatureReading.isPlausible(21.5f))
    }

    @Test
    fun coldAndHotWeather_arePlausible() {
        assertTrue(TemperatureReading.isPlausible(-40f))
        assertTrue(TemperatureReading.isPlausible(50f))
    }

    @Test
    fun emulatorPlaceholderValue_isRejected() {
        // The exact value the Medium Phone API 36.1 emulator sent.
        assertFalse(TemperatureReading.isPlausible(-1.0000000150474662e30f))
    }

    @Test
    fun boundaries_areInclusive() {
        assertTrue(TemperatureReading.isPlausible(TemperatureReading.MIN_PLAUSIBLE_CELSIUS))
        assertTrue(TemperatureReading.isPlausible(TemperatureReading.MAX_PLAUSIBLE_CELSIUS))
        assertFalse(TemperatureReading.isPlausible(-273.2f))
        assertFalse(TemperatureReading.isPlausible(150.1f))
    }

    @Test
    fun nanAndInfinity_areRejected() {
        assertFalse(TemperatureReading.isPlausible(Float.NaN))
        assertFalse(TemperatureReading.isPlausible(Float.POSITIVE_INFINITY))
        assertFalse(TemperatureReading.isPlausible(Float.NEGATIVE_INFINITY))
    }
}
