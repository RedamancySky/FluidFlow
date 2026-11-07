package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.components.EmergencyRescueDialog
import com.example.util.ChineseOemHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GesturePreferencesRepository
import com.example.model.AppGestureSettings
import com.example.model.GestureAction
import com.example.service.FluidGestureAccessibilityService
import com.example.ui.components.getActionIcon

@Composable
fun DashboardScreen(
  repository: GesturePreferencesRepository,
  onNavigateToPlayground: () -> Unit,
  onNavigateToZoneConfig: (zoneSide: String) -> Unit,
  onNavigateToOemOptimization: () -> Unit,
  onNavigateToHideNavBar: () -> Unit
) {
  val context = LocalContext.current
  val settings by repository.settings.collectAsState()
  val isServiceActive by FluidGestureAccessibilityService.isServiceConnected.collectAsState()
  val lastTriggeredAction by FluidGestureAccessibilityService.lastTriggeredAction.collectAsState()

  var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
  var isBatteryOptimized by remember { mutableStateOf(ChineseOemHelper.isIgnoringBatteryOptimizations(context)) }
  var showRescueDialog by remember { mutableStateOf(false) }

  if (showRescueDialog) {
    EmergencyRescueDialog(
      repository = repository,
      onDismiss = { showRescueDialog = false }
    )
  }

  // Re-check permissions periodically or on focus
  LaunchedEffect(Unit) {
    hasOverlayPermission = Settings.canDrawOverlays(context)
    isBatteryOptimized = ChineseOemHelper.isIgnoringBatteryOptimizations(context)
  }

  // Battery restriction is the single most common cause of HyperOS/MIUI silently killing the
  // gesture service hours after setup, so the "fully functional" badge must not go green without it.
  val isFullyFunctional = isServiceActive && hasOverlayPermission && isBatteryOptimized && settings.serviceEnabled

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Hero Header
    HeaderSection()

    Spacer(modifier = Modifier.height(16.dp))

    // Prominent Accessibility Activation Alert when not yet running
    if (!isServiceActive) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(18.dp))
          .testTag("accessibility_not_running_alert")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "CỬ CHỈ CHƯA CHẠY KHI THOÁT APP",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onErrorContainer
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Bạn chỉ mới thử được bên trong màn hình Sandbox. Để cử chỉ vuốt hoạt động trên màn hình chính và các ứng dụng khác khi thoát app, bạn BẮT BUỘC phải bật Dịch Vụ Trợ Năng cho FluidFlow.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
              }
              context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = MaterialTheme.colorScheme.onError
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("enable_accessibility_cta_button")
          ) {
            Text(text = "👉 Bật Dịch Vụ Trợ Năng Ngay (1 Bước)", fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Master Service Card
    MasterServiceCard(
      isEnabled = settings.serviceEnabled,
      isFullyFunctional = isFullyFunctional,
      onToggle = { enabled ->
        repository.setServiceEnabled(enabled)
        FluidGestureAccessibilityService.instance?.refreshOverlays()
      }
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Fail-Safe Emergency Rescue Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFFEF4444).copy(alpha = 0.08f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
        .testTag("dashboard_rescue_card")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444).copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Cơ Chế Cứu Hộ Khi Đơ Thao Tác",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Bấm 3 lần Âm lượng / Nguồn hoặc Lắc 3 lần",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = settings.shakeToRescueEnabled,
            onCheckedChange = { repository.setShakeToRescueEnabled(it) },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEF4444))
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Khi gặp sự cố mất điều hướng, bạn chỉ cần bấm 3 lần nút Âm lượng, 3 lần nút Nguồn, hoặc lắc máy 3 lần để mở ngay bảng khôi phục.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = { showRescueDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
          modifier = Modifier.fillMaxWidth().testTag("dashboard_test_rescue_button")
        ) {
          Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Mở Bảng Cứu Hộ Khẩn Cấp (Test)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Fix Overlapping 3-Button Nav Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (settings.bottomBar.offsetAbove3Buttons)
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = if (settings.bottomBar.offsetAbove3Buttons) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
          shape = RoundedCornerShape(18.dp)
        )
        .testTag("overlap_fix_card")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Nâng Vùng Vuốt Lên Trên 3 Phím (+48dp)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (settings.bottomBar.offsetAbove3Buttons)
                "Đang nâng vùng vuốt lên trên 3 phím ảo, không lo chạm nhầm!"
              else
                "Bật nếu máy bạn vẫn hiển thị thanh 3 phím để tránh bị đè.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = settings.bottomBar.offsetAbove3Buttons,
            onCheckedChange = { repository.setBottomOffsetAbove3Buttons(it) }
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = onNavigateToHideNavBar,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Xem cách ẩn hoàn toàn 3 phím ảo hệ thống", fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Last Triggered Action Pill (Live HUD)
    AnimatedVisibility(visible = lastTriggeredAction != null) {
      lastTriggeredAction?.let { action ->
        LiveActionBadge(action = action)
        Spacer(modifier = Modifier.height(14.dp))
      }
    }

    // Permission Checklist
    Text(
      text = "QUYỀN HỆ THỐNG CẦN THIẾT",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
      ),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    PermissionStatusCard(
      title = "Dịch Vụ Trợ Năng (Accessibility)",
      description = "Dùng để thực thi thao tác Back, Home, Đa nhiệm không cần phím ảo.",
      isGranted = isServiceActive,
      icon = Icons.Default.SettingsAccessibility,
      actionLabel = if (isServiceActive) "Đã Kích Hoạt" else "Mở Cài Đặt Trợ Năng",
      testTag = "perm_accessibility_card",
      onClick = {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      }
    )

    Spacer(modifier = Modifier.height(10.dp))

    PermissionStatusCard(
      title = "Vẽ Đè Lên Ứng Dụng (System Overlay)",
      description = "Cho phép viền cảm ứng nhận diện vuốt khi dùng các launcher bên thứ ba.",
      isGranted = hasOverlayPermission,
      icon = Icons.Default.Layers,
      actionLabel = if (hasOverlayPermission) "Đã Cho Phép" else "Cấp Quyền Vẽ Đè",
      testTag = "perm_overlay_card",
      onClick = {
        val intent = Intent(
          Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.parse("package:${context.packageName}")
        ).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      }
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Chinese OEM & Anti-Kill Optimization Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isBatteryOptimized)
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        else
          MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onNavigateToOemOptimization)
        .border(
          width = 1.dp,
          color = if (isBatteryOptimized) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
          shape = RoundedCornerShape(18.dp)
        )
        .testTag("oem_optimization_banner")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
              if (isBatteryOptimized) Color(0xFF10B981).copy(alpha = 0.2f)
              else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isBatteryOptimized) Icons.Default.Security else Icons.Default.BatteryAlert,
            contentDescription = null,
            tint = if (isBatteryOptimized) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Tối Ưu Máy Xiaomi, OPPO, Vivo, Realme",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (isBatteryOptimized) "Đã tắt hạn chế pin • Tiến trình được bảo vệ khi khóa"
            else "Cần cấu hình: Chống tự động đóng/crash ứng dụng khi khóa màn hình",
            style = MaterialTheme.typography.bodySmall,
            color = if (isBatteryOptimized) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Hide 3-Button Nav Bar Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onNavigateToHideNavBar)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
          shape = RoundedCornerShape(18.dp)
        )
        .testTag("hide_navbar_banner")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Ẩn 3 Nút Điều Hướng Khi Thoát App",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Cử chỉ vẫn chạy khi thoát • Xem 3 cách ẩn 3 nút toàn hệ thống",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Interactive Sandbox CTA Banner
    PlaygroundBannerCard(
      onOpenPlayground = onNavigateToPlayground
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Active Trigger Zones Summary
    Text(
      text = "VÙNG CỬ CHỈ ĐANG HOẠT ĐỘNG",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
      ),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    // Left Edge Card
    TriggerZoneSummaryCard(
      zoneTitle = "Viền Cạnh Trái (Left Edge)",
      swipeAction = settings.leftEdge.swipeAction,
      holdAction = settings.leftEdge.holdAction,
      enabled = settings.leftEdge.enabled,
      heightPercent = settings.leftEdge.heightPercent,
      onConfigure = { onNavigateToZoneConfig("LEFT") },
      testTag = "summary_left_edge"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Right Edge Card
    TriggerZoneSummaryCard(
      zoneTitle = "Viền Cạnh Phải (Right Edge)",
      swipeAction = settings.rightEdge.swipeAction,
      holdAction = settings.rightEdge.holdAction,
      enabled = settings.rightEdge.enabled,
      heightPercent = settings.rightEdge.heightPercent,
      onConfigure = { onNavigateToZoneConfig("RIGHT") },
      testTag = "summary_right_edge"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Bottom Bar Card
    TriggerZoneSummaryCard(
      zoneTitle = "Thanh Viền Đáy (Bottom Bar)",
      swipeAction = settings.bottomBar.centerSectionSwipe,
      holdAction = settings.bottomBar.centerHoldAction,
      enabled = settings.bottomBar.enabled,
      heightPercent = 100f,
      extraInfo = "Trái: ${settings.bottomBar.leftSectionSwipe.title} • Phải: ${settings.bottomBar.rightSectionSwipe.title}",
      onConfigure = { onNavigateToZoneConfig("BOTTOM") },
      testTag = "summary_bottom_bar"
    )

    Spacer(modifier = Modifier.height(30.dp))
  }
}

@Composable
private fun HeaderSection() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "FluidFlow Gestures",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = (-0.5).sp
        ),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Điều hướng mượt mà cho Launcher bên thứ ba",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(
              MaterialTheme.colorScheme.primary,
              MaterialTheme.colorScheme.secondary
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Gesture,
        contentDescription = "Logo",
        tint = Color.Black,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun MasterServiceCard(
  isEnabled: Boolean,
  isFullyFunctional: Boolean,
  onToggle: (Boolean) -> Unit
) {
  val statusColor by animateColorAsState(
    targetValue = if (isFullyFunctional) Color(0xFF10B981) else Color(0xFFF59E0B),
    label = "statusColor"
  )

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent,
        shape = RoundedCornerShape(24.dp)
      )
      .testTag("master_service_card")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isEnabled) statusColor else Color.Gray)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isEnabled) {
              if (isFullyFunctional) "Đang Hoạt Động" else "Cần Cấp Đủ Quyền"
            } else "Đã Tạm Dừng",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isEnabled) statusColor else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Điều Hướng Toàn Cục",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = if (isEnabled) "Cử chỉ vuốt viền trái, phải và đáy đã sẵn sàng"
          else "Bật công tắc để kích hoạt cử chỉ trên launcher",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Switch(
        checked = isEnabled,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.primary,
          checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
          uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
          uncheckedTrackColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.testTag("master_service_switch")
      )
    }
  }
}

