package ca.gbc.comp3074.morrison.lab5

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import ca.gbc.comp3074.morrison.lab5.ui.theme.Lab5Theme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationComponent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationComponent(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val fusedClient = remember(key1 = context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()

    val location = remember { mutableStateOf<Location?>(null) }
    val status = remember { mutableStateOf("") }

    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun fetchLocation() {
        if (!hasPermission()) {
            status.value = "Permission not granted"
            return
        }

        status.value = "Fetching location..."
        val cancelToken = CancellationTokenSource()
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancelToken.token
        ).addOnSuccessListener { l ->
            if (l == null) {
                status.value = "Location not found"
            } else {
                location.value = l
                status.value = "location: (%.5f, %.5f)".format(l.latitude, l.longitude)
            }
        }.addOnFailureListener { e ->
            status.value = "Location fetch failed: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        if (res.values.any { it }) {
            fetchLocation()
        } else {
            status.value = "Permission denied"
        }
    }

    fun startTracking() {
        if (locationCallback != null) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateIntervalMillis(2000L).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                location.value = result.lastLocation
                status.value = "Location: (%.5f, %.5f)".format(
                    result.lastLocation?.latitude,
                    result.lastLocation?.longitude
                )
            }
        }
        locationCallback = callback
        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            null
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = status.value,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (hasPermission()) {
                    fetchLocation()
                } else {
                    permissionLauncher.launch(
                        input = arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Get Location")
        }
        Button(
            onClick = {
                startTracking()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Start Tracking")
        }
        Button(
            onClick = {
                locationCallback?.let { fusedClient.removeLocationUpdates(it) }
                locationCallback = null
                location.value = null
                status.value = "Tracking stopped"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Stop Tracking")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationPreview() {
    Lab5Theme {
        LocationComponent()
    }
}
