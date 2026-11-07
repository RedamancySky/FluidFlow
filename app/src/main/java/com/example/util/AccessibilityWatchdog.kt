package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.service.FluidGestureAccessibilityService

object AccessibilityWatchdog {

  private const val CHANNEL_ID = "fluid_flow_recovery_alert"
  private const val NOTIFICATION_ID = 2001
  private const val RENOTIFY_COOLDOWN_MS = 10 * 60 * 1000L

  @Volatile
  private var lastNotifiedAt = 0L

  fun isEnabledInSystem(context: Context): Boolean {
    val expected = "${context.packageName}/${FluidGestureAccessibilityService::class.java.name}"
    val enabledServices = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabledServices.split(':').any { it.equals(expected, ignoreCase = true) }
  }

  /**
   * Checks whether the gesture service is actually alive right now and, if not,
   * raises a recovery notification (debounced) pointing straight at Accessibility settings.
   * HyperOS/MIUI can silently revoke the accessibility toggle or kill the whole process
   * in the background without any callback the app can intercept, so this is a
   * detect-after-the-fact safety net rather than a guarantee of instant recovery.
   */
  fun checkAndRecover(context: Context, force: Boolean = false) {
    val isDown = !isEnabledInSystem(context) || !FluidGestureAccessibilityService.isServiceConnected.value
    if (!isDown) return

    val now = System.currentTimeMillis()
    if (!force && now - lastNotifiedAt < RENOTIFY_COOLDOWN_MS) return
    lastNotifiedAt = now

    notifyServiceDown(context)
  }

  private fun notifyServiceDown(context: Context) {
    createChannelIfNeeded(context)

    val openSettingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      openSettingsIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setContentTitle("FluidFlow đã bị hệ thống tắt mất")
      .setContentText("Cử chỉ vuốt đã ngừng hoạt động. Nhấn để mở lại Dịch Vụ Trợ Năng.")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_ERROR)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    manager?.notify(NOTIFICATION_ID, notification)
  }

  private fun createChannelIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(
      CHANNEL_ID,
      "Cảnh Báo Mất Cử Chỉ",
      NotificationManager.IMPORTANCE_HIGH
    ).apply {
      description = "Cảnh báo khi hệ thống tắt ngầm Dịch Vụ Trợ Năng của FluidFlow"
    }
    val manager = context.getSystemService(NotificationManager::class.java)
    manager?.createNotificationChannel(channel)
  }
}
