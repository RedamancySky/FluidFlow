package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GesturePreferencesRepository
import com.example.service.FluidGestureAccessibilityService
import com.example.util.SystemNavBarManager
import kotlinx.coroutines.launch

@Composable
fun EmergencyRescueDialog(
  repository: GesturePreferencesRepository,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .testTag("emergency_rescue_dialog"),
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("emergency_rescue_close_button")
      ) {
        Text("Đã Ổn, Đóng Hộp Thoại")
      }
    },
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "CỨU HỘ KHẨN CẤP",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFEF4444)
          )
          Text(
            text = "Khôi phục điều hướng khi bị đơ thao tác",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Explanatory badge
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "⚡ Đã kích hoạt cơ chế Fail-Safe",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Bạn có thể dùng nút Âm lượng (3 lần), Nút Nguồn (3 lần), Lắc máy hoặc thông báo để mở bảng này bất cứ khi nào gặp trục trặc.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "CHỌN THAO TÁC XỬ LÝ NHANH:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 1: Restore 3-button nav immediately
        RescueActionCard(
          icon = Icons.Default.Undo,
          iconTint = Color(0xFF10B981),
          title = "Khôi phục thanh 3 phím gốc",
          subtitle = "Bỏ ẩn ADB & hiện lại 3 phím ảo Back, Home, Đa nhiệm ngay lập tức",
          onClick = {
            val restored = SystemNavBarManager.setGlobalNavBarHidden(context, false)
            SystemNavBarManager.openSystemNavigationSettings(context)
            Toast.makeText(
              context,
              if (restored) "Đã gửi lệnh khôi phục 3 phím!" else "Đang mở Cài đặt Điều hướng để bật lại 3 phím",
              Toast.LENGTH_SHORT
            ).show()
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 2: Offset bottom above 3 buttons
        RescueActionCard(
          icon = Icons.Default.ArrowUpward,
          iconTint = Color(0xFF00E5FF),
          title = "Nâng viền vuốt lên trên 3 phím (+48dp)",
          subtitle = "Tránh xung đột: Dùng cả 3 phím máy lẫn cử chỉ vuốt mà không lo bị đè",
          onClick = {
            coroutineScope.launch {
              repository.setBottomOffsetAbove3Buttons(true)
              FluidGestureAccessibilityService.instance?.refreshOverlays()
              Toast.makeText(context, "Đã nâng viền cảm ứng lên trên thanh 3 phím!", Toast.LENGTH_SHORT).show()
            }
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 3: Restart overlay service
        RescueActionCard(
          icon = Icons.Default.Refresh,
          iconTint = Color(0xFFF59E0B),
          title = "Khởi động lại vùng cảm ứng",
          subtitle = "Làm mới lại các lớp viền cử chỉ Trái, Phải và Đáy nếu bị đơ cảm ứng",
          onClick = {
            FluidGestureAccessibilityService.instance?.refreshOverlays()
            Toast.makeText(context, "Đã làm mới lại các viền cử chỉ!", Toast.LENGTH_SHORT).show()
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 4: Open system nav settings
        RescueActionCard(
          icon = Icons.Default.Settings,
          iconTint = Color(0xFF8B5CF6),
          title = "Mở cài đặt điều hướng máy",
          subtitle = "Truy cập phần cài đặt Điều hướng/Cử chỉ trong Cài đặt hệ thống",
          onClick = {
            val opened = SystemNavBarManager.openSystemNavigationSettings(context)
            if (!opened) {
              SystemNavBarManager.openHomeScreenSettings(context)
            }
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action 5: Temporarily stop service
        RescueActionCard(
          icon = Icons.Default.PowerSettingsNew,
          iconTint = Color(0xFFEF4444),
          title = "Tạm tắt dịch vụ FluidFlow",
          subtitle = "Tắt mọi viền cảm ứng cử chỉ để trả máy về trạng thái ban đầu",
          onClick = {
            coroutineScope.launch {
              repository.setServiceEnabled(false)
              FluidGestureAccessibilityService.instance?.removeOverlays()
              Toast.makeText(context, "Đã tạm dừng viền cử chỉ FluidFlow", Toast.LENGTH_SHORT).show()
              onDismiss()
            }
          }
        )
      }
    }
  )
}

@Composable
private fun RescueActionCard(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(iconTint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
