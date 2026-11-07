package com.example.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.HapticIntensity

class HapticHelper(private val context: Context) {
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  fun vibrate(intensity: HapticIntensity) {
    if (intensity == HapticIntensity.OFF || vibrator == null || !vibrator.hasVibrator()) return

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val effect = when (intensity) {
          HapticIntensity.LIGHT -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
          HapticIntensity.MEDIUM -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
          HapticIntensity.STRONG -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
          HapticIntensity.OFF -> null
        }
        if (effect != null) {
          vibrator.vibrate(effect)
        } else {
          vibrator.vibrate(
            VibrationEffect.createOneShot(intensity.durationMs, intensity.amplitude)
          )
        }
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(
          VibrationEffect.createOneShot(intensity.durationMs, intensity.amplitude)
        )
      }
    } catch (_: Exception) {
      // Fallback for devices without predefined effect support
      try {
        @Suppress("DEPRECATION")
        vibrator.vibrate(intensity.durationMs)
      } catch (_: Exception) {
        // Silently ignore if vibration permission or hardware fails
      }
    }
  }

  fun vibrateCustom(durationMs: Long, amplitude: Int = 180) {
    if (vibrator == null || !vibrator.hasVibrator()) return
    try {
      vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
    } catch (_: Exception) {
      @Suppress("DEPRECATION")
      vibrator.vibrate(durationMs)
    }
  }
}
