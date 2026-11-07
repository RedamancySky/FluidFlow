package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.util.AccessibilityWatchdog

class KeepAliveForegroundService : Service() {

  private val handler = Handler(Looper.getMainLooper())
  private val healthCheckRunnable = object : Runnable {
    override fun run() {
      AccessibilityWatchdog.checkAndRecover(applicationContext)
      handler.postDelayed(this, HEALTH_CHECK_INTERVAL_MS)
    }
  }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    val notification = buildForegroundNotification()
    startForeground(NOTIFICATION_ID, notification)
    isRunning = true
    handler.postDelayed(healthCheckRunnable, HEALTH_CHECK_INTERVAL_MS)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP -> {
        stopSelf()
        return START_NOT_STICKY
      }
      ACTION_RESTORE_NAV_BAR -> {
        com.example.util.SystemNavBarManager.setGlobalNavBarHidden(this, false)
        com.example.util.SystemNavBarManager.openSystemNavigationSettings(this)
        val rescueIntent = Intent(this, MainActivity::class.java).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
          putExtra(MainActivity.EXTRA_EMERGENCY_RESCUE, true)
        }
        startActivity(rescueIntent)
        return START_NOT_STICKY
      }
    }
    return START_STICKY
  }

  override fun onDestroy() {
    isRunning = false
    handler.removeCallbacks(healthCheckRunnable)
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Duy Trì Cử Chỉ & Cứu Hộ Khẩn Cấp",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Giữ cử chỉ điều hướng mượt mà và cung cấp nút cứu hộ nhanh khi đơ cảm ứng"
        setShowBadge(false)
      }
      val manager = getSystemService(NotificationManager::class.java)
      manager?.createNotificationChannel(channel)
    }
  }

  private fun buildForegroundNotification(): Notification {
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
      },
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val rescuePendingIntent = PendingIntent.getActivity(
      this,
      1,
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(MainActivity.EXTRA_EMERGENCY_RESCUE, true)
      },
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val restoreNavPendingIntent = PendingIntent.getService(
      this,
      2,
      Intent(this, KeepAliveForegroundService::class.java).apply {
        action = ACTION_RESTORE_NAV_BAR
      },
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("FluidFlow Gestures đang chạy")
      .setContentText("Kéo xuống để mở nhanh Cứu hộ hoặc Khôi phục 3 phím")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentIntent(pendingIntent)
      .addAction(0, "🆘 Cứu Hộ / Mở App", rescuePendingIntent)
      .addAction(0, "🔄 Khôi Phục 3 Phím", restoreNavPendingIntent)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_SERVICE)
      .build()
  }

  companion object {
    private const val CHANNEL_ID = "fluid_flow_keep_alive"
    private const val NOTIFICATION_ID = 1001
    const val ACTION_STOP = "com.example.STOP_KEEP_ALIVE"
    const val ACTION_RESTORE_NAV_BAR = "com.example.RESTORE_NAV_BAR"

    var isRunning: Boolean = false

    fun start(context: Context) {
      val intent = Intent(context, KeepAliveForegroundService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stop(context: Context) {
      val intent = Intent(context, KeepAliveForegroundService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }
  }
}
