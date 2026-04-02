package com.android.lab6

object GameConstants {
    const val TARGET_FPS = 60
    const val FRAME_PERIOD = 1000L / TARGET_FPS

    // Paddle dimensions as fraction of screen
    const val PADDLE_WIDTH_RATIO = 0.25f
    const val PADDLE_HEIGHT_RATIO = 0.02f
    const val PADDLE_MARGIN_RATIO = 0.08f

    // Ball
    const val BALL_RADIUS_RATIO = 0.015f
    const val BALL_SPEED_RATIO = 0.008f
    const val BALL_SPEED_INCREASE = 1.05f

    // AI
    const val AI_SPEED_FACTOR = 0.75f

    // Score
    const val WIN_SCORE = 5

    // Colors (compile-time integer constants)
    const val COLOR_BACKGROUND = 0xFF1A1A2E.toInt()
    const val COLOR_PADDLE_PLAYER = 0xFFE94560.toInt()
    const val COLOR_PADDLE_AI = 0xFF0F3460.toInt()
    const val COLOR_BALL = 0xFFFFFFFF.toInt()
    const val COLOR_TEXT = 0xFFFFFFFF.toInt()
    const val COLOR_CENTER_LINE = 0x33FFFFFF
    const val COLOR_OVERLAY = 0xCC000000.toInt()
}
