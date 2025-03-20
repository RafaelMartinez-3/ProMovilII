package com.example.futbolito

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }
    }
}

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var ballPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val ballRadius = 20.dp.value // Convertimos 20dp a píxeles aproximados
    val goalWidthFraction = 0.25f  // 25% del ancho
    val goalHeight = 40f
    var scoreTop by remember { mutableStateOf(0) }
    var scoreBottom by remember { mutableStateOf(0) }

    val obstacles = listOf(
        Offset(0.2f, 0.2f),
        Offset(0.3f, 0.5f),
        Offset(0.4f, 0.3f),
        Offset(0.6f, 0.4f),
        Offset(0.7f, 0.6f),
        Offset(0.5f, 0.7f),
        Offset(0.8f, 0.2f)
    ) // Posiciones relativas

    val obstacleSizeFraction = 0.1f // Tamaño relativo

    DisposableEffect(sensorManager) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (canvasSize.width == 0f || canvasSize.height == 0f) return

                    val dx = -it.values[0] * 5
                    val dy = it.values[1] * 5

                    val fieldWidth = canvasSize.width
                    val fieldHeight = canvasSize.height

                    val newPosition = Offset(
                        x = min(max(ballPosition.x + dx, ballRadius), fieldWidth - ballRadius),
                        y = min(max(ballPosition.y + dy, ballRadius), fieldHeight - ballRadius)
                    )

                    // Verificar colisión con obstáculos
                    val collision = obstacles.any {
                        val obsX = it.x * fieldWidth
                        val obsY = it.y * fieldHeight
                        newPosition.x in obsX..(obsX + obstacleSizeFraction * fieldWidth) &&
                                newPosition.y in obsY..(obsY + obstacleSizeFraction * fieldHeight)
                    }

                    if (!collision) {
                        ballPosition = newPosition
                    }

                    // Detectar gol en la portería superior
                    val goalWidth = fieldWidth * goalWidthFraction
                    val goalStartX = (fieldWidth - goalWidth) / 2

                    if (ballPosition.y <= goalHeight &&
                        ballPosition.x in goalStartX..(goalStartX + goalWidth)
                    ) {
                        scoreBottom++
                        ballPosition = Offset(fieldWidth / 2, fieldHeight / 2)
                    }

                    // Detectar gol en la portería inferior
                    if (ballPosition.y >= fieldHeight - goalHeight &&
                        ballPosition.x in goalStartX..(goalStartX + goalWidth)
                    ) {
                        scoreTop++
                        ballPosition = Offset(fieldWidth / 2, fieldHeight / 2)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(sensorListener) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Top: $scoreTop  |  Bottom: $scoreBottom",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasSize = size

                val fieldWidth = size.width
                val fieldHeight = size.height
                val goalWidth = fieldWidth * goalWidthFraction
                val goalStartX = (fieldWidth - goalWidth) / 2

                // Inicializar pelota si está en (0,0)
                if (ballPosition == Offset.Zero) {
                    ballPosition = Offset(fieldWidth / 2, fieldHeight / 2)
                }

                // Dibujar cancha
                drawRect(Color.Green, size = size)

                // Portería superior
                drawRect(
                    color = Color.White,
                    topLeft = Offset(goalStartX, 0f),
                    size = androidx.compose.ui.geometry.Size(goalWidth, goalHeight)
                )

                // Portería inferior
                drawRect(
                    color = Color.White,
                    topLeft = Offset(goalStartX, fieldHeight - goalHeight),
                    size = androidx.compose.ui.geometry.Size(goalWidth, goalHeight)
                )

                // Dibujar obstáculos
                obstacles.forEach {
                    val obsX = it.x * fieldWidth
                    val obsY = it.y * fieldHeight
                    drawRect(
                        Color.Gray,
                        topLeft = Offset(obsX, obsY),
                        size = androidx.compose.ui.geometry.Size(
                            obstacleSizeFraction * fieldWidth,
                            obstacleSizeFraction * fieldHeight
                        )
                    )
                }

                // Dibujar pelota
                drawCircle(color = Color.Red, radius = ballRadius, center = ballPosition)
            }
        }
    }
}
