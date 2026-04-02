package com.android.lab6

import android.view.SurfaceHolder

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameState: GameState,
    private val renderer: GameRenderer,
    private val gameView: GameView
) : Thread() {

    @Volatile
    var running = false

    override fun run() {
        while (running) {
            val startTime = System.currentTimeMillis()

            val canvas = surfaceHolder.lockCanvas()
            if (canvas == null) {
                sleep(GameConstants.FRAME_PERIOD)
                continue
            }
            try {
                gameView.applyPendingInput()
                gameState.update()
                renderer.draw(canvas, gameState)
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }

            val elapsed = System.currentTimeMillis() - startTime
            val sleepTime = GameConstants.FRAME_PERIOD - elapsed
            if (sleepTime > 0) {
                sleep(sleepTime)
            }
        }
    }
}
