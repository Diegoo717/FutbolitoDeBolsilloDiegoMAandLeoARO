package com.example.futbolitodiegomaandleoaro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext

class AccelerometerManager(private val context: Context) {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun hasAccelerometer(): Boolean = accelerometer != null

    fun registerListener(listener: SensorEventListener, samplingPeriod: Int) {
        accelerometer?.let {
            sensorManager.registerListener(
                listener,
                it,
                samplingPeriod
            )
        }
    }

    fun unregisterListener(listener: SensorEventListener) {
        sensorManager.unregisterListener(listener)
    }
}

@Composable
fun rememberAccelerometerState(): MutableState<Offset> {
    val context = LocalContext.current
    val accelerometerManager = remember { AccelerometerManager(context) }
    val accelerometerValue = remember { mutableStateOf(Offset(0f, 0f)) }

    DisposableEffect(key1 = Unit) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {

                val x = event.values[0] * -0.2f
                val y = event.values[1] * 0.2f

                accelerometerValue.value = Offset(x, y)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

            }
        }

        accelerometerManager.registerListener(
            sensorListener,
            SensorManager.SENSOR_DELAY_GAME
        )

        onDispose {
            accelerometerManager.unregisterListener(sensorListener)
        }
    }

    return accelerometerValue
}