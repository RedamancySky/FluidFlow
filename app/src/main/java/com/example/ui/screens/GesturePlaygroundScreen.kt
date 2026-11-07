package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GesturePreferencesRepository
import com.example.model.GestureAction
import com.example.service.FluidGestureAccessibilityService
import com.example.service.HapticHelper
import com.example.ui.components.FluidGestureInteractiveSandbox
import com.example.ui.components.TouchEdgeSide
import kotlinx.coroutines.delay

@Composable
fun GesturePlaygroundScreen(
  repository: GesturePreferencesRepository,
  onNavigateBack: () -> Unit
) {
  BackHandler { onNavigateBack() }

  // Suppress accessibility service global actions while practicing in sandbox
  DisposableEffect(Unit) {
    FluidGestureAccessibilityService.isPlaygroundActive = true
    onDispose {
      FluidGestureAccessibilityService.isPlaygroundActive = false
    }
  }

  val context = LocalContext.current
  val settings by repository.settings.collectAsState()
  val hapticHelper = remember { HapticHelper(context) }

  var lastTriggeredSide by remember { mutableStateOf<TouchEdgeSide?>(null) }
  var lastAction by remember { mutableStateOf<GestureAction?>(null) }
  var triggerCount by remember { mutableStateOf(0) }

  var sensitivityDistance by remember { mutableFloatStateOf(130f) }

  // Auto clear HUD after 2.5s
  LaunchedEffect(triggerCount) {
    if (triggerCount > 0) {
      delay(2200)
      lastAction = null
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF090D16))
  ) {
    // Interactive Fluid Sandbox Canvas
    FluidGestureInteractiveSandbox(
      modifier = Modifier.fillMaxSize(),
      visualConfig = settings.visual,
      triggerThresholdPx = sensitivityDistance,
      onGestureTriggered = { side, action ->
        lastTriggeredSide = side
        lastAction = when (side) {
          TouchEdgeSide.LEFT -> settings.leftEdge.swipeAction
          TouchEdgeSide.RIGHT -> settings.rightEdge.swipeAction
          TouchEdgeSide.BOTTOM -> settings.bottomBar.centerSectionSwipe
        }
        triggerCount++
        if (settings.haptic.enabled) {
          hapticHelper.vibrate(settings.haptic.intensity)
        }
      },
      onHapticTick = {
        if (settings.haptic.enabled) {
          hapticHelper.vibrate(settings.haptic.intensity)
        }
      }
    )

    // Top Controls Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 24.dp)
        .align(Alignment.TopCenter)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
              .testTag("playground_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Quay lại",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = "Gesture Sandbox",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = "Thử nghiệm kéo sóng nước trực tiếp",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Triggered Action Notification HUD
      AnimatedVisibility(
        visible = lastAction != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        lastAction?.let { action ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
              )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ElectricBolt,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Kích hoạt từ viền: ${lastTriggeredSide?.name ?: ""}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = action.title,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }

    // Center Instructions Guide
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "LƯỚT TỪ VIỀN MÀN HÌNH",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp
        ),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "← Kéo từ Cạnh Trái hoặc Cạnh Phải →\n↑ Vuốt từ Viền Đáy lên",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
      )
    }

    // Bottom Calibration Panel
    Card(
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .testTag("sandbox_calibration_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Khoảng Cách Kích Hoạt (Sensitivity)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${sensitivityDistance.toInt()} px",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Slider(
          value = sensitivityDistance,
          onValueChange = { sensitivityDistance = it },
          valueRange = 70f..240f,
          colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("sensitivity_slider")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Vuốt nhẹ là nhận",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Kéo sâu mới nhận",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
