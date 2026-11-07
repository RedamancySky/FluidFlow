package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SettingsApplications
import com.example.ui.components.EmergencyRescueDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GesturePreferencesRepository
import com.example.util.ChineseOemHelper
import com.example.util.SystemNavBarManager

@Composable
fun HideNavBarScreen(
  repository: GesturePreferencesRepository,
  onNavigateBack: () -> Unit
) {
  BackHandler { onNavigateBack() }

  val context = LocalContext.current
  val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

  var selectedBrand by remember { mutableStateOf(ChineseOemHelper.getDeviceBrand()) }
  var hasSecurePerm by remember { mutableStateOf(SystemNavBarManager.hasSecureSettingsPermission(context)) }
  var isHiddenGlobally by remember { mutableStateOf(SystemNavBarManager.isSystemNavBarHiddenGlobally(context)) }
  val isRootAvailable = remember { SystemNavBarManager.isRootAvailable() }
  val settings by repository.settings.collectAsState()
  var showRescueModal by remember { mutableStateOf(false) }
  var adbMethodTab by remember { mutableStateOf(0) }

  if (showRescueModal) {
    EmergencyRescueDialog(
      repository = repository,
      onDismiss = { showRescueModal = false }
    )
  }

  LaunchedEffect(Unit) {
    hasSecurePerm = SystemNavBarManager.hasSecureSettingsPermission(context)
    isHiddenGlobally = SystemNavBarManager.isSystemNavBarHiddenGlobally(context)
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
        modifier = Modifier.testTag("hide_navbar_back_button")
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
          text = "Ẩn Thanh Điều Hướng 3 Nút",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Ẩn hoàn toàn khi thoát ứng dụng ra launcher",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Direct Answer FAQ Banner
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "KHI THOÁT ỨNG DỤNG, CỬ CHỈ CÓ CHẠY?",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "• Cử chỉ vuốt FluidFlow: Chắc chắn HOẠT ĐỘNG 100% ở mọi ứng dụng và màn hình chính của launcher bên thứ ba nhờ Dịch Vụ Trợ Năng cấp hệ thống.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "• Ẩn 3 phím ảo khi thoát app: Theo chính sách bảo mật của Android, để ẩn 3 phím ảo trên toàn hệ thống bạn có thể chọn 1 trong các cách tiện lợi bên dưới.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Emergency Fail-Safe Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFFEF4444).copy(alpha = 0.12f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        .testTag("rescue_failsafe_card")
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "CƠ CHẾ CỨU HỘ AN TOÀN (FAIL-SAFE)",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
              color = Color(0xFFEF4444)
            )
            Text(
              text = "Không bao giờ lo bị kẹt hay đơ thao tác",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Nếu bạn ẩn 3 phím mà lỡ bị đơ cử chỉ hoặc không thể Back/Home, bạn có thể gọi bảng Cứu Hộ Khẩn Cấp bất cứ lúc nào bằng 4 cách:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "1. Bấm 3 lần phím Âm lượng (Tăng hoặc Giảm liên tiếp)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "2. Bấm 3 lần phím Nguồn (Tắt/Bật màn hình liên tiếp)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "3. Lắc mạnh điện thoại 3 lần (Cảm biến gia tốc)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "4. Bấm nút '🆘 Cứu Hộ' hoặc 'Khôi Phục 3 Phím' trên thanh thông báo", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
          onClick = { showRescueModal = true },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFEF4444)
          ),
          modifier = Modifier.fillMaxWidth().testTag("test_rescue_button")
        ) {
          Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Thử Kích Hoạt Bảng Cứu Hộ Ngay", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Global Switch (if permission is granted)
    AnimatedVisibility(visible = hasSecurePerm || isRootAvailable) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp)
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Công Tắc Ẩn 3 Phím Toàn Hệ Thống",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (isHiddenGlobally) "Đang ẩn 3 nút trên toàn bộ máy (Immersive Mode)"
              else "Gạt bật để ẩn 3 phím ảo ở mọi ứng dụng và launcher",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = isHiddenGlobally,
            onCheckedChange = { hide ->
              val success = SystemNavBarManager.setGlobalNavBarHidden(context, hide)
              if (success) {
                isHiddenGlobally = hide
                Toast.makeText(context, if (hide) "Đã ẩn 3 phím hệ thống!" else "Đã khôi phục 3 phím!", Toast.LENGTH_SHORT).show()
              }
            },
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
          )
        }
      }
    }

    // Instant Solution (No root/ADB needed)
    Text(
      text = "GIẢI PHÁP TỨC THÌ (0 GIÂY - KHÔNG CẦN CÀI ĐẶT)",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (settings.bottomBar.offsetAbove3Buttons)
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Nâng Vùng Vuốt Lên Trên 3 Phím (+48dp)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (settings.bottomBar.offsetAbove3Buttons)
              "Đang nâng vùng vuốt lên trên thanh 3 nút. Bạn có thể vuốt Home, Back, Đa nhiệm mà không sợ bị đè!"
            else
              "Gạt bật để vùng vuốt đáy tự động nâng lên trên 3 phím Back, Home, Recents của máy, không còn bị đè hay chạm nhầm.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
          checked = settings.bottomBar.offsetAbove3Buttons,
          onCheckedChange = { repository.setBottomOffsetAbove3Buttons(it) },
          colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Method 1: System Settings (Recommended - 0s)
    Text(
      text = "CÁCH 1: DÙNG CÀI ĐẶT ĐIỀU HƯỚNG CỦA MÁY (KHUYÊN DÙNG)",
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
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Smartphone,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Không Cần Máy Tính Hay ADB",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Chuyển điện thoại sang cử chỉ để tự động ẩn 3 phím",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Chọn dòng máy của bạn:",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Brand Selector Chips
        val brands = listOf(
          ChineseOemHelper.OemBrand.XIAOMI to "Xiaomi / POCO",
          ChineseOemHelper.OemBrand.SAMSUNG to "Samsung",
          ChineseOemHelper.OemBrand.OPPO to "OPPO / Realme",
          ChineseOemHelper.OemBrand.VIVO to "Vivo / iQOO",
          ChineseOemHelper.OemBrand.HUAWEI to "Huawei / Honor",
          ChineseOemHelper.OemBrand.GENERIC to "Android Gốc / Pixel"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          brands.forEach { (brand, label) ->
            FilterChip(
              selected = selectedBrand == brand,
              onClick = { selectedBrand = brand },
              label = { Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp)) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guide Path Highlight Box
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AltRoute,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ĐƯỜNG DẪN CÀI ĐẶT TRÊN MÁY BẠN:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = SystemNavBarManager.getNavigationGuidePath(selectedBrand),
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Button: Open System Navigation Settings
        Button(
          onClick = {
            val opened = SystemNavBarManager.openSystemNavigationSettings(context, selectedBrand)
            if (opened) {
              Toast.makeText(context, "Đang mở Cài đặt Điều hướng...", Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(context, "Vui lòng làm theo đường dẫn cài đặt phía trên", Toast.LENGTH_LONG).show()
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("open_system_nav_settings_button")
        ) {
          Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Mở Cài Đặt Điều Hướng / Cử Chỉ", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Shortcut Button (Home screen or Display settings)
        OutlinedButton(
          onClick = {
            if (selectedBrand == ChineseOemHelper.OemBrand.XIAOMI) {
              SystemNavBarManager.openHomeScreenSettings(context)
            } else {
              SystemNavBarManager.openDisplaySettings(context)
            }
          },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("open_secondary_settings_button")
        ) {
          Icon(
            imageVector = if (selectedBrand == ChineseOemHelper.OemBrand.XIAOMI) Icons.Default.Home else Icons.Default.Smartphone,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (selectedBrand == ChineseOemHelper.OemBrand.XIAOMI) "Mở Cài Đặt Màn Hình Chính (Home Screen)"
            else "Mở Cài Đặt Màn Hình (Display Settings)",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "💡 Lưu ý: Trên một số phiên bản ROM MIUI/HyperOS hoặc ColorOS, hệ thống bảo mật có thể hạn chế mở thẳng trang con. Bạn chỉ cần nhấn nút 'Mở Cài Đặt Màn Hình Chính' hoặc theo đường dẫn trên là sẽ thấy ngay mục 'Điều hướng hệ thống'!",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Method 2: Shizuku / Termux / ADB
    Text(
      text = "CÁCH 2: DÙNG SHIZUKU & TERMUX (KHÔNG CẦN MÁY TÍNH) HOẶC ADB",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Direct Answer: Is ADB Mandatory?
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CÓ BẮT BUỘC DÙNG ADB KHÔNG?",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "• KHÔNG BẮT BUỘC: Nếu không muốn gõ lệnh, bạn có thể dùng 'Cách 1' (chuyển sang cử chỉ trong Cài đặt máy) hoặc dùng 'Giải pháp tức thì' (Nâng viền vuốt lên trên 3 phím +48dp) để không bị chạm đè, dùng ngay 0 giây!\n" +
              "• CHỈ DÙNG KHI: Bạn muốn ẩn triệt để 100% thanh 3 phím đen dưới đáy trên các launcher ngoài (Nova, Smart, Niagara...). Bạn hoàn toàn có thể làm trực tiếp trên điện thoại bằng Shizuku + Termux mà không cần máy tính PC!",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Selector: Phone (Shizuku/Termux) vs PC (ADB Cable)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FilterChip(
        selected = adbMethodTab == 0,
        onClick = { adbMethodTab = 0 },
        leadingIcon = {
          Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
        },
        label = { Text("📱 Trên Điện Thoại (Shizuku + Termux)", fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.weight(1f)
      )

      FilterChip(
        selected = adbMethodTab == 1,
        onClick = { adbMethodTab = 1 },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Computer, contentDescription = null, modifier = Modifier.size(16.dp))
        },
        label = { Text("💻 Dùng Máy Tính (ADB USB)", fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (adbMethodTab == 0) {
      // TAB 0: SHIZUKU + TERMUX GUIDE (NO PC REQUIRED)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Status indicator
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                  if (hasSecurePerm) Color(0xFF10B981).copy(alpha = 0.2f)
                  else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (hasSecurePerm) Icons.Default.CheckCircle else Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = if (hasSecurePerm) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (hasSecurePerm) "QUYỀN HỆ THỐNG: ĐÃ ĐƯỢC CẤP THÀNH CÔNG!" else "HƯỚNG DẪN SHIZUKU + TERMUX",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (hasSecurePerm) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (hasSecurePerm) "Bạn có thể gạt công tắc phía trên để ẩn 3 phím." else "Thực hiện lần lượt 4 bước đơn giản dưới đây:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Step 1: Developer Options & Wireless Debugging
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "BƯỚC 1: Bật Tùy Chọn Nhà Phát Triển & Gỡ Lỗi Không Dây",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "1. Mở Cài đặt > Giới thiệu điện thoại > Nhấn liên tục 7 lần vào 'Số hiệu bản dựng' (hoặc 'Phiên bản MIUI/HyperOS') để mở khóa chế độ nhà phát triển.\n" +
                    "2. Vào 'Tùy chọn nhà phát triển' > Bật 'Gỡ lỗi USB' và 'Gỡ lỗi không dây' (Wireless Debugging).\n" +
                    "⚠️ Dòng Xiaomi/POCO: Bắt buộc bật thêm 'Cài đặt qua USB' và 'Gỡ lỗi USB (Cài đặt bảo mật)'.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  val opened = SystemNavBarManager.openDeveloperSettings(context)
                  if (!opened) Toast.makeText(context, "Vui lòng vào Cài đặt để mở Tùy chọn nhà phát triển", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("open_dev_settings_button")
              ) {
                Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Mở Tùy Chọn Nhà Phát Triển", fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Step 2: Install and Start Shizuku
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "BƯỚC 2: Cài Đặt & Ghép Nối Shizuku (Không Cần PC)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "1. Cài đặt Shizuku từ Google Play Store (hoàn toàn miễn phí).\n" +
                    "2. Kết nối điện thoại với mạng Wi-Fi.\n" +
                    "3. Mở Shizuku > Tại mục 'Khởi động qua gỡ lỗi không dây', nhấn 'Ghép nối' (Pairing) > Nhập mã 6 chữ số từ thông báo hệ thống.\n" +
                    "4. Nhấn 'Khởi động' (Start) trong Shizuku -> Thấy 'Shizuku đang chạy' là xong!",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedButton(
                onClick = { SystemNavBarManager.openShizukuOnPlayStore(context) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("open_shizuku_play_store_button")
              ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Mở Shizuku Trên Google Play Store", fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Step 3: Install Termux & Setup Rish
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "BƯỚC 3: Cài Đặt Termux & Kích Hoạt Rish",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "1. Cài đặt ứng dụng Termux (khuyên dùng bản F-Droid mới nhất).\n" +
                    "2. Trong ứng dụng Shizuku: Bấm 'Sử dụng Shizuku trong ứng dụng đầu cuối' > Bấm 'Xuất tệp rish' để cài đặt vào Termux.\n" +
                    "3. Mở Termux, gõ lệnh: rish rồi nhấn phím Enter trên bàn phím.\n" +
                    "4. Chọn 'Cho phép mọi lúc' (Allow all the time) khi Shizuku hiện thông báo xin cấp quyền.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedButton(
                onClick = { SystemNavBarManager.openTermuxDownload(context) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("download_termux_button")
              ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Tải Termux (Bản F-Droid Mới Nhất)", fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Step 4: Run Commands in Termux
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "BƯỚC 4: Chạy Lần Lượt Các Lệnh Trong Termux (Sau khi gõ rish)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "💡 Lưu ý: Khi ở trong môi trường rish của Termux, bạn KHÔNG cần gõ 'adb shell', chỉ cần gõ trực tiếp các lệnh dưới:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Command 1: Grant Secure Settings (Recommended)
              val cmd1 = SystemNavBarManager.getInternalGrantPermissionCommand(context)
              Text(
                text = "Lệnh 1 (Khuyên dùng nhất - Chạy 1 lần duy nhất):",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00E5FF)
              )
              Text(
                text = "Cấp quyền cho FluidFlow tự động bật/tắt ẩn 3 phím bằng công tắc trong app mãi mãi, sau này không bao giờ phải chạy lệnh lại nữa!",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF030712))
                  .padding(10.dp)
              ) {
                Text(text = cmd1, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF00E5FF))
              }
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedButton(
                onClick = {
                  val clip = ClipData.newPlainText("Command 1", cmd1)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Đã sao chép Lệnh 1 (Cấp quyền)!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("copy_cmd1_button")
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sao Chép Lệnh 1 (Cấp Quyền 1 Lần)", fontSize = 11.5.sp)
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Command 2: Direct Hide
              val cmd2 = SystemNavBarManager.getInternalHideCommand()
              Text(
                text = "Lệnh 2 (Lệnh ẩn trực tiếp thanh 3 phím toàn màn hình):",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF10B981)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF030712))
                  .padding(10.dp)
              ) {
                Text(text = cmd2, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF10B981))
              }
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedButton(
                onClick = {
                  val clip = ClipData.newPlainText("Command 2", cmd2)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Đã sao chép Lệnh 2 (Ẩn 3 phím)!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("copy_cmd2_button")
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sao Chép Lệnh 2 (Ẩn 3 Phím Trực Tiếp)", fontSize = 11.5.sp)
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Command 3: Direct Restore
              val cmd3 = SystemNavBarManager.getInternalRestoreCommand()
              Text(
                text = "Lệnh 3 (Lệnh khôi phục lại 3 phím mặc định):",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFF59E0B)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF030712))
                  .padding(10.dp)
              ) {
                Text(text = cmd3, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFFF59E0B))
              }
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedButton(
                onClick = {
                  val clip = ClipData.newPlainText("Command 3", cmd3)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Đã sao chép Lệnh 3 (Khôi phục)!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("copy_cmd3_button")
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sao Chép Lệnh 3 (Khôi Phục 3 Phím)", fontSize = 11.5.sp)
              }
            }
          }
        }
      }
    } else {
      // TAB 1: PC ADB GUIDE
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
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Computer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(text = "HƯỚNG DẪN CẤP QUYỀN QUA MÁY TÍNH (PC)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
              Text(text = "Dành cho người có máy tính và cáp kết nối USB", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "1. Bật 'Gỡ lỗi USB' trong Tùy chọn nhà phát triển trên điện thoại.\n" +
                "2. Cắm cáp kết nối điện thoại với máy tính, mở Command Prompt hoặc PowerShell.\n" +
                "3. Chạy lần lượt các câu lệnh sau:",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // PC ADB Command 1
          val pcAdb1 = SystemNavBarManager.getAdbGrantPermissionCommand(context)
          Text(text = "Lệnh 1 (Cấp quyền 1 lần cho FluidFlow):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF00E5FF))
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF030712)).padding(10.dp)) {
            Text(text = pcAdb1, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF00E5FF))
          }
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = {
              val clip = ClipData.newPlainText("PC ADB Cmd 1", pcAdb1)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Đã sao chép lệnh ADB cấp quyền!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Sao Chép Lệnh ADB Cấp Quyền", fontSize = 11.5.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // PC ADB Command 2
          val pcAdb2 = SystemNavBarManager.getAdbDirectHideCommand()
          Text(text = "Lệnh 2 (Ẩn 3 phím trực tiếp bằng ADB):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF10B981))
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF030712)).padding(10.dp)) {
            Text(text = pcAdb2, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF10B981))
          }
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = {
              val clip = ClipData.newPlainText("PC ADB Cmd 2", pcAdb2)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Đã sao chép lệnh ADB ẩn trực tiếp!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Sao Chép Lệnh ADB Ẩn Trực Tiếp", fontSize = 11.5.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // PC ADB Command 3
          val pcAdb3 = SystemNavBarManager.getAdbRestoreCommand()
          Text(text = "Lệnh 3 (Khôi phục lại 3 phím bằng ADB):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFFF59E0B))
          Spacer(modifier = Modifier.height(4.dp))
          Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF030712)).padding(10.dp)) {
            Text(text = pcAdb3, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFFF59E0B))
          }
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedButton(
            onClick = {
              val clip = ClipData.newPlainText("PC ADB Cmd 3", pcAdb3)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Đã sao chép lệnh ADB khôi phục!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Sao Chép Lệnh ADB Khôi Phục", fontSize = 11.5.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Highlighted FAQ on ADB Conflict
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CÓ BỊ XUNG ĐỘT KHI VÀO CÀI ĐẶT BẬT LẠI?",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = Color(0xFF10B981)
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "• HOÀN TOÀN KHÔNG BỊ XUNG ĐỘT HOẶC LỖI MÁY:",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "1. Lệnh 'policy_control' chỉ là cờ hiển thị giao diện (Display Policy) của Android, hoàn toàn không sửa đổi file hệ thống hay can thiệp vào ROM.\n" +
              "2. Khi bạn vào Cài đặt điện thoại bật lại 3 phím hoặc chuyển sang cử chỉ gốc, Hệ thống Android (SystemUI) sẽ tự động nạp lại giao diện bình thường.\n" +
              "3. Bạn có thể khôi phục 3 phím ngay lập tức bằng Nút 'Khôi Phục 3 Phím' trong app hoặc câu lệnh số 3 phía trên bất cứ lúc nào.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Method 3: Bottom Edge Overlap Mode
    Text(
      text = "CÁCH 3: CHẾ ĐỘ PHỦ ĐÈ CẢM ỨNG ĐÁY",
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
        Text(
          text = "Nếu bạn vẫn để thanh 3 phím hiện, vùng cảm ứng viền đáy của FluidFlow sẽ nằm đè lên phía trước để ưu tiên nhận diện thao tác vuốt trước khi hệ thống bấm vào 3 phím.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}
