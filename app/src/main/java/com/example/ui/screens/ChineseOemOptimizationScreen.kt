package com.example.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.service.FluidGestureAccessibilityService
import com.example.service.KeepAliveForegroundService
import com.example.util.ChineseOemHelper

@Composable
fun ChineseOemOptimizationScreen(
  repository: GesturePreferencesRepository,
  onNavigateBack: () -> Unit
) {
  BackHandler { onNavigateBack() }

  val context = LocalContext.current
  val settings by repository.settings.collectAsState()

  var isIgnoringBattery by remember { mutableStateOf(ChineseOemHelper.isIgnoringBatteryOptimizations(context)) }
  val brand = remember { ChineseOemHelper.getDeviceBrand() }
  val guide = remember { ChineseOemHelper.getOptimizationGuideForCurrentBrand() }

  LaunchedEffect(Unit) {
    isIgnoringBattery = ChineseOemHelper.isIgnoringBatteryOptimizations(context)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier.testTag("oem_opt_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Quay lại",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = "Chống Crash & Quản Lý Pin",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Tối ưu hóa đặc biệt cho Xiaomi, OPPO, Vivo, Realme",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Detected Device Banner
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(
            listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
              MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
          ),
          shape = RoundedCornerShape(20.dp)
        )
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
            imageVector = Icons.Default.Smartphone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "DÒNG MÁY PHÁT HIỆN ĐƯỢC",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = brand.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Battery Optimization Card
    Text(
      text = "QUẢN LÝ TIÊU THỤ PIN (BATTERY DOZE)",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                if (isIgnoringBattery) Color(0xFF10B981).copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isIgnoringBattery) Icons.Default.CheckCircle else Icons.Default.BatteryChargingFull,
              contentDescription = null,
              tint = if (isIgnoringBattery) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isIgnoringBattery) "Pin: Đã Tắt Tối Ưu Hóa (Không Giới Hạn)" else "Pin: Đang Bị Hệ Thống Hạn Chế",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (isIgnoringBattery) "Ứng dụng sẽ không bị tắt khi điện thoại tắt màn hình hoặc chuyển sang chế độ ngủ."
              else "Hệ điều hành có thể dừng dịch vụ khi khóa màn hình nếu chưa tắt tối ưu hóa pin.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            ChineseOemHelper.requestIgnoreBatteryOptimizations(context)
            // Re-check
            isIgnoringBattery = ChineseOemHelper.isIgnoringBatteryOptimizations(context)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isIgnoringBattery) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
            contentColor = if (isIgnoringBattery) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("request_battery_optimization_button")
        ) {
          Icon(
            imageVector = Icons.Default.Power,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isIgnoringBattery) "Kiểm Tra Lại Quyền Pin" else "Cấp Quyền Không Giới Hạn Pin (No Restrictions)",
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Anti-Kill & Keep-Alive Toggles
    Text(
      text = "CƠ CHẾ BẢO VỆ CHẠY NGẦM LIÊN TỤC",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        // Toggle Keep-Alive Notification
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Ghim Tiến Trình (Keep-Alive Service)",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Hiển thị thông báo nhỏ nhằm nâng mức ưu tiên của tiến trình (Foreground Service), ngăn chặn dọn RAM tự động khi khóa màn hình.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Switch(
            checked = settings.antiKillNotificationEnabled,
            onCheckedChange = { enabled ->
              repository.setAntiKillNotificationEnabled(enabled)
              if (enabled && settings.serviceEnabled) {
                KeepAliveForegroundService.start(context)
              } else {
                KeepAliveForegroundService.stop(context)
              }
            },
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("toggle_keep_alive_switch")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Auto Restore on Unlock
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Tự Động Phục Hồi Khi Mở Khóa",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Tự động kích hoạt lại các viền cảm ứng ngay lập tức khi bạn mở khóa điện thoại.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Switch(
            checked = settings.autoRestoreOnUnlock,
            onCheckedChange = { enabled ->
              repository.setAutoRestoreOnUnlock(enabled)
            },
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("toggle_auto_restore_switch")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 1-Tap Direct System Settings Launchers
    Text(
      text = "LỐI TẮT HỆ THỐNG CỦA HÃNG",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      OemActionCard(
        title = "Cài Đặt Tự Khởi Chạy (Autostart)",
        subtitle = "Cho phép ứng dụng tự động chạy khi khởi động máy và không bị đóng băng",
        icon = Icons.Default.Speed,
        onClick = { ChineseOemHelper.openAutostartSettings(context) }
      )

      OemActionCard(
        title = "Quản Lý Đóng Băng & Tiết Kiệm Pin Hãng",
        subtitle = "Truy cập trình quản lý ứng dụng ẩn của HyperOS / MIUI / ColorOS / Funtouch",
        icon = Icons.Default.Security,
        onClick = { ChineseOemHelper.openBatterySaverSettings(context) }
      )

      OemActionCard(
        title = "Thông Tin Ứng Dụng (Cấp Toàn Bộ Quyền)",
        subtitle = "Mở trang chi tiết trong Cài đặt hệ thống để cấp các quyền bổ sung",
        icon = Icons.Default.Smartphone,
        onClick = { ChineseOemHelper.openAppDetails(context) }
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Step-by-Step OEM Guide
    Text(
      text = "HƯỚNG DẪN CHI TIẾT THEO MÁY",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = guide.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        guide.steps.forEachIndexed { index, step ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
              text = step,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}

@Composable
private fun OemActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Mở",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
