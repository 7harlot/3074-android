package com.comp3074.sensorlab

/**
 * Sanity check for ambient temperature readings.
 *
 * Some devices and emulator images report a temperature sensor but send a placeholder
 * such as -1.0E30 instead of a real value. Showing that as "°C" would be wrong, so any
 * reading outside a physically sensible range is treated as "no valid reading".
 *
 * Plain Kotlin (no Android classes) so it can be unit tested on the JVM.
 */
object TemperatureReading {

    /** Absolute zero. Nothing can be colder, so anything below this is junk. */
    const val MIN_PLAUSIBLE_CELSIUS = -273.15f

    /**
     * Generous upper limit. Ambient air on Earth never gets close to 100 °C;
     * 150 leaves room for odd hardware without letting placeholder values through.
     */
    const val MAX_PLAUSIBLE_CELSIUS = 150f

    /** True for a real-looking value. Rejects NaN, infinity and out-of-range numbers. */
    fun isPlausible(celsius: Float): Boolean =
        celsius.isFinite() && celsius in MIN_PLAUSIBLE_CELSIUS..MAX_PLAUSIBLE_CELSIUS
}
