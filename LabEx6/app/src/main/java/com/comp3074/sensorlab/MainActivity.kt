package com.comp3074.sensorlab

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Single screen that shows live ambient temperature and gyroscope readings.
 *
 * The activity itself is the SensorEventListener: SensorManager calls
 * onSensorChanged() on it every time a registered sensor has a new value.
 */
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    // Nullable because getDefaultSensor() returns null when the phone doesn't have that sensor.
    private var temperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    // Compose-observable state: writing to these from onSensorChanged() makes Compose
    // redraw only the parts of the UI that read them.
    private var temperature by mutableStateOf<Float?>(null) // null = no reading yet
    // True when the sensor exists but sends nonsense (e.g. the emulator's -1.0E30 placeholder).
    private var temperatureInvalid by mutableStateOf(false)
    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)

    private var temperatureAvailable by mutableStateOf(false)
    private var gyroscopeAvailable by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Ask for each sensor once. A missing sensor comes back as null, never a crash.
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        temperatureAvailable = temperatureSensor != null
        gyroscopeAvailable = gyroscopeSensor != null

        setContent {
            MaterialTheme {
                SensorScreen(
                    temperature = temperature,
                    temperatureAvailable = temperatureAvailable,
                    temperatureInvalid = temperatureInvalid,
                    gyroX = gyroX,
                    gyroY = gyroY,
                    gyroZ = gyroZ,
                    gyroscopeAvailable = gyroscopeAvailable
                )
            }
        }
    }

    /** Screen is in the foreground: start listening. ?.let skips a missing sensor. */
    override fun onResume() {
        super.onResume()

        temperatureSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /** Screen is leaving the foreground: stop all sensors so we don't drain the battery. */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // one call removes this listener from every sensor
    }

    /** Called by SensorManager with each new reading. Both sensors share this method. */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // Check which sensor sent the event, because values[] means different things per sensor.
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                val celsius = event.values[0] // air temperature in °C
                // Only show values that make physical sense; otherwise flag the reading as invalid.
                if (TemperatureReading.isPlausible(celsius)) {
                    temperature = celsius
                    temperatureInvalid = false
                } else {
                    temperature = null
                    temperatureInvalid = true
                }
            }

            Sensor.TYPE_GYROSCOPE -> {
                // Angular velocity (rotation speed) around each axis, in rad/s.
                gyroX = event.values[0]
                gyroY = event.values[1]
                gyroZ = event.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not required for this lab.
    }
}

/**
 * The whole screen. It only receives plain values, so it knows nothing about sensors.
 * That keeps it easy to read and lets the @Preview below draw it without a phone.
 */
@Composable
fun SensorScreen(
    temperature: Float?,
    temperatureAvailable: Boolean,
    temperatureInvalid: Boolean,
    gyroX: Float,
    gyroY: Float,
    gyroZ: Float,
    gyroscopeAvailable: Boolean
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Android Sensor Monitor",
                style = MaterialTheme.typography.headlineMedium
            )

            TemperatureCard(
                temperature = temperature,
                available = temperatureAvailable,
                invalid = temperatureInvalid
            )

            GyroscopeCard(
                x = gyroX,
                y = gyroY,
                z = gyroZ,
                available = gyroscopeAvailable
            )
        }
    }
}

/** Shows °C, "not available", an invalid-reading message, or "waiting" before the first event. */
@Composable
fun TemperatureCard(
    temperature: Float?,
    available: Boolean,
    invalid: Boolean = false
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ambient Temperature",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                !available -> Text("Temperature sensor is not available.")
                invalid -> Text("Temperature sensor returned no valid reading (treated as unavailable).")
                temperature == null -> Text("Waiting for sensor reading...")
                else -> Text(
                    text = "%.1f °C".format(temperature),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

/** Shows X, Y, Z rotation speed plus the Part 18 movement indicator. */
@Composable
fun GyroscopeCard(
    x: Float,
    y: Float,
    z: Float,
    available: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Gyroscope",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!available) {
                Text("Gyroscope is not available.")
            } else {
                Text("X: %.3f rad/s".format(x))
                Text("Y: %.3f rad/s".format(y))
                Text("Z: %.3f rad/s".format(z))

                Spacer(modifier = Modifier.height(8.dp))
                MovementIndicator(isMoving = GyroMotion.isMoving(x, y, z))
            }
        }
    }
}

/**
 * Part 18 extension: a coloured badge that flips when rotation passes the threshold.
 * Uses words as well as colour, so it still works for colour-blind users.
 */
@Composable
fun MovementIndicator(isMoving: Boolean) {
    val colors = MaterialTheme.colorScheme
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMoving) colors.errorContainer else colors.secondaryContainer,
            contentColor = if (isMoving) colors.onErrorContainer else colors.onSecondaryContainer
        )
    ) {
        Text(
            text = if (isMoving) "MOVING" else "Still",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Android Studio preview: "phone without a temperature sensor, being rotated". */
@Preview(showBackground = true)
@Composable
fun SensorScreenPreview() {
    MaterialTheme {
        SensorScreen(
            temperature = null,
            temperatureAvailable = false,
            temperatureInvalid = false,
            gyroX = 0.812f,
            gyroY = -0.045f,
            gyroZ = 0.003f,
            gyroscopeAvailable = true
        )
    }
}
