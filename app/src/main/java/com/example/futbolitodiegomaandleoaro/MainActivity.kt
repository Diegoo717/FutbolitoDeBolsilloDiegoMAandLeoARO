package com.example.futbolitodiegomaandleoaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futbolitodiegomaandleoaro.ui.theme.FootballGameTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                FootballGameTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FootballGame()
                    }
                }
            }
        }
    }

@Composable
fun FootballGame() {
    var ballPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var ballVelocity by remember { mutableStateOf(Offset(0f, 0f)) }
    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }

    var timeRemaining by remember { mutableStateOf(50) }
    var currentPeriod by remember { mutableStateOf(1) }
    var currentPlayer by remember { mutableStateOf(1) }
    var gameActive by remember { mutableStateOf(true) }

    var isPaused by remember { mutableStateOf(false) }

    var showPeriodDialog by remember { mutableStateOf(false) }
    var showFinalDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val accelerometer = rememberAccelerometerState()

    fun positionBallForCurrentPlayer(width: Float, height: Float) {
        if (currentPlayer == 1) {
            ballPosition = Offset(width / 2, height * 0.15f)
        } else {
            ballPosition = Offset(width / 2, height * 0.85f)
        }
    }

    LaunchedEffect(key1 = currentPeriod, key2 = currentPlayer, key3 = isPaused) {
        timeRemaining = 15
        while (timeRemaining > 0 && gameActive) {
            if (!isPaused) {
                delay(1000)
                timeRemaining--

                if (timeRemaining == 0) {
                    ballVelocity = Offset(0f, 0f)

                    if (currentPlayer == 1) {
                        currentPlayer = 2
                        dialogMessage = "¡Cambio de jugador! Ahora es turno del Jugador 2"
                    } else {
                        currentPlayer = 1
                        currentPeriod++
                        dialogMessage = "¡Cambio de jugador! Ahora es turno del Jugador 1"
                    }

                    showPeriodDialog = true

                    if (currentPeriod > 3) {
                        gameActive = false
                        var resultMessage = "¡Juego Terminado!\n\n"
                        resultMessage += "Jugador 1: $player2Score\n"
                        resultMessage += "Jugador 2: $player1Score\n\n"

                        if (player1Score > player2Score) {
                            resultMessage += "¡El Jugador 2 ha ganado!"
                        } else if (player2Score > player1Score) {
                            resultMessage += "¡El Jugador 1 ha ganado!"
                        } else {
                            resultMessage += "¡Empate!"
                        }

                        dialogMessage = resultMessage
                        showFinalDialog = true
                    }
                }
            }
        }
    }

    // Efecto para manejar la lógica de movimiento y colisiones
    LaunchedEffect(key1 = Unit, key2 = isPaused) {
        while (true) {
            delay(16)

            if (gameActive && !showPeriodDialog && !showFinalDialog && !isPaused) {
                ballVelocity = Offset(
                    ballVelocity.x + accelerometer.value.x,
                    ballVelocity.y + accelerometer.value.y
                )

                ballPosition = Offset(
                    ballPosition.x + ballVelocity.x,
                    ballPosition.y + ballVelocity.y
                )

                ballVelocity = Offset(
                    ballVelocity.x * 0.98f,
                    ballVelocity.y * 0.98f
                )
            }
        }
    }

    if (showPeriodDialog) {
        AlertDialog(
            onDismissRequest = { showPeriodDialog = false },
            title = { Text("Cambio de Turno") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showPeriodDialog = false
                        ballVelocity = Offset(0f, 0f)
                    }
                ) {
                    Text("Continuar")
                }
            }
        )
    }

    // Diálogo de fin de juego
    if (showFinalDialog) {
        AlertDialog(
            onDismissRequest = { /* No hacer nada */ },
            title = { Text("Resultado Final") },
            text = {
                Text(
                    text = dialogMessage,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Reiniciar juego
                        player1Score = 0
                        player2Score = 0
                        currentPeriod = 1
                        currentPlayer = 1
                        gameActive = true
                        showFinalDialog = false
                    }
                ) {
                    Text("Nuevo Juego")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E5221),
                            Color(0xFF1E5221)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tiempo: $timeRemaining s - Periodo: $currentPeriod/3",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                IconButton(
                    onClick = {
                        isPaused = !isPaused
                    },
                    modifier = Modifier
                        .background(
                            color = if (isPaused) Color.Green.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Close,
                        contentDescription = if (isPaused) "Reanudar" else "Pausar",
                        tint = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(
                        color = if (currentPlayer == 1)
                            Color.Blue.copy(alpha = 0.2f)
                        else
                            Color.Red.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "Turno del Jugador $currentPlayer",
                    fontSize = 16.sp,
                    color = if (currentPlayer == 1) Color.White else Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Jugador 1: $player2Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue.copy(alpha = 0.8f)
                )
                Text(
                    text = "Jugador 2: $player1Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red.copy(alpha = 0.8f)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val ballRadius = 30f
                val goalWidth = width * 0.3f
                val goalHeight = height * 0.1f
                val obstacleRadius = 40f
                val wallThickness = 15f
                val arcRadius = 100f

                if (ballPosition.x == 0f && ballPosition.y == 0f) {
                    positionBallForCurrentPlayer(width, height)
                }

                val newX = when {
                    ballPosition.x - ballRadius < 0 -> {
                        ballVelocity = Offset(-ballVelocity.x * 0.8f, ballVelocity.y)
                        ballRadius
                    }
                    ballPosition.x + ballRadius > width -> {
                        ballVelocity = Offset(-ballVelocity.x * 0.8f, ballVelocity.y)
                        width - ballRadius
                    }
                    else -> ballPosition.x
                }

                val newY = when {
                    ballPosition.y - ballRadius < 0 -> {
                        if (ballPosition.x > width / 2 - goalWidth / 2 &&
                            ballPosition.x < width / 2 + goalWidth / 2 &&
                            currentPlayer == 2) {
                            player1Score++
                            positionBallForCurrentPlayer(width, height)
                            ballVelocity = Offset(0f, 0f)
                            return@Canvas
                        } else {
                            ballVelocity = Offset(ballVelocity.x, -ballVelocity.y * 0.8f)
                            ballRadius
                        }
                    }
                    ballPosition.y + ballRadius > height -> {
                        if (ballPosition.x > width / 2 - goalWidth / 2 &&
                            ballPosition.x < width / 2 + goalWidth / 2 &&
                            currentPlayer == 1) {
                            player2Score++
                            positionBallForCurrentPlayer(width, height)
                            ballVelocity = Offset(0f, 0f)
                            return@Canvas
                        } else {
                            ballVelocity = Offset(ballVelocity.x, -ballVelocity.y * 0.8f)
                            height - ballRadius
                        }
                    }
                    else -> ballPosition.y
                }

                ballPosition = Offset(newX, newY)

                drawRect(color = Color(0xFF4CAF50), size = Size(width, height))

                drawRect(
                    color = Color.White,
                    topLeft = Offset(0f, 0f),
                    size = Size(width, height),
                    style = Stroke(width = 8f)
                )

                drawLine(
                    color = Color.White,
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 4f
                )

                drawCircle(
                    color = Color.White,
                    radius = width / 6,
                    center = Offset(width / 2, height / 2),
                    style = Stroke(width = 4f)
                )

                val smallAreaWidth = width * 0.15f
                val smallAreaHeight = height * 0.2f

                drawRect(
                    color = Color.White,
                    topLeft = Offset(width/2 - smallAreaWidth/2, 0f),
                    size = Size(smallAreaWidth, smallAreaHeight),
                    style = Stroke(width = 2f)
                )

                drawRect(
                    color = Color.White,
                    topLeft = Offset(width/2 - smallAreaWidth/2, height - smallAreaHeight),
                    size = Size(smallAreaWidth, smallAreaHeight),
                    style = Stroke(width = 2f)
                )

                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(width / 2 - goalWidth / 2, 0f),
                    size = Size(goalWidth, goalHeight),
                    style = Stroke(width = 8f)
                )

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(width / 2 - goalWidth / 2, height - goalHeight),
                    size = Size(goalWidth, goalHeight),
                    style = Stroke(width = 8f)
                )

                val obstacles = listOf(
                    // Barras centrales horizontales
                    RectObstacle(Offset(width * 0.2f, height * 0.3f), 100f, 15f),
                    RectObstacle(Offset(width * 0.8f, height * 0.3f), 100f, 15f),
                    RectObstacle(Offset(width * 0.2f, height * 0.7f), 100f, 15f),
                    RectObstacle(Offset(width * 0.8f, height * 0.7f), 100f, 15f),
                    RectObstacle(Offset(width * 0.4f, height * 0.4f), 60f, 15f),
                    RectObstacle(Offset(width * 0.6f, height * 0.4f), 60f, 15f),
                    RectObstacle(Offset(width * 0.4f, height * 0.6f), 60f, 15f),
                    RectObstacle(Offset(width * 0.6f, height * 0.6f), 60f, 15f),

                    // Barras verticales centrales
                    RectObstacle(Offset(width * 0.3f, height * 0.5f), 15f, 120f),
                    RectObstacle(Offset(width * 0.7f, height * 0.5f), 15f, 120f),

                    // Jugadores circulares
                    CircleObstacle(Offset(width * 0.1f, height * 0.2f), obstacleRadius),
                    CircleObstacle(Offset(width * 0.9f, height * 0.2f), obstacleRadius),
                    CircleObstacle(Offset(width * 0.1f, height * 0.8f), obstacleRadius),
                    CircleObstacle(Offset(width * 0.9f, height * 0.8f), obstacleRadius),
                    CircleObstacle(Offset(width * 0.2f, height * 0.4f), obstacleRadius * 0.7f),
                    CircleObstacle(Offset(width * 0.8f, height * 0.4f), obstacleRadius * 0.7f),
                    CircleObstacle(Offset(width * 0.2f, height * 0.6f), obstacleRadius * 0.7f),
                    CircleObstacle(Offset(width * 0.8f, height * 0.6f), obstacleRadius * 0.7f),

                    // Defensas
                    RectObstacle(Offset(width * 0.35f, height * 0.15f), 70f, 25f),
                    RectObstacle(Offset(width * 0.65f, height * 0.15f), 70f, 25f),
                    RectObstacle(Offset(width * 0.35f, height * 0.85f), 70f, 25f),
                    RectObstacle(Offset(width * 0.65f, height * 0.85f), 70f, 25f),

                    // Paredes laterales con aberturas
                    RectObstacle(Offset(width * 0.1f, height * 0.3f), wallThickness, height * 0.4f),
                    RectObstacle(Offset(width * 0.9f, height * 0.3f), wallThickness, height * 0.4f),

                    // Arcos semicirculares
                    ArcObstacle(Offset(width * 0.5f, height * 0.25f), arcRadius, 180f, 180f),
                    ArcObstacle(Offset(width * 0.5f, height * 0.75f), arcRadius, 0f, 180f),

                    // Obstáculos diagonales
                    RectObstacle(Offset(width * 0.25f, height * 0.25f), 80f, 15f, 45f),
                    RectObstacle(Offset(width * 0.75f, height * 0.25f), 80f, 15f, -45f),
                    RectObstacle(Offset(width * 0.25f, height * 0.75f), 80f, 15f, -45f),
                    RectObstacle(Offset(width * 0.75f, height * 0.75f), 80f, 15f, 45f)
                )

                for (obstacle in obstacles) {
                    when (obstacle) {
                        is CircleObstacle -> {
                            drawCircle(
                                color = Color.White,
                                center = obstacle.center,
                                radius = obstacle.radius,
                                style = Stroke(width = 3f)
                            )

                            val distance = sqrt(
                                (ballPosition.x - obstacle.center.x).pow(2) +
                                        (ballPosition.y - obstacle.center.y).pow(2)
                            )

                            if (distance < ballRadius + obstacle.radius) {
                                val nx = (ballPosition.x - obstacle.center.x) / distance
                                val ny = (ballPosition.y - obstacle.center.y) / distance

                                val dotProduct = ballVelocity.x * nx + ballVelocity.y * ny
                                ballVelocity = Offset(
                                    ballVelocity.x - 2 * dotProduct * nx,
                                    ballVelocity.y - 2 * dotProduct * ny
                                ) * 0.9f

                                val overlap = ballRadius + obstacle.radius - distance + 1f
                                ballPosition = Offset(
                                    ballPosition.x + overlap * nx,
                                    ballPosition.y + overlap * ny
                                )
                            }
                        }

                        is RectObstacle -> {
                            if (obstacle.rotation != 0f) {
                                rotate(
                                    degrees = obstacle.rotation,
                                    pivot = obstacle.center
                                ) {
                                    drawRect(
                                        color = Color.White,
                                        topLeft = Offset(obstacle.center.x - obstacle.width/2, obstacle.center.y - obstacle.height/2),
                                        size = Size(obstacle.width, obstacle.height),
                                        style = Stroke(width = 3f)
                                    )
                                }
                            } else {
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(obstacle.center.x - obstacle.width/2, obstacle.center.y - obstacle.height/2),
                                    size = Size(obstacle.width, obstacle.height),
                                    style = Stroke(width = 3f)
                                )
                            }

                            if (obstacle.rotation == 0f) {
                                val closestX = ballPosition.x.coerceIn(obstacle.center.x - obstacle.width/2, obstacle.center.x + obstacle.width/2)
                                val closestY = ballPosition.y.coerceIn(obstacle.center.y - obstacle.height/2, obstacle.center.y + obstacle.height/2)

                                val distance = sqrt(
                                    (ballPosition.x - closestX).pow(2) +
                                            (ballPosition.y - closestY).pow(2)
                                )

                                if (distance < ballRadius) {
                                    val nx = (ballPosition.x - closestX) / distance
                                    val ny = (ballPosition.y - closestY) / distance

                                    val dotProduct = ballVelocity.x * nx + ballVelocity.y * ny
                                    ballVelocity = Offset(
                                        ballVelocity.x - 2 * dotProduct * nx,
                                        ballVelocity.y - 2 * dotProduct * ny
                                    ) * 0.9f

                                    val overlap = ballRadius - distance + 1f
                                    ballPosition = Offset(
                                        ballPosition.x + overlap * nx,
                                        ballPosition.y + overlap * ny
                                    )
                                }
                            }
                        }

                        is ArcObstacle -> {
                            drawArc(
                                color = Color.White,
                                startAngle = obstacle.startAngle,
                                sweepAngle = obstacle.sweepAngle,
                                useCenter = false,
                                topLeft = Offset(obstacle.center.x - obstacle.radius, obstacle.center.y - obstacle.radius),
                                size = Size(obstacle.radius * 2, obstacle.radius * 2),
                                style = Stroke(width = 3f)
                            )

                            val distance = sqrt(
                                (ballPosition.x - obstacle.center.x).pow(2) +
                                        (ballPosition.y - obstacle.center.y).pow(2)
                            )

                            if (distance < ballRadius + obstacle.radius &&
                                distance > obstacle.radius - ballRadius) {
                                val nx = (ballPosition.x - obstacle.center.x) / distance
                                val ny = (ballPosition.y - obstacle.center.y) / distance

                                val dotProduct = ballVelocity.x * nx + ballVelocity.y * ny
                                ballVelocity = Offset(
                                    ballVelocity.x - 2 * dotProduct * nx,
                                    ballVelocity.y - 2 * dotProduct * ny
                                ) * 0.9f

                                val overlap = ballRadius + obstacle.radius - distance + 1f
                                ballPosition = Offset(
                                    ballPosition.x + overlap * nx,
                                    ballPosition.y + overlap * ny
                                )
                            }
                        }
                    }
                }



                
                drawCircle(color = Color.DarkGray, radius = ballRadius, center = ballPosition)
                drawCircle(
                    color = Color.Green,
                    radius = ballRadius * 0.2f,
                    center = ballPosition,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

sealed class Obstacle
data class CircleObstacle(val center: Offset, val radius: Float) : Obstacle()
data class RectObstacle(
    val center: Offset,
    val width: Float = 60f,
    val height: Float = 60f,
    val rotation: Float = 0f
) : Obstacle()
data class ArcObstacle(
    val center: Offset,
    val radius: Float,
    val startAngle: Float,
    val sweepAngle: Float
) : Obstacle()

operator fun Offset.times(scalar: Float) = Offset(x * scalar, y * scalar)