package com.example.model

import android.accessibilityservice.AccessibilityService

enum class GestureAction(
  val id: String,
  val title: String,
  val description: String,
  val globalActionId: Int? = null,
  val isCustomAction: Boolean = false
) {
  BACK(
    id = "BACK",
    title = "Quay lại (Back)",
    description = "Trở về màn hình trước đó",
    globalActionId = AccessibilityService.GLOBAL_ACTION_BACK
  ),
  HOME(
    id = "HOME",
    title = "Màn hình chính (Home)",
    description = "Trở về màn hình chính của launcher",
    globalActionId = AccessibilityService.GLOBAL_ACTION_HOME
  ),
  RECENTS(
    id = "RECENTS",
    title = "Đa nhiệm (Recent Apps)",
    description = "Mở danh sách các ứng dụng vừa dùng",
    globalActionId = AccessibilityService.GLOBAL_ACTION_RECENTS
  ),
  LAST_APP(
    id = "LAST_APP",
    title = "Chuyển app trước (Quick Switch)",
    description = "Chuyển đổi tức thì giữa 2 ứng dụng gần nhất",
    globalActionId = AccessibilityService.GLOBAL_ACTION_RECENTS,
    isCustomAction = true
  ),
  NOTIFICATIONS(
    id = "NOTIFICATIONS",
    title = "Bảng thông báo (Notifications)",
    description = "Kéo bảng trung tâm thông báo xuống",
    globalActionId = AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
  ),
  QUICK_SETTINGS(
    id = "QUICK_SETTINGS",
    title = "Cài đặt nhanh (Quick Settings)",
    description = "Mở bảng công tắc cài đặt nhanh",
    globalActionId = AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS
  ),
  SCREENSHOT(
    id = "SCREENSHOT",
    title = "Chụp màn hình (Screenshot)",
    description = "Chụp lại nội dung màn hình hiện tại",
    globalActionId = 9 // AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT (API 28+)
  ),
  LOCK_SCREEN(
    id = "LOCK_SCREEN",
    title = "Khóa màn hình (Lock Screen)",
    description = "Tắt và khóa màn hình thiết bị",
    globalActionId = 8 // AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN (API 28+)
  ),
  NONE(
    id = "NONE",
    title = "Không hành động",
    description = "Vô hiệu hóa cử chỉ này"
  );

  companion object {
    fun fromId(id: String): GestureAction {
      return entries.find { it.id == id } ?: NONE
    }
  }
}
