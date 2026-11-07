package com.example.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import java.util.Locale

object ChineseOemHelper {

  enum class OemBrand(val displayName: String) {
    XIAOMI("Xiaomi / Redmi / POCO (HyperOS, MIUI)"),
    HUAWEI("Huawei / Honor (HarmonyOS, EMUI)"),
    OPPO("OPPO / Realme / OnePlus (ColorOS, Realme UI)"),
    VIVO("Vivo / iQOO (OriginOS, FuntouchOS)"),
    TRANSSION("Infinix / Tecno (XOS, HiOS)"),
    SAMSUNG("Samsung (One UI)"),
    GENERIC("Android Khác")
  }

  fun getDeviceBrand(): OemBrand {
    val manufacturer = Build.MANUFACTURER.lowercase(Locale.ROOT)
    val brand = Build.BRAND.lowercase(Locale.ROOT)

    return when {
      manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || brand.contains("poco") -> OemBrand.XIAOMI
      manufacturer.contains("huawei") || manufacturer.contains("honor") -> OemBrand.HUAWEI
      manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> OemBrand.OPPO
      manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> OemBrand.VIVO
      manufacturer.contains("transsion") || manufacturer.contains("infinix") || manufacturer.contains("tecno") -> OemBrand.TRANSSION
      manufacturer.contains("samsung") -> OemBrand.SAMSUNG
      else -> OemBrand.GENERIC
    }
  }

  fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
  }

  @SuppressLint("BatteryLife")
  fun requestIgnoreBatteryOptimizations(context: Context): Boolean {
    return try {
      val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:${context.packageName}")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      try {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  fun openAutostartSettings(context: Context): Boolean {
    val intentList = listOf(
      // Xiaomi / HyperOS / MIUI
      Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
      // Huawei / Honor
      Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
      Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
      // OPPO / Realme
      Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
      Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
      Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
      // Vivo / iQOO
      Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
      Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
      Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
      // Transsion (Infinix/Tecno)
      Intent().setComponent(ComponentName("com.transsion.phonemanager", "com.transsion.phonemanager.battery.BatteryCleanActivity"))
    )

    for (intent in intentList) {
      try {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return true
      } catch (_: Exception) {
        // try next
      }
    }

    // Fallback to app details
    return openAppDetails(context)
  }

  fun openBatterySaverSettings(context: Context): Boolean {
    val intentList = listOf(
      // Xiaomi No Restrictions
      Intent().setComponent(ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity")).apply {
        putExtra("package_name", context.packageName)
        putExtra("package_label", "FluidFlow")
      },
      // ColorOS / Realme battery freeze
      Intent().setComponent(ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelga.PowerConsumptionActivity")),
      // Vivo High Background Power
      Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.MainGuideActivity")),
      // Samsung Battery Optimization
      Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    )

    for (intent in intentList) {
      try {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return true
      } catch (_: Exception) {
        // try next
      }
    }

    return openAppDetails(context)
  }

  fun openAppDetails(context: Context): Boolean {
    return try {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      false
    }
  }

  data class OemOptimizationGuide(
    val title: String,
    val steps: List<String>
  )

  fun getOptimizationGuideForCurrentBrand(): OemOptimizationGuide {
    return when (getDeviceBrand()) {
      OemBrand.XIAOMI -> OemOptimizationGuide(
        title = "Tối ưu hóa HyperOS / MIUI (Xiaomi, Redmi, POCO)",
        steps = listOf(
          "Bật 'Tự khởi chạy' (Autostart) trong Bảo mật > Quản lý ứng dụng > Tự khởi chạy.",
          "Cài đặt Tiết kiệm pin: Chuyển từ 'Tiết kiệm pin MIUI' sang 'Không hạn chế' (No restrictions).",
          "Khóa ứng dụng trong màn hình Đa nhiệm: Mở giao diện đa nhiệm, vuốt giữ thẻ ứng dụng FluidFlow và nhấn vào biểu tượng Ổ Khóa.",
          "Tắt tính năng 'Tự động dọn RAM khi khóa màn hình' trong Bảo mật > Cài đặt Tăng tốc."
        )
      )
      OemBrand.OPPO -> OemOptimizationGuide(
        title = "Tối ưu hóa ColorOS / Realme UI (OPPO, Realme, OnePlus)",
        steps = listOf(
          "Cài đặt pin: Bật 'Cho phép hoạt động dưới nền' (Allow background activity).",
          "Bật 'Cho phép tự khởi động' (Auto-launch) trong Quản lý ứng dụng.",
          "Tắt chế độ đóng băng ứng dụng nhanh (Quick freeze) đối với FluidFlow.",
          "Mở Đa nhiệm, nhấn dấu 3 chấm góc thẻ FluidFlow và chọn 'Khóa' (Lock)."
        )
      )
      OemBrand.VIVO -> OemOptimizationGuide(
        title = "Tối ưu hóa OriginOS / FuntouchOS (Vivo, iQOO)",
        steps = listOf(
          "Vào iManager > Quản lý ứng dụng > Quản lý tự khởi động > Bật FluidFlow.",
          "Vào Cài đặt Pin > Tiêu thụ pin ở chế độ nền cao > Cho phép ứng dụng tiếp tục chạy.",
          "Trong màn hình Đa nhiệm, kéo thẻ FluidFlow xuống để hiện ổ khóa màu vàng."
        )
      )
      OemBrand.HUAWEI -> OemOptimizationGuide(
        title = "Tối ưu hóa EMUI / HarmonyOS (Huawei, Honor)",
        steps = listOf(
          "Vào Quản lý pin > Khởi chạy ứng dụng > Tắt 'Tự động quản lý' cho FluidFlow.",
          "Bật cả 3 mục thủ công: 'Tự khởi chạy', 'Khởi chạy liên kết' và 'Chạy ngầm'.",
          "Khóa ứng dụng trong màn hình ứng dụng gần đây."
        )
      )
      else -> OemOptimizationGuide(
        title = "Tối ưu hóa thiết bị Android",
        steps = listOf(
          "Tắt tối ưu hóa pin: Chọn 'Không hạn chế' (Unrestricted) trong mục Pin của ứng dụng.",
          "Cấp quyền 'Dịch vụ Trợ năng' và 'Hiển thị trên các ứng dụng khác'.",
          "Bật 'Chế độ Chạy ngầm liên tục' (Keep-Alive Service) trong ứng dụng để ghim vào thanh thông báo.",
          "Khóa ứng dụng trong màn hình Đa nhiệm để không bị quét dọn RAM."
        )
      )
    }
  }
}
