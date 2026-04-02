package com.android.lab5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun GForceMeterApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(GForceState()) }
    var sensorAvailable by remember { mutableStateOf(true) }
    val filteredValues = remember { floatArrayOf(0f, 0f, 0f) }
    val lastHistorySample = remember { longArrayOf(0L) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            sensorAvailable = false
            return@DisposableEffect onDispose {}
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                lowPassFilter(event.values, filteredValues)
                val snapshot = toGForce(filteredValues[0], filteredValues[1], filteredValues[2])
                val now = snapshot.timestamp

                val updatedHistory = if (now - lastHistorySample[0] >= SensorConfig.HISTORY_SAMPLE_INTERVAL_MS) {
                    lastHistorySample[0] = now
                    trimHistory(state.history + snapshot, now)
                } else {
                    state.history
                }

                state = state.copy(
                    current = snapshot,
                    maxMagnitude = maxOf(state.maxMagnitude, snapshot.magnitude),
                    maxX = maxOf(state.maxX, abs(snapshot.x)),
                    maxY = maxOf(state.maxY, abs(snapshot.y)),
                    maxZ = maxOf(state.maxZ, abs(snapshot.z)),
                    history = updatedHistory
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val mainHandler = Handler(Looper.getMainLooper())
        sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME,
            mainHandler
        )

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "G-Force Meter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        if (!sensorAvailable) {
            Text(
                "Accelerometer not available on this device",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(32.dp)
            )
        }

        GForceGauge(
            current = state.current,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        GForceReadout(state = state)

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(4.dp))

        Text(
            "History (30s)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp)
                .align(Alignment.Start)
        )

        GForceGraph(history = state.history)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                state = GForceState()
                filteredValues.fill(0f)
                lastHistorySample[0] = 0L
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Reset")
        }

        Spacer(Modifier.height(16.dp))
    }
}
