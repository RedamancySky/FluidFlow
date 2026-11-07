package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.data.GesturePreferencesRepository
import com.example.util.AccessibilityWatchdog

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action ?: return

    when (action) {
      Intent.ACTION_BOOT_COMPLETED,
      Intent.ACTION_MY_PACKAGE_REPLACED -> {
        val repo = GesturePreferencesRepository(context)
        val settings = repo.settings.value
        if (settings.serviceEnabled && settings.antiKillNotificationEnabled) {
          KeepAliveForegroundService.start(context)
        }
        FluidGestureAccessibilityService.instance?.refreshOverlays()
      }
      Intent.ACTION_USER_PRESENT -> {
        // Screen unlocked: re-verify and refresh overlay windows in case Chinese ROM detached them
        FluidGestureAccessibilityService.instance?.refreshOverlays()

        val settings = GesturePreferencesRepository(context).settings.value
        if (settings.serviceEnabled) {
          // Give the accessibility framework a moment to rebind after unlock before judging it dead
          val pendingResult = goAsync()
          Handler(Looper.getMainLooper()).postDelayed({
            try {
              AccessibilityWatchdog.checkAndRecover(context)
            } finally {
              pendingResult.finish()
            }
          }, 2000)
        }
      }
    }
  }
}