@Composable
private fun LiveActionBadge(action: GestureAction) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        shape = RoundedCornerShape(14.dp)
      )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.ElectricBolt,
        contentDescription = "Triggered",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "Vừa kích hoạt: ",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = action.title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
private fun PermissionStatusCard(
  title: String,
  description: String,
  isGranted: Boolean,
  icon: ImageVector,
  actionLabel: String,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
              if (isGranted) Color(0xFF10B981).copy(alpha = 0.2f)
              else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = if (isGranted) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      if (!isGranted) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onClick,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = actionLabel, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
private fun PlaygroundBannerCard(onOpenPlayground: () -> Unit) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onOpenPlayground)
      .testTag("open_playground_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Thử cử chỉ",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Sandbox Thử Nghiệm Cử Chỉ",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Kéo thử viền sóng nước, căn chỉnh độ nhạy và rung phản hồi",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
private fun TriggerZoneSummaryCard(
  zoneTitle: String,
  swipeAction: GestureAction,
  holdAction: GestureAction,
  enabled: Boolean,
  heightPercent: Float,
  extraInfo: String? = null,
  onConfigure: () -> Unit,
  testTag: String
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onConfigure)
      .testTag(testTag)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (enabled) MaterialTheme.colorScheme.primary else Color.Gray)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = zoneTitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = "Chỉnh sửa",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        ActionMiniBadge(label = "Vuốt", action = swipeAction)
        ActionMiniBadge(label = "Vuốt & Giữ", action = holdAction)
      }

      if (extraInfo != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = extraInfo,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun ActionMiniBadge(label: String, action: GestureAction) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Icon(
      imageVector = getActionIcon(action),
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "$label: ${action.title.split("(").first().trim()}",
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
