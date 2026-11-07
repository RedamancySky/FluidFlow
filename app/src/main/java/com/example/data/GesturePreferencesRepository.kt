package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppGestureSettings
import com.example.model.BottomBarConfig
import com.example.model.EdgeZoneConfig
import com.example.model.GestureAction
import com.example.model.HapticConfig
import com.example.model.HapticIntensity
import com.example.model.VisualWaveConfig
import com.example.model.WaveThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GesturePreferencesRepository(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("fluid_gesture_prefs", Context.MODE_PRIVATE)

  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<AppGestureSettings> = _settings.asStateFlow()

  fun updateSettings(newSettings: AppGestureSettings) {
    _settings.value = newSettings
    saveSettings(newSettings)
  }

  fun setServiceEnabled(enabled: Boolean) {
    val updated = _settings.value.copy(serviceEnabled = enabled)
    updateSettings(updated)
  }

  fun updateLeftEdge(transform: (EdgeZoneConfig) -> EdgeZoneConfig) {
    val updated = _settings.value.copy(leftEdge = transform(_settings.value.leftEdge))
    updateSettings(updated)
  }

  fun updateRightEdge(transform: (EdgeZoneConfig) -> EdgeZoneConfig) {
    val updated = _settings.value.copy(rightEdge = transform(_settings.value.rightEdge))
    updateSettings(updated)
  }

  fun updateBottomBar(transform: (BottomBarConfig) -> BottomBarConfig) {
    val updated = _settings.value.copy(bottomBar = transform(_settings.value.bottomBar))
    updateSettings(updated)
  }

  fun updateVisual(transform: (VisualWaveConfig) -> VisualWaveConfig) {
    val updated = _settings.value.copy(visual = transform(_settings.value.visual))
    updateSettings(updated)
  }

  fun updateHaptic(transform: (HapticConfig) -> HapticConfig) {
    val updated = _settings.value.copy(haptic = transform(_settings.value.haptic))
    updateSettings(updated)
  }

  fun setAntiKillNotificationEnabled(enabled: Boolean) {
    val updated = _settings.value.copy(antiKillNotificationEnabled = enabled)
    updateSettings(updated)
  }

  fun setAutoRestoreOnUnlock(enabled: Boolean) {
    val updated = _settings.value.copy(autoRestoreOnUnlock = enabled)
    updateSettings(updated)
  }

  fun setBottomOffsetAbove3Buttons(enabled: Boolean) {
    val updated = _settings.value.copy(
      bottomBar = _settings.value.bottomBar.copy(offsetAbove3Buttons = enabled)
    )
    updateSettings(updated)
  }

  fun setShakeToRescueEnabled(enabled: Boolean) {
    val updated = _settings.value.copy(shakeToRescueEnabled = enabled)
    updateSettings(updated)
  }

  private fun loadSettings(): AppGestureSettings {
    val serviceEnabled = prefs.getBoolean(KEY_SERVICE_ENABLED, true)

    val leftSwipe = GestureAction.fromId(prefs.getString(KEY_LEFT_SWIPE, GestureAction.BACK.id) ?: GestureAction.BACK.id)
    val leftHold = GestureAction.fromId(prefs.getString(KEY_LEFT_HOLD, GestureAction.LAST_APP.id) ?: GestureAction.LAST_APP.id)
    val leftDiagUp = GestureAction.fromId(prefs.getString(KEY_LEFT_DIAG_UP, GestureAction.NOTIFICATIONS.id) ?: GestureAction.NOTIFICATIONS.id)
    val leftDiagDown = GestureAction.fromId(prefs.getString(KEY_LEFT_DIAG_DOWN, GestureAction.QUICK_SETTINGS.id) ?: GestureAction.QUICK_SETTINGS.id)
    val leftHeight = prefs.getFloat(KEY_LEFT_HEIGHT, 65f)
    val leftWidth = prefs.getInt(KEY_LEFT_WIDTH, 18)
    val leftOffset = prefs.getFloat(KEY_LEFT_OFFSET, 15f)
    val leftTrigger = prefs.getInt(KEY_LEFT_TRIGGER, 36)

    val rightSwipe = GestureAction.fromId(prefs.getString(KEY_RIGHT_SWIPE, GestureAction.BACK.id) ?: GestureAction.BACK.id)
    val rightHold = GestureAction.fromId(prefs.getString(KEY_RIGHT_HOLD, GestureAction.RECENTS.id) ?: GestureAction.RECENTS.id)
    val rightDiagUp = GestureAction.fromId(prefs.getString(KEY_RIGHT_DIAG_UP, GestureAction.SCREENSHOT.id) ?: GestureAction.SCREENSHOT.id)
    val rightDiagDown = GestureAction.fromId(prefs.getString(KEY_RIGHT_DIAG_DOWN, GestureAction.NOTIFICATIONS.id) ?: GestureAction.NOTIFICATIONS.id)
    val rightHeight = prefs.getFloat(KEY_RIGHT_HEIGHT, 65f)
    val rightWidth = prefs.getInt(KEY_RIGHT_WIDTH, 18)
    val rightOffset = prefs.getFloat(KEY_RIGHT_OFFSET, 15f)
    val rightTrigger = prefs.getInt(KEY_RIGHT_TRIGGER, 36)

    val bottomCenter = GestureAction.fromId(prefs.getString(KEY_BOTTOM_CENTER, GestureAction.HOME.id) ?: GestureAction.HOME.id)
    val bottomLeft = GestureAction.fromId(prefs.getString(KEY_BOTTOM_LEFT, GestureAction.BACK.id) ?: GestureAction.BACK.id)
    val bottomRight = GestureAction.fromId(prefs.getString(KEY_BOTTOM_RIGHT, GestureAction.RECENTS.id) ?: GestureAction.RECENTS.id)
    val bottomHold = GestureAction.fromId(prefs.getString(KEY_BOTTOM_HOLD, GestureAction.RECENTS.id) ?: GestureAction.RECENTS.id)
    val bottomSwipeAcross = GestureAction.fromId(prefs.getString(KEY_BOTTOM_ACROSS, GestureAction.LAST_APP.id) ?: GestureAction.LAST_APP.id)

    val themePresetId = prefs.getString(KEY_THEME_PRESET, WaveThemePreset.CYAN_PULSE.id) ?: WaveThemePreset.CYAN_PULSE.id
    val preset = WaveThemePreset.entries.find { it.id == themePresetId } ?: WaveThemePreset.CYAN_PULSE
    val showIcon = prefs.getBoolean(KEY_SHOW_ICON, true)
    val showPill = prefs.getBoolean(KEY_SHOW_PILL, true)
    val pillOpacity = prefs.getFloat(KEY_PILL_OPACITY, 0.45f)

    val hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true)
    val hapticIntensityId = prefs.getString(KEY_HAPTIC_INTENSITY, HapticIntensity.MEDIUM.id) ?: HapticIntensity.MEDIUM.id
    val hapticIntensity = HapticIntensity.entries.find { it.id == hapticIntensityId } ?: HapticIntensity.MEDIUM

    return AppGestureSettings(
      serviceEnabled = serviceEnabled,
      leftEdge = EdgeZoneConfig(
        enabled = prefs.getBoolean(KEY_LEFT_ENABLED, true),
        swipeAction = leftSwipe,
        holdAction = leftHold,
        diagonalUpAction = leftDiagUp,
        diagonalDownAction = leftDiagDown,
        heightPercent = leftHeight,
        widthDp = leftWidth,
        verticalOffsetPercent = leftOffset,
        triggerDistanceDp = leftTrigger
      ),
      rightEdge = EdgeZoneConfig(
        enabled = prefs.getBoolean(KEY_RIGHT_ENABLED, true),
        swipeAction = rightSwipe,
        holdAction = rightHold,
        diagonalUpAction = rightDiagUp,
        diagonalDownAction = rightDiagDown,
        heightPercent = rightHeight,
        widthDp = rightWidth,
        verticalOffsetPercent = rightOffset,
        triggerDistanceDp = rightTrigger
      ),
      bottomBar = BottomBarConfig(
        enabled = prefs.getBoolean(KEY_BOTTOM_ENABLED, true),
        leftSectionSwipe = bottomLeft,
        centerSectionSwipe = bottomCenter,
        rightSectionSwipe = bottomRight,
        centerHoldAction = bottomHold,
        swipeAcrossAction = bottomSwipeAcross,
        heightDp = prefs.getInt(KEY_BOTTOM_HEIGHT, 36),
        triggerDistanceDp = prefs.getInt(KEY_BOTTOM_TRIGGER, 32),
        offsetAbove3Buttons = prefs.getBoolean(KEY_BOTTOM_OFFSET_ABOVE_3BUTTONS, false)
      ),
      visual = VisualWaveConfig(
        themePreset = preset,
        fluidWaveColor = preset.waveColor,
        accentGlowColor = preset.glowColor,
        showActionIcon = showIcon,
        showIndicatorPill = showPill,
        pillOpacity = pillOpacity
      ),
      haptic = HapticConfig(
        enabled = hapticEnabled,
        intensity = hapticIntensity
      ),
      antiKillNotificationEnabled = prefs.getBoolean(KEY_ANTI_KILL, true),
      autoRestoreOnUnlock = prefs.getBoolean(KEY_AUTO_RESTORE, true),
      shakeToRescueEnabled = prefs.getBoolean(KEY_SHAKE_TO_RESCUE, true)
    )
  }

  private fun saveSettings(s: AppGestureSettings) {
    prefs.edit()
      .putBoolean(KEY_SERVICE_ENABLED, s.serviceEnabled)
      .putBoolean(KEY_LEFT_ENABLED, s.leftEdge.enabled)
      .putString(KEY_LEFT_SWIPE, s.leftEdge.swipeAction.id)
      .putString(KEY_LEFT_HOLD, s.leftEdge.holdAction.id)
      .putString(KEY_LEFT_DIAG_UP, s.leftEdge.diagonalUpAction.id)
      .putString(KEY_LEFT_DIAG_DOWN, s.leftEdge.diagonalDownAction.id)
      .putFloat(KEY_LEFT_HEIGHT, s.leftEdge.heightPercent)
      .putInt(KEY_LEFT_WIDTH, s.leftEdge.widthDp)
      .putFloat(KEY_LEFT_OFFSET, s.leftEdge.verticalOffsetPercent)
      .putInt(KEY_LEFT_TRIGGER, s.leftEdge.triggerDistanceDp)

      .putBoolean(KEY_RIGHT_ENABLED, s.rightEdge.enabled)
      .putString(KEY_RIGHT_SWIPE, s.rightEdge.swipeAction.id)
      .putString(KEY_RIGHT_HOLD, s.rightEdge.holdAction.id)
      .putString(KEY_RIGHT_DIAG_UP, s.rightEdge.diagonalUpAction.id)
      .putString(KEY_RIGHT_DIAG_DOWN, s.rightEdge.diagonalDownAction.id)
      .putFloat(KEY_RIGHT_HEIGHT, s.rightEdge.heightPercent)
      .putInt(KEY_RIGHT_WIDTH, s.rightEdge.widthDp)
      .putFloat(KEY_RIGHT_OFFSET, s.rightEdge.verticalOffsetPercent)
      .putInt(KEY_RIGHT_TRIGGER, s.rightEdge.triggerDistanceDp)

      .putBoolean(KEY_BOTTOM_ENABLED, s.bottomBar.enabled)
      .putString(KEY_BOTTOM_LEFT, s.bottomBar.leftSectionSwipe.id)
      .putString(KEY_BOTTOM_CENTER, s.bottomBar.centerSectionSwipe.id)
      .putString(KEY_BOTTOM_RIGHT, s.bottomBar.rightSectionSwipe.id)
      .putString(KEY_BOTTOM_HOLD, s.bottomBar.centerHoldAction.id)
      .putString(KEY_BOTTOM_ACROSS, s.bottomBar.swipeAcrossAction.id)
      .putInt(KEY_BOTTOM_HEIGHT, s.bottomBar.heightDp)
      .putInt(KEY_BOTTOM_TRIGGER, s.bottomBar.triggerDistanceDp)
      .putBoolean(KEY_BOTTOM_OFFSET_ABOVE_3BUTTONS, s.bottomBar.offsetAbove3Buttons)

      .putString(KEY_THEME_PRESET, s.visual.themePreset.id)
      .putBoolean(KEY_SHOW_ICON, s.visual.showActionIcon)
      .putBoolean(KEY_SHOW_PILL, s.visual.showIndicatorPill)
      .putFloat(KEY_PILL_OPACITY, s.visual.pillOpacity)

      .putBoolean(KEY_HAPTIC_ENABLED, s.haptic.enabled)
      .putString(KEY_HAPTIC_INTENSITY, s.haptic.intensity.id)
      .putBoolean(KEY_ANTI_KILL, s.antiKillNotificationEnabled)
      .putBoolean(KEY_AUTO_RESTORE, s.autoRestoreOnUnlock)
      .putBoolean(KEY_SHAKE_TO_RESCUE, s.shakeToRescueEnabled)
      .apply()
  }

  companion object {
    private const val KEY_SERVICE_ENABLED = "service_enabled"
    private const val KEY_ANTI_KILL = "anti_kill_notification"
    private const val KEY_AUTO_RESTORE = "auto_restore_unlock"
    private const val KEY_LEFT_ENABLED = "left_enabled"
    private const val KEY_LEFT_SWIPE = "left_swipe"
    private const val KEY_LEFT_HOLD = "left_hold"
    private const val KEY_LEFT_DIAG_UP = "left_diag_up"
    private const val KEY_LEFT_DIAG_DOWN = "left_diag_down"
    private const val KEY_LEFT_HEIGHT = "left_height"
    private const val KEY_LEFT_WIDTH = "left_width"
    private const val KEY_LEFT_OFFSET = "left_offset"
    private const val KEY_LEFT_TRIGGER = "left_trigger"

    private const val KEY_RIGHT_ENABLED = "right_enabled"
    private const val KEY_RIGHT_SWIPE = "right_swipe"
    private const val KEY_RIGHT_HOLD = "right_hold"
    private const val KEY_RIGHT_DIAG_UP = "right_diag_up"
    private const val KEY_RIGHT_DIAG_DOWN = "right_diag_down"
    private const val KEY_RIGHT_HEIGHT = "right_height"
    private const val KEY_RIGHT_WIDTH = "right_width"
    private const val KEY_RIGHT_OFFSET = "right_offset"
    private const val KEY_RIGHT_TRIGGER = "right_trigger"

    private const val KEY_BOTTOM_ENABLED = "bottom_enabled"
    private const val KEY_BOTTOM_LEFT = "bottom_left"
    private const val KEY_BOTTOM_CENTER = "bottom_center"
    private const val KEY_BOTTOM_RIGHT = "bottom_right"
    private const val KEY_BOTTOM_HOLD = "bottom_hold"
    private const val KEY_BOTTOM_ACROSS = "bottom_across"
    private const val KEY_BOTTOM_HEIGHT = "bottom_height"
    private const val KEY_BOTTOM_TRIGGER = "bottom_trigger"
    private const val KEY_BOTTOM_OFFSET_ABOVE_3BUTTONS = "bottom_offset_above_3buttons"

    private const val KEY_THEME_PRESET = "theme_preset"
    private const val KEY_SHOW_ICON = "show_icon"
    private const val KEY_SHOW_PILL = "show_pill"
    private const val KEY_PILL_OPACITY = "pill_opacity"

    private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
    private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
    private const val KEY_SHAKE_TO_RESCUE = "shake_to_rescue"
  }
}
