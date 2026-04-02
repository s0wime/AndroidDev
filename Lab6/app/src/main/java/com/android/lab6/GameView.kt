package com.android.lab6

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameState = GameState()
    private val renderer = GameRenderer()
    private var gameThread: GameThread? = null

    // Pending touch events — written on UI thread, read on game thread
    @Volatile
    private var pendingTouchX: Float = -1f
    @Volatile
    private var pendingServe: Boolean = false
    @Volatile
    private var pendingReset: Boolean = false

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Dimensions will be set in surfaceChanged
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (width == 0 || height == 0) return

        val wasRunning = gameThread?.running == true
        if (wasRunning) {
            gameThread?.running = false
            gameThread?.join()
            gameThread = null
        }

        gameState.initDimensions(width, height)

        gameThread = GameThread(holder, gameState, renderer, this).apply {
            running = true
            start()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameThread?.running = false
        var retry = true
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (_: InterruptedException) {
                // Retry joining
            }
        }
        gameThread = null
    }

    /** Called by GameThread at the start of each frame to apply pending input. */
    fun applyPendingInput() {
        val touchX = pendingTouchX
        if (touchX >= 0f) {
            gameState.onPlayerTouch(touchX)
        }
        if (pendingReset) {
            pendingReset = false
            gameState.reset()
        } else if (pendingServe) {
            pendingServe = false
            gameState.serveBall()
        }
    }

    @SuppressLint("ClickableAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pendingTouchX = event.x
                when (gameState.phase) {
                    GamePhase.GAME_OVER -> pendingReset = true
                    GamePhase.SERVING -> pendingServe = true
                    else -> {}
                }
            }
            MotionEvent.ACTION_MOVE -> {
                pendingTouchX = event.x
            }
        }
        return true
    }
}
