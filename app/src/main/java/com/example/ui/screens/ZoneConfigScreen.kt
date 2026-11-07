package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GesturePreferencesRepository
import com.example.model.BottomBarConfig
import com.example.model.EdgeZoneConfig
import com.example.model.GestureAction
import com.example.service.FluidGestureAccessibilityService
import com.example.ui.components.ActionSelectorBottomSheet
import com.example.ui.components.getActionIcon

@Composable
fun ZoneConfigScreen(
  initialSide: String, // "LEFT", "RIGHT", "BOTTOM"
  repository: GesturePreferencesRepository,
  onNavigateBack: () -> Unit
) {
  BackHandler { onNavigateBack() }

  val settings by repository.settings.collectAsState()

  var selectedZoneSide by remember { mutableStateOf(initialSide) }

  // State for Action Selector Bottom Sheet
  var activeActionSlotName by remember { mutableStateOf<String?>(null) }
  var currentSlotAction by remember { mutableStateOf(GestureAction.BACK) }
  var onSaveAction by remember { mutableStateOf<(GestureAction) -> Unit>({}) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier.testTag("zone_config_back")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Quay lại",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = "Cài Đặt Vùng Cử Chỉ",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Zone Selector Segment Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      ZoneTabButton(
        title = "Viền Trái",
        isSelected = selectedZoneSide == "LEFT",
        onClick = { selectedZoneSide = "LEFT" },
        modifier = Modifier.weight(1f)
      )
      ZoneTabButton(
        title = "Viền Phải",
        isSelected = selectedZoneSide == "RIGHT",
        onClick = { selectedZoneSide = "RIGHT" },
        modifier = Modifier.weight(1f)
      )
      ZoneTabButton(
        title = "Viền Đáy",
        isSelected = selectedZoneSide == "BOTTOM",
        onClick = { selectedZoneSide = "BOTTOM" },
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Zone Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      when (selectedZoneSide) {
        "LEFT" -> {
          EdgeZoneEditor(
            sideTitle = "Viền Cạnh Trái",
            config = settings.leftEdge,
            onConfigChange = { newConfig ->
              repository.updateLeftEdge { newConfig }
              FluidGestureAccessibilityService.instance?.refreshOverlays()
            },
            onOpenActionPicker = { slotName, curAction, callback ->
              activeActionSlotName = slotName
              currentSlotAction = curAction
              onSaveAction = callback
            }
          )
        }
        "RIGHT" -> {
          EdgeZoneEditor(
            sideTitle = "Viền Cạnh Phải",
            config = settings.rightEdge,
            onConfigChange = { newConfig ->
              repository.updateRightEdge { newConfig }
              FluidGestureAccessibilityService.instance?.refreshOverlays()
            },
            onOpenActionPicker = { slotName, curAction, callback ->
              activeActionSlotName = slotName
              currentSlotAction = curAction
              onSaveAction = callback
            }
          )
        }
        "BOTTOM" -> {
          BottomBarEditor(
            config = settings.bottomBar,
            onConfigChange = { newConfig ->
              repository.updateBottomBar { newConfig }
              FluidGestureAccessibilityService.instance?.refreshOverlays()
            },
            onOpenActionPicker = { slotName, curAction, callback ->
              activeActionSlotName = slotName
              currentSlotAction = curAction
              onSaveAction = callback
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Action Picker Modal Sheet
  activeActionSlotName?.let { slotName ->
    ActionSelectorBottomSheet(
      title = slotName,
      currentAction = currentSlotAction,
      onActionSelected = { selectedAction ->
        onSaveAction(selectedAction)
        activeActionSlotName = null
        FluidGestureAccessibilityService.instance?.refreshOverlays()
      },
      onDismiss = { activeActionSlotName = null }
    )
  }
}

@Composable
private fun ZoneTabButton(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      ),
      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun EdgeZoneEditor(
  sideTitle: String,
  config: EdgeZoneConfig,
  onConfigChange: (EdgeZoneConfig) -> Unit,
  onOpenActionPicker: (slotName: String, current: GestureAction, onSave: (GestureAction) -> Unit) -> Unit
) {
  // Enabled Switch
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Kích Hoạt $sideTitle",
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Bật nhận diện thao tác vuốt từ cạnh này",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Switch(
        checked = config.enabled,
        onCheckedChange = { onConfigChange(config.copy(enabled = it)) },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.primary,
          checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
        )
      )
    }
  }

  Spacer(modifier = Modifier.height(18.dp))

  // Gesture Actions Section
  Text(
    text = "GÁN TÁC VỤ CHO THAO TÁC",
    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = MaterialTheme.colorScheme.primary
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt nhanh (Swipe)",
    subtitle = "Kéo ngón tay từ viền vào rồi thả ra",
    action = config.swipeAction,
    onClick = {
      onOpenActionPicker("Vuốt nhanh (Swipe)", config.swipeAction) {
        onConfigChange(config.copy(swipeAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt và giữ (Swipe & Hold)",
    subtitle = "Kéo ngón tay vào trong và giữ 0.45s",
    action = config.holdAction,
    onClick = {
      onOpenActionPicker("Vuốt và giữ (Swipe & Hold)", config.holdAction) {
        onConfigChange(config.copy(holdAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt chéo lên (Diagonal Up)",
    subtitle = "Kéo từ viền chếch lên phía trên",
    action = config.diagonalUpAction,
    onClick = {
      onOpenActionPicker("Vuốt chéo lên (Diagonal Up)", config.diagonalUpAction) {
        onConfigChange(config.copy(diagonalUpAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt chéo xuống (Diagonal Down)",
    subtitle = "Kéo từ viền chếch xuống phía dưới",
    action = config.diagonalDownAction,
    onClick = {
      onOpenActionPicker("Vuốt chéo xuống (Diagonal Down)", config.diagonalDownAction) {
        onConfigChange(config.copy(diagonalDownAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(20.dp))

  // Geometry Settings
  Text(
    text = "KÍCH THƯỚC VÙNG CẢM ỨNG",
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
    Column(modifier = Modifier.padding(16.dp)) {
      // Height Percent Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Chiều cao vùng nhận diện",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${config.heightPercent.toInt()}% màn hình",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Slider(
        value = config.heightPercent,
        onValueChange = { onConfigChange(config.copy(heightPercent = it)) },
        valueRange = 25f..100f,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Touch Width Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Độ dày viền nhận diện (Touch Target)",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${config.widthDp} dp",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Slider(
        value = config.widthDp.toFloat(),
        onValueChange = { onConfigChange(config.copy(widthDp = it.toInt())) },
        valueRange = 10f..35f,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Trigger Distance Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Khoảng cách vuốt kích hoạt",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${config.triggerDistanceDp} dp",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Slider(
        value = config.triggerDistanceDp.toFloat(),
        onValueChange = { onConfigChange(config.copy(triggerDistanceDp = it.toInt())) },
        valueRange = 22f..55f,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  }
}

@Composable
private fun BottomBarEditor(
  config: BottomBarConfig,
  onConfigChange: (BottomBarConfig) -> Unit,
  onOpenActionPicker: (slotName: String, current: GestureAction, onSave: (GestureAction) -> Unit) -> Unit
) {
  // Enabled Switch
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Kích Hoạt Thanh Viền Đáy",
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Thay thế hoàn hảo cử chỉ vuốt đáy của hệ thống gốc",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Switch(
        checked = config.enabled,
        onCheckedChange = { onConfigChange(config.copy(enabled = it)) },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.primary,
          checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
        )
      )
    }
  }

  Spacer(modifier = Modifier.height(18.dp))

  Text(
    text = "PHÂN VÙNG THAO TÁC CỦA THANH ĐÁY",
    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = MaterialTheme.colorScheme.primary
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt giữa đáy (Center Swipe)",
    subtitle = "Thao tác mặc định về màn hình chính",
    action = config.centerSectionSwipe,
    onClick = {
      onOpenActionPicker("Vuốt giữa đáy (Center Swipe)", config.centerSectionSwipe) {
        onConfigChange(config.copy(centerSectionSwipe = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt và giữ giữa (Center Hold)",
    subtitle = "Vuốt từ giữa lên và dừng lại",
    action = config.centerHoldAction,
    onClick = {
      onOpenActionPicker("Vuốt và giữ giữa (Center Hold)", config.centerHoldAction) {
        onConfigChange(config.copy(centerHoldAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt góc trái đáy (Left Pill)",
    subtitle = "Vuốt lên từ góc 1/4 bên trái",
    action = config.leftSectionSwipe,
    onClick = {
      onOpenActionPicker("Vuốt góc trái đáy", config.leftSectionSwipe) {
        onConfigChange(config.copy(leftSectionSwipe = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Vuốt góc phải đáy (Right Pill)",
    subtitle = "Vuốt lên từ góc 1/4 bên phải",
    action = config.rightSectionSwipe,
    onClick = {
      onOpenActionPicker("Vuốt góc phải đáy", config.rightSectionSwipe) {
        onConfigChange(config.copy(rightSectionSwipe = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(8.dp))

  ActionSlotRow(
    title = "Gạt ngang mép đáy (Swipe Across)",
    subtitle = "Vuốt ngón tay trượt ngang cạnh đáy để đổi nhanh app",
    action = config.swipeAcrossAction,
    onClick = {
      onOpenActionPicker("Gạt ngang mép đáy", config.swipeAcrossAction) {
        onConfigChange(config.copy(swipeAcrossAction = it))
      }
    }
  )

  Spacer(modifier = Modifier.height(20.dp))

  // Height Slider
  Text(
    text = "ĐỘ CAO VÙNG NHẬN DIỆN ĐÁY",
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
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Độ cao thanh cảm ứng đáy",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${config.heightDp} dp",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Slider(
        value = config.heightDp.toFloat(),
        onValueChange = { onConfigChange(config.copy(heightDp = it.toInt())) },
        valueRange = 14f..60f,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  }

  Spacer(modifier = Modifier.height(14.dp))

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (config.offsetAbove3Buttons)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Nâng lên trên thanh 3 phím (+48dp)",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (config.offsetAbove3Buttons)
            "Vùng vuốt được đặt phía trên 3 phím Back/Home/Recents của máy."
          else
            "Bật nếu máy chưa ẩn được 3 phím để tránh chạm nhầm phím hệ thống.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Switch(
        checked = config.offsetAbove3Buttons,
        onCheckedChange = { onConfigChange(config.copy(offsetAbove3Buttons = it)) }
      )
    }
  }
}

@Composable
private fun ActionSlotRow(
  title: String,
  subtitle: String,
  action: GestureAction,
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
        .padding(horizontal = 16.dp, vertical = 14.dp),
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
          imageVector = getActionIcon(action),
          contentDescription = action.title,
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
        Text(
          text = "→ ${action.title}",
          style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
        )
      }

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = "Chọn",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
