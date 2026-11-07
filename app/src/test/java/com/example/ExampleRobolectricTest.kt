package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GesturePreferencesRepository
import com.example.model.GestureAction
import com.example.util.ChineseOemHelper
import com.example.util.SystemNavBarManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.TARGET_SDK])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FluidFlow", appName)
  }

  @Test
  fun `verify default gesture preferences repository`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = GesturePreferencesRepository(context)
    val settings = repository.settings.value

    assertNotNull(settings)
    assertEquals(GestureAction.BACK, settings.leftEdge.swipeAction)
    assertEquals(GestureAction.HOME, settings.bottomBar.centerSectionSwipe)
    assertEquals(GestureAction.RECENTS, settings.rightEdge.holdAction)
    assertTrue("serviceEnabled should default to true", settings.serviceEnabled)
    assertTrue("antiKillNotificationEnabled should default to true", settings.antiKillNotificationEnabled)
    assertTrue("autoRestoreOnUnlock should default to true", settings.autoRestoreOnUnlock)
    assertTrue("ecoBatteryMode should default to true", settings.ecoBatteryMode)

    repository.setBottomOffsetAbove3Buttons(true)
    assertTrue("offsetAbove3Buttons should be true after update", repository.settings.value.bottomBar.offsetAbove3Buttons)
    repository.setBottomOffsetAbove3Buttons(false)
    org.junit.Assert.assertFalse("offsetAbove3Buttons should be false after update", repository.settings.value.bottomBar.offsetAbove3Buttons)
  }

  @Test
  fun `verify gesture action fromId mapping`() {
    assertEquals(GestureAction.BACK, GestureAction.fromId("BACK"))
    assertEquals(GestureAction.HOME, GestureAction.fromId("HOME"))
    assertEquals(GestureAction.RECENTS, GestureAction.fromId("RECENTS"))
    assertEquals(GestureAction.LAST_APP, GestureAction.fromId("LAST_APP"))
    assertEquals(GestureAction.SCREENSHOT, GestureAction.fromId("SCREENSHOT"))
  }

  @Test
  fun `verify chinese oem helper brand detection`() {
    val brand = ChineseOemHelper.getDeviceBrand()
    assertNotNull(brand)
    val guide = ChineseOemHelper.getOptimizationGuideForCurrentBrand()
    assertNotNull(guide)
    assertTrue(guide.steps.isNotEmpty())
  }

  @Test
  fun `verify system navbar manager adb commands`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val grantCmd = SystemNavBarManager.getAdbGrantPermissionCommand(context)
    val hideCmd = SystemNavBarManager.getAdbDirectHideCommand()
    val restoreCmd = SystemNavBarManager.getAdbRestoreCommand()

    assertTrue(grantCmd.contains("WRITE_SECURE_SETTINGS"))
    assertTrue(hideCmd.contains("immersive.navigation"))
    assertTrue(restoreCmd.contains("policy_control"))
  }

  @Test
  fun `verify navigation guide path returns valid instructions for all brands`() {
    for (brand in ChineseOemHelper.OemBrand.values()) {
      val guide = SystemNavBarManager.getNavigationGuidePath(brand)
      assertNotNull(guide)
      assertTrue("Guide should have content for $brand", guide.isNotBlank())
    }
  }

  @Test
  fun `verify playground active flag toggling`() {
    com.example.service.FluidGestureAccessibilityService.isPlaygroundActive = true
    assertTrue(com.example.service.FluidGestureAccessibilityService.isPlaygroundActive)
    com.example.service.FluidGestureAccessibilityService.isPlaygroundActive = false
    org.junit.Assert.assertFalse(com.example.service.FluidGestureAccessibilityService.isPlaygroundActive)
  }
}
