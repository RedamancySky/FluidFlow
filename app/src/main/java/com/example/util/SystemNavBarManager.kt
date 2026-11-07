package com.example.util

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import java.io.DataOutputStream

object SystemNavBarManager {

  fun hasSecureSettingsPermission(context: Context): Boolean {
    return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
  }

  fun isSystemNavBarHiddenGlobally(context: Context): Boolean {
    return try {
      val policy = Settings.Global.getString(context.contentResolver, "policy_control")
      policy != null && policy.contains("immersive.navigation")
    } catch (_: Exception) {
      false
    }
  }

  fun setGlobalNavBarHidden(context: Context, hide: Boolean): Boolean {
    val policyValue = if (hide) "immersive.navigation=*" else ""

    // Try standard Secure Settings if granted
    if (hasSecureSettingsPermission(context)) {
      try {
        Settings.Global.putString(context.contentResolver, "policy_control", policyValue)
        return true
      } catch (_: Exception) {
      }
    }

    // Try Root if device is rooted
    if (isRootAvailable()) {
      try {
        val process = Runtime.getRuntime().exec("su")
        DataOutputStream(process.outputStream).use { os ->
          os.writeBytes("settings put global policy_control \"$policyValue\"\n")
          os.writeBytes("exit\n")
          os.flush()
        }
        process.waitFor()
        return true
      } catch (_: Exception) {
      }
    }

    return false
  }

  fun isRootAvailable(): Boolean {
    return try {
      val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
      process.waitFor() == 0
    } catch (_: Exception) {
      false
    }
  }

  fun getAdbGrantPermissionCommand(context: Context): String {
    return "adb shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
  }

  fun getAdbDirectHideCommand(): String {
    return "adb shell settings put global policy_control immersive.navigation=*"
  }

  fun getAdbRestoreCommand(): String {
    return "adb shell settings put global policy_control \"\""
  }

  fun openSystemNavigationSettings(context: Context, specificBrand: ChineseOemHelper.OemBrand? = null): Boolean {
    val brand = specificBrand ?: ChineseOemHelper.getDeviceBrand()

    val intentList = mutableListOf<Intent>()

    when (brand) {
      ChineseOemHelper.OemBrand.XIAOMI -> {
        // Xiaomi / HyperOS / MIUI Full screen gestures and home screen navigation
        intentList.add(Intent("android.settings.FULL_SCREEN_DISPLAY_SETTINGS"))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$FullScreenDisplaySettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$FullDisplaySettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.FullDisplaySettings")))
        intentList.add(Intent().setComponent(ComponentName("com.miui.home", "com.miui.home.launcher.gesture.GestureNavigationSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.miui.home", "com.miui.home.launcher.allapps.AllAppsSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$SpecialFeatureSettingsActivity")))
      }
      ChineseOemHelper.OemBrand.SAMSUNG -> {
        // Samsung One UI Navigation Bar
        intentList.add(Intent("com.samsung.android.settings.NAVIGATION_BAR_SETTINGS"))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$NavigationBarSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.samsung.android.settings.navigationbar.NavigationBarSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.samsung.android.app.cocktailbarservice", "com.samsung.android.app.cocktailbarservice.settings.CocktailBarSettingsActivity")))
      }
      ChineseOemHelper.OemBrand.OPPO -> {
        // OPPO ColorOS / Realme UI / OnePlus OxygenOS
        intentList.add(Intent("com.coloros.navigation.MODE_SETTINGS"))
        intentList.add(Intent().setComponent(ComponentName("com.oplus.gesture", "com.oplus.gesture.NavigationModeActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.coloros.smartdrive", "com.coloros.smartdrive.navigation.NavigationActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.oplus.settings.feature.othersettings.navigationbar.NavigationBarSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.navigation.NavigationActivity")))
      }
      ChineseOemHelper.OemBrand.VIVO -> {
        // Vivo OriginOS / FuntouchOS
        intentList.add(Intent("com.vivo.settings.NAVIGATION_SETTINGS"))
        intentList.add(Intent().setComponent(ComponentName("com.vivo.settings", "com.vivo.settings.navigation.NavigationSettingActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.vivo.upslide", "com.vivo.upslide.navigation.NavigationActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.vivo.settings.NavigationSettingActivity")))
      }
      ChineseOemHelper.OemBrand.HUAWEI -> {
        // Huawei HarmonyOS / EMUI
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$SystemNavigationSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.huawei.settings.navigation.SystemNavigationSettingsActivity")))
        intentList.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.navigation.SystemNavigationActivity")))
      }
      else -> {
        // Stock Android / Google Pixel / Motorola / Others
        intentList.add(Intent("android.settings.SYSTEM_NAVIGATION_SETTINGS"))
        intentList.add(Intent("android.settings.GESTURE_NAVIGATION_SETTINGS"))
        intentList.add(Intent("android.settings.GESTURE_SETTINGS"))
        intentList.add(Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.SubSettings")).apply {
          putExtra(":settings:show_fragment", "com.android.settings.gestures.SystemNavigationGestureSettings")
        })
      }
    }

