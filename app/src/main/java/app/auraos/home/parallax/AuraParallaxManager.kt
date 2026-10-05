package app.auraos.home.parallax

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import app.auraos.core.performance.AuraPerformanceProfile

class AuraParallaxManager(
    context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var currentTilt = mutableStateOf(Offset.Zero)
        private set

    fun startListening() {
        rotationSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val rawX = if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            -event.values[1] * 18f
        } else {
            -event.values[0] * 2.2f
        }
        val rawY = if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            event.values[0] * 18f
        } else {
            event.values[1] * 2.2f
        }

        // Subtly clamped to avoid motion sickness
        val clampedX = rawX.coerceIn(-12f, 12f)
        val clampedY = rawY.coerceIn(-12f, 12f)
        currentTilt.value = Offset(clampedX, clampedY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun rememberAuraParallaxOffset(
    profile: AuraPerformanceProfile,
    isReducedMotion: Boolean
): State<Offset> {
    val tiltState = remember { mutableStateOf(Offset.Zero) }

    if (profile == AuraPerformanceProfile.LITE || isReducedMotion) {
        return tiltState
    }

    val multiplier = if (profile == AuraPerformanceProfile.FULL) 1.0f else 0.4f

    // Return subtle smoothed offset
    return remember(tiltState.value, multiplier) {
        mutableStateOf(Offset(tiltState.value.x * multiplier, tiltState.value.y * multiplier))
    }
}
