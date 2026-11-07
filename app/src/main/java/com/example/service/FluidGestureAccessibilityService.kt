package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.example.MainActivity
import com.example.data.GesturePreferencesRepository
import com.example.model.AppGestureSettings
import com.example.model.GestureAction
import com.example.model.HapticIntensity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class FluidGestureAccessibilityService : AccessibilityService() {

  private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
  private val handler = Handler(Looper.getMainLooper())
  private lateinit var prefsRepo: GesturePreferencesRepository
  private lateinit var hapticHelper: HapticHelper
  private var windowManager: WindowManager? = null

  // Overlays
  private var leftEdgeView: View? = null
  private var rightEdgeView: View? = null
  private var bottomEdgeView: View? = null

  private var currentSettings: AppGestureSettings = AppGestureSettings()
  private var emergencyRescueHelper: EmergencyRescueHelper? = null

  private var lastPowerStateTime = 0L
  private var powerStateChangeCount = 0

  private var lastVolumeKeyTime = 0L
  private var volumeKeyPressCount = 0

  private val screenStateReceiver = object : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      when (intent?.action) {
        Intent.ACTION_SCREEN_ON,
        Intent.ACTION_SCREEN_OFF -> {
          // Detect 3 consecutive Power button toggles within 1.8s for fail-safe rescue
          val now = System.currentTimeMillis()
          if (now - lastPowerStateTime < 1800L) {
            powerStateChangeCount++
          } else {
            powerStateChangeCount = 1
          }
          lastPowerStateTime = now

          if (powerStateChangeCount >= 3) {
            powerStateChangeCount = 0
            triggerEmergencyRescue()
          }

          if (intent.action == Intent.ACTION_SCREEN_OFF) {
            // Zero CPU & battery while screen is locked: remove overlays immediately
            handler.removeCallbacksAndMessages(null)
            removeOverlays()
          }
        }
        Intent.ACTION_USER_PRESENT -> {
          if (currentSettings.serviceEnabled && currentSettings.autoRestoreOnUnlock) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ refreshOverlays() }, 150)
          }
        }
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    _isServiceConnected.value = true
    prefsRepo = GesturePreferencesRepository(this)
    hapticHelper = HapticHelper(this)
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    emergencyRescueHelper = EmergencyRescueHelper(this)

    val screenFilter = android.content.IntentFilter().apply {
      addAction(Intent.ACTION_SCREEN_ON)
      addAction(Intent.ACTION_SCREEN_OFF)
      addAction(Intent.ACTION_USER_PRESENT)
    }
    registerReceiver(screenStateReceiver, screenFilter)

    serviceScope.launch {
      prefsRepo.settings.collect { settings ->
        currentSettings = settings
        if (settings.shakeToRescueEnabled) {
          emergencyRescueHelper?.start()
        } else {
          emergencyRescueHelper?.stop()
        }
        refreshOverlays()
      }
    }
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
    _isServiceConnected.value = true
    if (!currentSettings.serviceEnabled) {
      serviceScope.launch {
        prefsRepo.setServiceEnabled(true)
      }
    }
    refreshOverlays()
  }

  override fun onKeyEvent(event: android.view.KeyEvent?): Boolean {
    if (event != null && event.action == android.view.KeyEvent.ACTION_DOWN) {
      if (event.keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
        val now = System.currentTimeMillis()
        if (now - lastVolumeKeyTime < 1400L) {
          volumeKeyPressCount++
        } else {
          volumeKeyPressCount = 1
        }
        lastVolumeKeyTime = now

        if (volumeKeyPressCount >= 3) {
          volumeKeyPressCount = 0
          triggerEmergencyRescue()
        }
      }
    }
    return super.onKeyEvent(event)
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    // Passive monitoring
  }

  override fun onInterrupt() {
    // Interrupted by system
  }

  override fun onDestroy() {
    try {
      unregisterReceiver(screenStateReceiver)
    } catch (_: Exception) {}
    emergencyRescueHelper?.stop()
    removeOverlays()
    KeepAliveForegroundService.stop(this)
    instance = null
    _isServiceConnected.value = false
    super.onDestroy()
  }

  fun triggerEmergencyRescue() {
    hapticHelper.vibrate(HapticIntensity.STRONG)
    val intent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra(MainActivity.EXTRA_EMERGENCY_RESCUE, true)
    }
    startActivity(intent)
  }

  fun executeGesture(action: GestureAction) {
    if (action == GestureAction.NONE) return

    _lastTriggeredAction.value = action

    // In playground/sandbox mode, do not dispatch system actions so users can freely practice
    if (isPlaygroundActive) {
      return
    }

    when (action) {
      GestureAction.BACK -> performGlobalAction(GLOBAL_ACTION_BACK)
      GestureAction.HOME -> performGlobalAction(GLOBAL_ACTION_HOME)
      GestureAction.RECENTS -> performGlobalAction(GLOBAL_ACTION_RECENTS)
      GestureAction.NOTIFICATIONS -> performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
      GestureAction.QUICK_SETTINGS -> performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
      GestureAction.SCREENSHOT -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        }
      }
      GestureAction.LOCK_SCREEN -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        }
      }
      GestureAction.LAST_APP -> {
        // Double-tap recents to jump to previous application
        performGlobalAction(GLOBAL_ACTION_RECENTS)
        handler.postDelayed({
          performGlobalAction(GLOBAL_ACTION_RECENTS)
        }, 150)
      }
      GestureAction.NONE -> Unit
    }
  }

  fun refreshOverlays() {
    if (currentSettings.serviceEnabled && currentSettings.antiKillNotificationEnabled) {
      KeepAliveForegroundService.start(this)
    } else {
      KeepAliveForegroundService.stop(this)
    }

    if (!currentSettings.serviceEnabled) {
      removeOverlays()
      return
    }

    removeOverlays()
    val wm = windowManager ?: return

    val metrics = DisplayMetrics()
    @Suppress("DEPRECATION")
    wm.defaultDisplay.getRealMetrics(metrics)
    val screenHeight = metrics.heightPixels
    val screenWidth = metrics.widthPixels
    val density = resources.displayMetrics.density

    // Determine window type
    // If SYSTEM_ALERT_WINDOW is granted, TYPE_APPLICATION_OVERLAY gives standard touch routing.
    // Otherwise, TYPE_ACCESSIBILITY_OVERLAY is natively permitted for AccessibilityService!
    val windowType = if (Settings.canDrawOverlays(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    val windowFlags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
      WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

    fun safeAddView(view: View, params: WindowManager.LayoutParams): Boolean {
      return try {
        wm.addView(view, params)
        true
      } catch (_: Exception) {
        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && params.type != WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY) {
            params.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            wm.addView(view, params)
            true
          } else {
            false
          }
        } catch (_: Exception) {
          false
        }
      }
    }

    // Setup Left Edge Overlay
    if (currentSettings.leftEdge.enabled) {
      val leftWidthPx = (currentSettings.leftEdge.widthDp * density).toInt()
      val leftHeightPx = (screenHeight * (currentSettings.leftEdge.heightPercent / 100f)).toInt()
      val leftView = createEdgeTouchView(
        isLeft = true,
        thresholdPx = currentSettings.leftEdge.triggerDistanceDp * density,
        swipeAction = currentSettings.leftEdge.swipeAction,
        holdAction = currentSettings.leftEdge.holdAction,
        diagUpAction = currentSettings.leftEdge.diagonalUpAction,
        diagDownAction = currentSettings.leftEdge.diagonalDownAction
      )

      val leftParams = WindowManager.LayoutParams(
        leftWidthPx,
        leftHeightPx,
        windowType,
        windowFlags,
        PixelFormat.TRANSLUCENT
      ).apply {
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        x = 0
        y = 0
      }

      if (safeAddView(leftView, leftParams)) {
        leftEdgeView = leftView
      }
    }

    // Setup Right Edge Overlay
    if (currentSettings.rightEdge.enabled) {
      val rightWidthPx = (currentSettings.rightEdge.widthDp * density).toInt()
      val rightHeightPx = (screenHeight * (currentSettings.rightEdge.heightPercent / 100f)).toInt()
      val rightView = createEdgeTouchView(
        isLeft = false,
        thresholdPx = currentSettings.rightEdge.triggerDistanceDp * density,
        swipeAction = currentSettings.rightEdge.swipeAction,
        holdAction = currentSettings.rightEdge.holdAction,
        diagUpAction = currentSettings.rightEdge.diagonalUpAction,
        diagDownAction = currentSettings.rightEdge.diagonalDownAction
      )

      val rightParams = WindowManager.LayoutParams(
        rightWidthPx,
        rightHeightPx,
        windowType,
        windowFlags,
        PixelFormat.TRANSLUCENT
      ).apply {
        gravity = Gravity.END or Gravity.CENTER_VERTICAL
        x = 0
        y = 0
      }

      if (safeAddView(rightView, rightParams)) {
        rightEdgeView = rightView
      }
    }

    // Setup Bottom Edge Overlay
    if (currentSettings.bottomBar.enabled) {
      val bottomHeightPx = (currentSettings.bottomBar.heightDp * density).toInt()
      val bottomView = createBottomTouchView(
        screenWidth = screenWidth,
        thresholdPx = currentSettings.bottomBar.triggerDistanceDp * density
      )

      val yOffsetPx = if (currentSettings.bottomBar.offsetAbove3Buttons) (48 * density).toInt() else 0

      val bottomParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        bottomHeightPx,
        windowType,
        windowFlags,
        PixelFormat.TRANSLUCENT
      ).apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        x = 0
        y = yOffsetPx
      }

      if (safeAddView(bottomView, bottomParams)) {
        bottomEdgeView = bottomView
      }
    }
  }

  fun removeOverlays() {
    val wm = windowManager ?: return
    leftEdgeView?.let {
      try { wm.removeView(it) } catch (_: Exception) {}
      leftEdgeView = null
    }
    rightEdgeView?.let {
      try { wm.removeView(it) } catch (_: Exception) {}
      rightEdgeView = null
    }
    bottomEdgeView?.let {
      try { wm.removeView(it) } catch (_: Exception) {}
      bottomEdgeView = null
    }
  }

  private fun createEdgeTouchView(
    isLeft: Boolean,
    thresholdPx: Float,
    swipeAction: GestureAction,
    holdAction: GestureAction,
    diagUpAction: GestureAction,
    diagDownAction: GestureAction
  ): View {
    val view = View(this)
    // 100% Transparent: Absolutely no visible colored strips or faint borders on screen
    view.setBackgroundColor(0x00000000)

    var startX = 0f
    var startY = 0f
    var lastProcessedX = 0f
    var isTriggered = false
    var isHoldFired = false
    var holdRunnable: Runnable? = null

    view.setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          startX = event.rawX
          startY = event.rawY
          lastProcessedX = startX
          isTriggered = false
          isHoldFired = false

          holdRunnable = Runnable {
            if (isTriggered && !isHoldFired && holdAction != GestureAction.NONE) {
              isHoldFired = true
              triggerHaptic()
              executeGesture(holdAction)
            }
          }
          handler.postDelayed(holdRunnable!!, 450)
          true
        }
        MotionEvent.ACTION_MOVE -> {
          // Micro-movement throttling to save CPU & Battery
          if (abs(event.rawX - lastProcessedX) < 4f) {
            return@setOnTouchListener true
          }
          lastProcessedX = event.rawX

          val deltaX = if (isLeft) event.rawX - startX else startX - event.rawX
          val deltaY = event.rawY - startY

          if (deltaX >= thresholdPx) {
            if (!isTriggered) {
              isTriggered = true
              triggerHaptic()
            }
          } else {
            isTriggered = false
          }
          true
        }
        MotionEvent.ACTION_UP -> {
          holdRunnable?.let { handler.removeCallbacks(it) }

          val deltaX = if (isLeft) event.rawX - startX else startX - event.rawX
          val deltaY = event.rawY - startY

          if (isTriggered && !isHoldFired) {
            // Check diagonal swipes
            if (deltaY < -thresholdPx * 1.5f && diagUpAction != GestureAction.NONE) {
              executeGesture(diagUpAction)
            } else if (deltaY > thresholdPx * 1.5f && diagDownAction != GestureAction.NONE) {
              executeGesture(diagDownAction)
            } else {
              executeGesture(swipeAction)
            }
          }
          isTriggered = false
          isHoldFired = false
          true
        }
        MotionEvent.ACTION_CANCEL -> {
          holdRunnable?.let { handler.removeCallbacks(it) }
          isTriggered = false
          isHoldFired = false
          true
        }
        else -> false
      }
    }
    return view
  }

  private fun createBottomTouchView(screenWidth: Int, thresholdPx: Float): View {
    val view = View(this)
    // 100% Transparent: Absolutely no visible colored strips on bottom edge
    view.setBackgroundColor(0x00000000)

    var startX = 0f
    var startY = 0f
    var lastProcessedY = 0f
    var isTriggered = false
    var isHoldFired = false
    var holdRunnable: Runnable? = null

    view.setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          startX = event.rawX
          startY = event.rawY
          isTriggered = false
          isHoldFired = false

          holdRunnable = Runnable {
            if (isTriggered && !isHoldFired) {
              isHoldFired = true
              triggerHaptic()
              executeGesture(currentSettings.bottomBar.centerHoldAction)
            }
          }
          handler.postDelayed(holdRunnable!!, 450)
          true
        }
        MotionEvent.ACTION_MOVE -> {
          val deltaY = startY - event.rawY
          val deltaX = event.rawX - startX

          if (deltaY >= thresholdPx || abs(deltaX) >= thresholdPx * 2.0f) {
            if (!isTriggered) {
              isTriggered = true
              triggerHaptic()
            }
          } else {
            isTriggered = false
          }
          true
        }
        MotionEvent.ACTION_UP -> {
          holdRunnable?.let { handler.removeCallbacks(it) }

          val deltaY = startY - event.rawY
          val deltaX = event.rawX - startX

          if (isTriggered && !isHoldFired) {
            if (abs(deltaX) > thresholdPx * 1.8f) {
              // Swipe horizontally along bottom edge: Quick Switch
              executeGesture(currentSettings.bottomBar.swipeAcrossAction)
            } else {
              // Split into left 25%, center 50%, right 25%
              val sectionWidth = screenWidth / 4f
              val action = when {
                startX < sectionWidth -> currentSettings.bottomBar.leftSectionSwipe
                startX > screenWidth - sectionWidth -> currentSettings.bottomBar.rightSectionSwipe
                else -> currentSettings.bottomBar.centerSectionSwipe
              }
              executeGesture(action)
            }
          }
          isTriggered = false
          isHoldFired = false
          true
        }
        MotionEvent.ACTION_CANCEL -> {
          holdRunnable?.let { handler.removeCallbacks(it) }
          isTriggered = false
          isHoldFired = false
          true
        }
        else -> false
      }
    }
    return view
  }

  private fun triggerHaptic() {
    if (currentSettings.haptic.enabled) {
      hapticHelper.vibrate(currentSettings.haptic.intensity)
    }
  }

  companion object {
    var instance: FluidGestureAccessibilityService? = null
    var isPlaygroundActive: Boolean = false

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected = _isServiceConnected.asStateFlow()

    private val _lastTriggeredAction = MutableStateFlow<GestureAction?>(null)
    val lastTriggeredAction = _lastTriggeredAction.asStateFlow()

    fun isServiceActive(): Boolean = instance != null
  }
}
