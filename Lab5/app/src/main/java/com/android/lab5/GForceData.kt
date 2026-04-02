package com.android.lab5

data class GForceSnapshot(
    val x: Float,
    val y: Float,
    val z: Float,
    val magnitude: Float,
    val timestamp: Long
)

data class GForceState(
    val current: GForceSnapshot = GForceSnapshot(0f, 0f, 0f, 0f, 0L),
    val maxMagnitude: Float = 0f,
    val maxX: Float = 0f,
    val maxY: Float = 0f,
    val maxZ: Float = 0f,
    val history: List<GForceSnapshot> = emptyList()
)

object SensorConfig {
    const val GRAVITY = 9.80665f
    const val LOW_PASS_ALPHA = 0.2f
    const val HISTORY_DURATION_MS = 30_000L
    const val HISTORY_SAMPLE_INTERVAL_MS = 100L
    const val GAUGE_MAX_G = 3.0f
}
