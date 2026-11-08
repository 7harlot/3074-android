package com.comp3074.sensorlab

import kotlin.math.sqrt

/**
 * Part 18 extension: decides whether the phone is "moving" from one gyroscope reading.
 *
 * Kept as plain Kotlin (no Android classes) so it can be unit tested on the JVM.
 */
object GyroMotion {

    /**
     * Above this total rotation speed we call the phone "moving".
     * 0.5 rad/s is roughly 29 degrees per second: a deliberate twist, not hand jitter.
     * A phone lying on a desk reads around 0.00 to 0.02 rad/s.
     */
    const val MOVEMENT_THRESHOLD_RAD_PER_SEC = 0.5f

    /**
     * Total rotation speed, combining all three axes.
     * Like the length of an arrow (x, y, z): sqrt(x² + y² + z²).
     * Squaring also means a negative value (rotating the other way) still counts.
     */
    fun magnitude(x: Float, y: Float, z: Float): Float = sqrt(x * x + y * y + z * z)

    /** True when the rotation speed is strictly above [threshold]. */
    fun isMoving(
        x: Float,
        y: Float,
        z: Float,
        threshold: Float = MOVEMENT_THRESHOLD_RAD_PER_SEC
    ): Boolean = magnitude(x, y, z) > threshold
}
