package com.example.service

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.MainActivity
import kotlin.math.sqrt

class EmergencyRescueHelper(private val context: Context) : SensorEventListener {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
  private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

  private var isListening = false
  private var lastShakeTimestamp = 0L
  private var shakeCount = 0

  fun start() {
    if (isListening || accelerometer == null) return
    sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    isListening = true
  }

  fun stop() {
    if (!isListening) return
    sensorManager?.unregisterListener(this)
    isListening = false
    shakeCount = 0
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

    val x = event.values[0]
    val y = event.values[1]
    val z = event.values[2]

    val gForce = sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

    // Detect strong shake motion
    if (gForce > 13.5) {
      val now = System.currentTimeMillis()
      if (now - lastShakeTimestamp < 400) {
        // Debounce micro oscillations within same shake
        return
      }

      if (now - lastShakeTimestamp < 1500) {
        shakeCount++
      } else {
        shakeCount = 1
      }
      lastShakeTimestamp = now

      // 3 firm shakes triggers emergency rescue
      if (shakeCount >= 3) {
        shakeCount = 0
        triggerEmergencyRescue()
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // No-op
  }

  private fun triggerEmergencyRescue() {
    // Haptic SOS feedback
    vibrateEmergencyAlert()

    // Launch MainActivity with Emergency Rescue modal
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra(MainActivity.EXTRA_EMERGENCY_RESCUE, true)
    }
    context.startActivity(intent)
  }

  private fun vibrateEmergencyAlert() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150, 100, 300), -1)
        vm?.vibrate(CombinedVibration.createParallel(effect))
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150, 100, 300), -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(500)
        }
      }
    } catch (_: Exception) {
    }
  }
}
