package com.comp3074.sensorlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Plain JVM tests for the movement indicator logic. Run: gradlew test */
class GyroMotionTest {

    private val delta = 0.0001f

    @Test
    fun magnitude_ofStillPhone_isZero() {
        assertEquals(0f, GyroMotion.magnitude(0f, 0f, 0f), delta)
    }

    @Test
    fun magnitude_combinesAllAxes() {
        // 3-4-5 triangle extended to 3D: sqrt(1 + 4 + 4) = 3
        assertEquals(3f, GyroMotion.magnitude(1f, 2f, 2f), delta)
    }

    @Test
    fun magnitude_ignoresDirection() {
        assertEquals(
            GyroMotion.magnitude(0.3f, 0.4f, 0f),
            GyroMotion.magnitude(-0.3f, -0.4f, 0f),
            delta
        )
    }

    @Test
    fun isMoving_falseForSensorNoise() {
        assertFalse(GyroMotion.isMoving(0.01f, -0.02f, 0.005f))
    }

    @Test
    fun isMoving_trueForFastRotationOnOneAxis() {
        assertTrue(GyroMotion.isMoving(0f, 0f, -1.2f))
    }

    @Test
    fun isMoving_exactlyAtThreshold_isNotMoving() {
        // Boundary: the rule is "strictly above".
        assertFalse(GyroMotion.isMoving(0.5f, 0f, 0f))
        assertTrue(GyroMotion.isMoving(0.5001f, 0f, 0f))
    }

    @Test
    fun isMoving_smallValuesOnEveryAxisCanAddUp() {
        // 0.3 on each axis = 0.52 total, above 0.5 even though no single axis is.
        assertTrue(GyroMotion.isMoving(0.3f, 0.3f, 0.3f))
    }

    @Test
    fun isMoving_respectsCustomThreshold() {
        assertFalse(GyroMotion.isMoving(1f, 0f, 0f, threshold = 2f))
        assertTrue(GyroMotion.isMoving(1f, 0f, 0f, threshold = 0.1f))
    }
}
