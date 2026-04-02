package com.android.lab5

import android.os.SystemClock
import kotlin.math.sqrt

fun lowPassFilter(
    input: FloatArray,
    output: FloatArray,
    alpha: Float = SensorConfig.LOW_PASS_ALPHA
) {
    for (i in input.indices) {
        output[i] = output[i] + alpha * (input[i] - output[i])
    }
}

fun toGForce(x: Float, y: Float, z: Float): GForceSnapshot {
    val gx = x / SensorConfig.GRAVITY
    val gy = y / SensorConfig.GRAVITY
    val gz = z / SensorConfig.GRAVITY
    val mag = sqrt(gx * gx + gy * gy + gz * gz)
    return GForceSnapshot(gx, gy, gz, mag, SystemClock.elapsedRealtime())
}

fun trimHistory(history: List<GForceSnapshot>, now: Long): List<GForceSnapshot> {
    val cutoff = now - SensorConfig.HISTORY_DURATION_MS
    return history.filter { it.timestamp >= cutoff }
}
