package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.GesturePreferencesRepository

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
      }
    }
  }
}
