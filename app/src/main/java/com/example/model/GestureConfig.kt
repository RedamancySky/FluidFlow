package com.example.model

enum class GestureZoneType(val id: String, val title: String, val defaultSide: String) {
  LEFT_EDGE("LEFT_EDGE", "Cạnh Trái", "LEFT"),
  RIGHT_EDGE("RIGHT_EDGE", "Cạnh Phải", "RIGHT"),
  BOTTOM_BAR("BOTTOM_BAR", "Thanh Đáy", "BOTTOM")
}

data class EdgeZoneConfig(
  val enabled: Boolean = true,
  val swipeAction: GestureAction = GestureAction.BACK,
  val holdAction: GestureAction = GestureAction.RECENTS,
  val diagonalUpAction: GestureAction = GestureAction.NOTIFICATIONS,
  val diagonalDownAction: GestureAction = GestureAction.QUICK_SETTINGS,
  val heightPercent: Float = 65f, // Generous 65% of screen height
  val widthDp: Int = 24, // Generous 24dp touch target
  val verticalOffsetPercent: Float = 15f,
  val triggerDistanceDp: Int = 32 // Quick responsive trigger distance
)

data class BottomBarConfig(
  val enabled: Boolean = true,
  val leftSectionSwipe: GestureAction = GestureAction.BACK,
  val centerSectionSwipe: GestureAction = GestureAction.HOME,
  val rightSectionSwipe: GestureAction = GestureAction.RECENTS,
  val centerHoldAction: GestureAction = GestureAction.RECENTS,
  val swipeAcrossAction: GestureAction = GestureAction.LAST_APP,
  val heightDp: Int = 36, // Comfortable 36dp grab area
  val triggerDistanceDp: Int = 32,
  val offsetAbove3Buttons: Boolean = false // When true, elevate bottom bar by 48dp above the 3-button system bar
)

enum class BackIndicatorStyle(val id: String, val displayName: String, val description: String) {
  ADAPTIVE_COMPACT("ADAPTIVE_COMPACT", "Nhỏ Gọn Thích Ứng Nền (Gợi ý)", "Tự đổi màu theo nền sáng/tối, kích thước tinh tế"),
  COMPACT_CHEVRON("COMPACT_CHEVRON", "Mũi Tên Tối Giản", "Thanh thoát, chỉ hiển thị mũi tên chevron mờ nhẹ"),
  FLUID_TEARDROP("FLUID_TEARDROP", "Giọt Nước Neon", "Hiệu ứng bóng nước uốn lượn phong cách Cyber")
}

data class VisualWaveConfig(
  val themePreset: WaveThemePreset = WaveThemePreset.ADAPTIVE_THEME,
  val indicatorStyle: BackIndicatorStyle = BackIndicatorStyle.ADAPTIVE_COMPACT,
  val fluidWaveColor: Long = 0xDD1E293B,
  val accentGlowColor: Long = 0xFF00E5FF,
  val showActionIcon: Boolean = true,
  val showIndicatorPill: Boolean = true,
  val pillOpacity: Float = 0.35f,
  val bubbleScale: Float = 0.65f, // Compact sizing
  val adaptiveBackground: Boolean = true
)

enum class WaveThemePreset(
  val id: String,
  val displayName: String,
  val waveColor: Long,
  val glowColor: Long
) {
  ADAPTIVE_THEME("ADAPTIVE", "Thích Ứng Nền (Sáng/Tối)", 0xEE1E293B, 0xFF00E5FF),
  CYAN_PULSE("CYAN", "Electric Cyan", 0xFF00E5FF, 0xFF8B5CF6),
  NEON_VIOLET("VIOLET", "Neon Violet", 0xFF8B5CF6, 0xFFEC4899),
  EMERALD_FLOW("EMERALD", "Emerald Stream", 0xFF10B981, 0xFF06B6D4),
  SUNSET_AMBER("AMBER", "Cyber Amber", 0xFFF59E0B, 0xFFEF4444),
  OBSIDIAN_WHITE("MINIMAL", "Monochrome Minimal", 0xFFE2E8F0, 0xFF94A3B8)
}

enum class HapticIntensity(val id: String, val displayName: String, val durationMs: Long, val amplitude: Int) {
  OFF("OFF", "Tắt rung", 0, 0),
  LIGHT("LIGHT", "Nhẹ (Tick)", 12, 50),
  MEDIUM("MEDIUM", "Vừa (Click)", 20, 110),
  STRONG("STRONG", "Mạnh (Heavy)", 35, 220)
}

data class HapticConfig(
  val enabled: Boolean = true,
  val intensity: HapticIntensity = HapticIntensity.LIGHT,
  val vibrateOnHoldThreshold: Boolean = true
)

data class AppGestureSettings(
  val serviceEnabled: Boolean = true,
  val leftEdge: EdgeZoneConfig = EdgeZoneConfig(
    enabled = true,
    swipeAction = GestureAction.BACK,
    holdAction = GestureAction.LAST_APP,
    diagonalUpAction = GestureAction.NOTIFICATIONS,
    diagonalDownAction = GestureAction.QUICK_SETTINGS,
    heightPercent = 65f,
    widthDp = 24,
    verticalOffsetPercent = 15f
  ),
  val rightEdge: EdgeZoneConfig = EdgeZoneConfig(
    enabled = true,
    swipeAction = GestureAction.BACK,
    holdAction = GestureAction.RECENTS,
    diagonalUpAction = GestureAction.SCREENSHOT,
    diagonalDownAction = GestureAction.NOTIFICATIONS,
    heightPercent = 65f,
    widthDp = 24,
    verticalOffsetPercent = 15f
  ),
  val bottomBar: BottomBarConfig = BottomBarConfig(
    enabled = true,
    leftSectionSwipe = GestureAction.BACK,
    centerSectionSwipe = GestureAction.HOME,
    rightSectionSwipe = GestureAction.RECENTS,
    centerHoldAction = GestureAction.RECENTS,
    swipeAcrossAction = GestureAction.LAST_APP,
    heightDp = 36,
    offsetAbove3Buttons = false
  ),
  val visual: VisualWaveConfig = VisualWaveConfig(),
  val haptic: HapticConfig = HapticConfig(),
  val antiKillNotificationEnabled: Boolean = true,
  val autoRestoreOnUnlock: Boolean = true,
  val ecoBatteryMode: Boolean = true, // Ultra low CPU & battery optimization
  val hideSystemNavInApp: Boolean = true, // Automatically hide 3-button nav inside app
  val shakeToRescueEnabled: Boolean = true // Emergency shake-to-rescue if gestures freeze
)
