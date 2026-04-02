package com.android.lab6

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sign
import kotlin.random.Random

enum class GamePhase { SERVING, PLAYING, GAME_OVER }

class GameState {
    var screenWidth = 0
        private set
    var screenHeight = 0
        private set

    // Ball
    var ballX = 0f
    var ballY = 0f
    var ballSpeedX = 0f
    var ballSpeedY = 0f
    var ballRadius = 0f
        private set

    // Paddles
    var playerPaddleX = 0f
    var aiPaddleX = 0f
    var paddleWidth = 0f
        private set
    var paddleHeight = 0f
        private set
    var playerPaddleY = 0f
        private set
    var aiPaddleY = 0f
        private set

    // Score
    var playerScore = 0
        private set
    var aiScore = 0
        private set

    // Phase
    var phase = GamePhase.SERVING
        private set

    var winner = ""
        private set

    fun initDimensions(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        paddleWidth = width * GameConstants.PADDLE_WIDTH_RATIO
        paddleHeight = height * GameConstants.PADDLE_HEIGHT_RATIO
        ballRadius = width * GameConstants.BALL_RADIUS_RATIO

        val margin = height * GameConstants.PADDLE_MARGIN_RATIO
        playerPaddleY = height - margin - paddleHeight
        aiPaddleY = margin

        playerPaddleX = width / 2f
        aiPaddleX = width / 2f

        ballX = width / 2f
        ballY = height / 2f
    }

    fun update() {
        if (phase != GamePhase.PLAYING) return
        moveBall()
        moveAI()
        checkWallCollision()
        checkPaddleCollision()
        checkScoring()
    }

    fun onPlayerTouch(x: Float) {
        playerPaddleX = x.coerceIn(paddleWidth / 2, screenWidth - paddleWidth / 2)
    }

    fun serveBall() {
        if (phase != GamePhase.SERVING) return
        ballX = screenWidth / 2f
        ballY = screenHeight / 2f

        val baseSpeed = screenHeight * GameConstants.BALL_SPEED_RATIO
        val angle = Random.nextFloat() * 0.5f + 0.25f // 0.25 to 0.75
        ballSpeedX = baseSpeed * angle * if (Random.nextBoolean()) 1f else -1f
        // Always serve toward the player (downward = positive Y)
        ballSpeedY = baseSpeed * (1f - angle)

        phase = GamePhase.PLAYING
    }

    fun reset() {
        playerScore = 0
        aiScore = 0
        winner = ""
        ballX = screenWidth / 2f
        ballY = screenHeight / 2f
        ballSpeedX = 0f
        ballSpeedY = 0f
        playerPaddleX = screenWidth / 2f
        aiPaddleX = screenWidth / 2f
        phase = GamePhase.SERVING
    }

    private fun moveBall() {
        // Clamp speed to prevent tunneling through paddles
        val maxSpeedY = paddleHeight * 0.8f
        if (abs(ballSpeedY) > maxSpeedY) {
            val scale = maxSpeedY / abs(ballSpeedY)
            ballSpeedX *= scale
            ballSpeedY *= scale
        }
        ballX += ballSpeedX
        ballY += ballSpeedY
    }

    private fun moveAI() {
        val targetX = ballX
        val diff = targetX - aiPaddleX
        val maxMove = abs(ballSpeedY) * GameConstants.AI_SPEED_FACTOR

        aiPaddleX += diff.coerceIn(-maxMove, maxMove)
        aiPaddleX = aiPaddleX.coerceIn(paddleWidth / 2, screenWidth - paddleWidth / 2)
    }

    private fun checkWallCollision() {
        if (ballX - ballRadius <= 0) {
            ballX = ballRadius
            ballSpeedX = abs(ballSpeedX)
        }
        if (ballX + ballRadius >= screenWidth) {
            ballX = screenWidth - ballRadius
            ballSpeedX = -abs(ballSpeedX)
        }
    }

    private fun checkPaddleCollision() {
        // Player paddle (bottom)
        if (ballSpeedY > 0 &&
            ballY + ballRadius >= playerPaddleY &&
            ballY + ballRadius <= playerPaddleY + paddleHeight &&
            ballX >= playerPaddleX - paddleWidth / 2 &&
            ballX <= playerPaddleX + paddleWidth / 2
        ) {
            ballY = playerPaddleY - ballRadius
            reflectBall(playerPaddleX)
        }

        // AI paddle (top)
        if (ballSpeedY < 0 &&
            ballY - ballRadius <= aiPaddleY + paddleHeight &&
            ballY - ballRadius >= aiPaddleY &&
            ballX >= aiPaddleX - paddleWidth / 2 &&
            ballX <= aiPaddleX + paddleWidth / 2
        ) {
            ballY = aiPaddleY + paddleHeight + ballRadius
            reflectBall(aiPaddleX)
        }
    }

    private fun reflectBall(paddleCenterX: Float) {
        val offset = (ballX - paddleCenterX) / (paddleWidth / 2) // -1 to 1
        val currentSpeed = hypot(ballSpeedX.toDouble(), ballSpeedY.toDouble()).toFloat()
        val baseSpeed = screenHeight * GameConstants.BALL_SPEED_RATIO
        val maxSpeed = baseSpeed * 3f
        val newSpeed = (currentSpeed * GameConstants.BALL_SPEED_INCREASE).coerceAtMost(maxSpeed)

        ballSpeedX = newSpeed * offset * 0.7f
        ballSpeedY = -ballSpeedY.sign * newSpeed * 0.85f
    }

    private fun checkScoring() {
        // Ball passed player paddle (bottom)
        if (ballY - ballRadius > screenHeight) {
            aiScore++
            if (aiScore >= GameConstants.WIN_SCORE) {
                phase = GamePhase.GAME_OVER
                winner = "AI"
            } else {
                phase = GamePhase.SERVING
            }
        }

        // Ball passed AI paddle (top)
        if (ballY + ballRadius < 0) {
            playerScore++
            if (playerScore >= GameConstants.WIN_SCORE) {
                phase = GamePhase.GAME_OVER
                winner = "PLAYER"
            } else {
                phase = GamePhase.SERVING
            }
        }
    }
}