    // Direct fallbacks for standard gesture navigation settings
    intentList.add(Intent("android.settings.SYSTEM_NAVIGATION_SETTINGS"))
    intentList.add(Intent("android.settings.GESTURE_NAVIGATION_SETTINGS"))
    intentList.add(Intent(Settings.ACTION_DISPLAY_SETTINGS))

    for (intent in intentList) {
      try {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return true
      } catch (_: Exception) {
      }
    }

    return false
  }

  fun openHomeScreenSettings(context: Context): Boolean {
    val intentList = listOf(
      // Xiaomi Home Screen
      Intent().setComponent(ComponentName("com.miui.home", "com.miui.home.launcher.allapps.AllAppsSettingsActivity")),
      // Samsung Home Screen
      Intent().setComponent(ComponentName("com.sec.android.app.launcher", "com.sec.android.app.launcher.settings.HomeSettingsActivity")),
      // ColorOS Home Screen
      Intent().setComponent(ComponentName("com.android.launcher", "com.android.launcher.settings.HomeSettingsActivity")),
      // Display / Home fallbacks
      Intent(Settings.ACTION_DISPLAY_SETTINGS),
      Intent(Settings.ACTION_SETTINGS)
    )

    for (intent in intentList) {
      try {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return true
      } catch (_: Exception) {
      }
    }
    return false
  }

  fun openDisplaySettings(context: Context): Boolean {
    return try {
      val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      false
    }
  }

  fun openDeveloperSettings(context: Context): Boolean {
    return try {
      val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      try {
        val intent = Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  fun openShizukuOnPlayStore(context: Context): Boolean {
    val playStoreIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=moe.shizuku.privileged.api")).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    return try {
      context.startActivity(playStoreIntent)
      true
    } catch (_: Exception) {
      try {
        val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api")).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  fun openTermuxDownload(context: Context): Boolean {
    return try {
      val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://f-droid.org/packages/com.termux/")).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(webIntent)
      true
    } catch (_: Exception) {
      false
    }
  }

  fun getInternalGrantPermissionCommand(context: Context): String {
    return "pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
  }

  fun getInternalHideCommand(): String {
    return "settings put global policy_control immersive.navigation=*"
  }

  fun getInternalRestoreCommand(): String {
    return "settings put global policy_control \"\""
  }

  fun getNavigationGuidePath(brand: ChineseOemHelper.OemBrand): String {
    return when (brand) {
      ChineseOemHelper.OemBrand.XIAOMI -> "Cài đặt > Màn hình chính > Điều hướng hệ thống > Chọn 'Cử chỉ' (Hoặc: Cài đặt bổ sung > Hiển thị toàn màn hình)"
      ChineseOemHelper.OemBrand.SAMSUNG -> "Cài đặt > Màn hình > Thanh điều hướng > Chọn 'Thao tác vuốt'"
      ChineseOemHelper.OemBrand.OPPO -> "Cài đặt > Cài đặt bổ sung > Điều hướng hệ thống > Chọn 'Cử chỉ'"
      ChineseOemHelper.OemBrand.VIVO -> "Cài đặt > Điều hướng hệ thống > Chọn 'Cử chỉ điều hướng'"
      ChineseOemHelper.OemBrand.HUAWEI -> "Cài đặt > Hệ thống & cập nhật > Điều hướng hệ thống > Chọn 'Cử chỉ'"
      else -> "Cài đặt > Hệ thống > Cử chỉ > Điều hướng hệ thống > Chọn 'Điều hướng bằng cử chỉ'"
    }
  }
}
