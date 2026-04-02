package com.android.lab6

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface

class GameRenderer {

    private val backgroundPaint = Paint().apply {
        color = GameConstants.COLOR_BACKGROUND
        style = Paint.Style.FILL
    }

    private val playerPaddlePaint = Paint().apply {
        color = GameConstants.COLOR_PADDLE_PLAYER
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val aiPaddlePaint = Paint().apply {
        color = GameConstants.COLOR_PADDLE_AI
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val ballPaint = Paint().apply {
        color = GameConstants.COLOR_BALL
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val scorePaint = Paint().apply {
        color = GameConstants.COLOR_TEXT
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        isAntiAlias = true
    }

    private val centerLinePaint = Paint().apply {
        color = GameConstants.COLOR_CENTER_LINE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 0f)
        isAntiAlias = true
    }

    private val overlayPaint = Paint().apply {
        color = GameConstants.COLOR_OVERLAY
        style = Paint.Style.FILL
    }

    private val messagePaint = Paint().apply {
        color = GameConstants.COLOR_TEXT
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    fun draw(canvas: Canvas, state: GameState) {
        drawBackground(canvas, state)
        drawCenterLine(canvas, state)
        drawScores(canvas, state)
        drawPaddles(canvas, state)
        drawBall(canvas, state)

        when (state.phase) {
            GamePhase.GAME_OVER -> drawGameOverOverlay(canvas, state)
            GamePhase.SERVING -> drawServingPrompt(canvas, state)
            else -> {}
        }
    }

    private fun drawBackground(canvas: Canvas, state: GameState) {
        canvas.drawRect(0f, 0f, state.screenWidth.toFloat(), state.screenHeight.toFloat(), backgroundPaint)
    }

    private fun drawCenterLine(canvas: Canvas, state: GameState) {
        val centerY = state.screenHeight / 2f
        canvas.drawLine(0f, centerY, state.screenWidth.toFloat(), centerY, centerLinePaint)
    }

    private fun drawScores(canvas: Canvas, state: GameState) {
        scorePaint.textSize = state.screenWidth * 0.12f
        val centerX = state.screenWidth / 2f

        // AI score (top half)
        canvas.drawText(
            state.aiScore.toString(),
            centerX,
            state.screenHeight * 0.35f,
            scorePaint
        )

        // Player score (bottom half)
        canvas.drawText(
            state.playerScore.toString(),
            centerX,
            state.screenHeight * 0.7f,
            scorePaint
        )
    }

    private fun drawPaddles(canvas: Canvas, state: GameState) {
        val halfW = state.paddleWidth / 2
        val cornerRadius = state.paddleHeight / 2

        // Player paddle (bottom)
        canvas.drawRoundRect(
            state.playerPaddleX - halfW,
            state.playerPaddleY,
            state.playerPaddleX + halfW,
            state.playerPaddleY + state.paddleHeight,
            cornerRadius, cornerRadius,
            playerPaddlePaint
        )

        // AI paddle (top)
        canvas.drawRoundRect(
            state.aiPaddleX - halfW,
            state.aiPaddleY,
            state.aiPaddleX + halfW,
            state.aiPaddleY + state.paddleHeight,
            cornerRadius, cornerRadius,
            aiPaddlePaint
        )
    }

    private fun drawBall(canvas: Canvas, state: GameState) {
        canvas.drawCircle(state.ballX, state.ballY, state.ballRadius, ballPaint)
    }

    private fun drawServingPrompt(canvas: Canvas, state: GameState) {
        messagePaint.textSize = state.screenWidth * 0.05f
        canvas.drawText(
            "Tap to serve",
            state.screenWidth / 2f,
            state.screenHeight * 0.55f,
            messagePaint
        )
    }

    private fun drawGameOverOverlay(canvas: Canvas, state: GameState) {
        // Dimmed overlay
        canvas.drawRect(0f, 0f, state.screenWidth.toFloat(), state.screenHeight.toFloat(), overlayPaint)

        val centerX = state.screenWidth / 2f
        val centerY = state.screenHeight / 2f

        // Winner text
        messagePaint.textSize = state.screenWidth * 0.1f
        val winnerText = if (state.winner == "PLAYER") "You Win!" else "AI Wins!"
        canvas.drawText(winnerText, centerX, centerY - state.screenWidth * 0.05f, messagePaint)

        // Final score
        messagePaint.textSize = state.screenWidth * 0.06f
        canvas.drawText(
            "${state.playerScore} - ${state.aiScore}",
            centerX,
            centerY + state.screenWidth * 0.05f,
            messagePaint
        )

        // Restart prompt
        messagePaint.textSize = state.screenWidth * 0.04f
        canvas.drawText(
            "Tap to play again",
            centerX,
            centerY + state.screenWidth * 0.15f,
            messagePaint
        )
    }
}
