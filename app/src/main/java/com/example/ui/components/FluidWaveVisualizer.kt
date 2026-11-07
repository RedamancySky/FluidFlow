package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.GestureAction
import com.example.model.VisualWaveConfig
import kotlinx.coroutines.launch
import kotlin.math.min

enum class TouchEdgeSide {
  LEFT, RIGHT, BOTTOM
}

@Composable
fun FluidGestureInteractiveSandbox(
  modifier: Modifier = Modifier,
  visualConfig: VisualWaveConfig,
  triggerThresholdPx: Float = 110f,
  onGestureTriggered: (side: TouchEdgeSide, action: GestureAction) -> Unit = { _, _ -> },
  onHapticTick: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()

  var activeSide by remember { mutableStateOf<TouchEdgeSide?>(null) }
  var touchX by remember { mutableFloatStateOf(0f) }
  var touchY by remember { mutableFloatStateOf(0f) }
  val pullDistanceAnim = remember { Animatable(0f) }
  var isTriggered by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            val screenWidth = size.width.toFloat()
            val screenHeight = size.height.toFloat()
            val edgeMargin = 60f

            activeSide = when {
              offset.x <= edgeMargin -> TouchEdgeSide.LEFT
              offset.x >= screenWidth - edgeMargin -> TouchEdgeSide.RIGHT
              offset.y >= screenHeight - edgeMargin -> TouchEdgeSide.BOTTOM
              else -> null
            }

            if (activeSide != null) {
              touchX = offset.x
              touchY = offset.y
              isTriggered = false
              scope.launch { pullDistanceAnim.snapTo(0f) }
            }
          },
          onDrag = { change, dragAmount ->
            val side = activeSide ?: return@detectDragGestures
            change.consume()

            touchX += dragAmount.x
            touchY += dragAmount.y

            val currentDist = when (side) {
              TouchEdgeSide.LEFT -> touchX.coerceAtLeast(0f)
              TouchEdgeSide.RIGHT -> (size.width - touchX).coerceAtLeast(0f)
              TouchEdgeSide.BOTTOM -> (size.height - touchY).coerceAtLeast(0f)
            }

            // Compact damping for sleek motion
            val dampedDist = min(currentDist, triggerThresholdPx * 1.4f)

            scope.launch { pullDistanceAnim.snapTo(dampedDist) }

            if (dampedDist >= triggerThresholdPx) {
              if (!isTriggered) {
                isTriggered = true
                onHapticTick()
              }
            } else {
              isTriggered = false
            }
          },
          onDragEnd = {
            val side = activeSide
            if (side != null && isTriggered) {
              val action = when (side) {
                TouchEdgeSide.LEFT -> GestureAction.BACK
                TouchEdgeSide.RIGHT -> GestureAction.BACK
                TouchEdgeSide.BOTTOM -> GestureAction.HOME
              }
              onGestureTriggered(side, action)
            }

            activeSide = null
            isTriggered = false
            scope.launch {
              pullDistanceAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                  dampingRatio = Spring.DampingRatioLowBouncy,
                  stiffness = Spring.StiffnessMedium
                )
              )
            }
          },
          onDragCancel = {
            activeSide = null
            isTriggered = false
            scope.launch { pullDistanceAnim.snapTo(0f) }
          }
        )
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val side = activeSide
      val dist = pullDistanceAnim.value
      if (dist > 2f && side != null) {
        drawCompactGestureIndicator(
          side = side,
          touchX = touchX,
          touchY = touchY,
          distance = dist,
          threshold = triggerThresholdPx,
          isTriggered = isTriggered,
          visualConfig = visualConfig
        )
      }
    }
  }
}

private fun DrawScope.drawCompactGestureIndicator(
  side: TouchEdgeSide,
  touchX: Float,
  touchY: Float,
  distance: Float,
  threshold: Float,
  isTriggered: Boolean,
  visualConfig: VisualWaveConfig
) {
  // Adaptive background colors (sleek frosted surface & subtle accent)
  val baseSurface = Color(0xDD111827) // Frosted deep background
  val accentColor = Color(visualConfig.accentGlowColor)
  val indicatorColor = if (isTriggered) accentColor else Color(0xFFE2E8F0)
  val fillColor = if (isTriggered) baseSurface else Color(0xCC1E293B)

  // Compact span: reduced from 180f to sleek 65f
  val waveSpan = (65f + distance * 0.25f) * visualConfig.bubbleScale

  when (side) {
    TouchEdgeSide.LEFT -> {
      val startY = (touchY - waveSpan).coerceAtLeast(0f)
      val endY = (touchY + waveSpan).coerceAtMost(size.height)
      val peakX = distance * 0.85f

      // Compact Teardrop curve
      val path = Path().apply {
        moveTo(0f, startY)
        cubicTo(
          x1 = peakX * 0.3f, y1 = startY + waveSpan * 0.35f,
          x2 = peakX * 0.85f, y2 = touchY - waveSpan * 0.2f,
          x3 = peakX, y3 = touchY
        )
        cubicTo(
          x1 = peakX * 0.85f, y1 = touchY + waveSpan * 0.2f,
          x2 = peakX * 0.3f, y2 = endY - waveSpan * 0.35f,
          x3 = 0f, y3 = endY
        )
        close()
      }

      // Draw subtle frosted teardrop
      drawPath(
        path = path,
        color = fillColor
      )
      drawPath(
        path = path,
        color = if (isTriggered) accentColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
        style = Stroke(width = 1.5f)
      )

      // Sleek Back Chevron Arrow `<`
      val arrowCenterX = peakX - 12f
      val arrowCenterY = touchY
      val arrowPath = Path().apply {
        moveTo(arrowCenterX + 4f, arrowCenterY - 8f)
        lineTo(arrowCenterX - 4f, arrowCenterY)
        lineTo(arrowCenterX + 4f, arrowCenterY + 8f)
      }

      drawPath(
        path = arrowPath,
        color = indicatorColor,
        style = Stroke(
          width = if (isTriggered) 3.5f else 2.5f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }

    TouchEdgeSide.RIGHT -> {
      val screenW = size.width
      val startY = (touchY - waveSpan).coerceAtLeast(0f)
      val endY = (touchY + waveSpan).coerceAtMost(size.height)
      val peakX = screenW - distance * 0.85f

      val path = Path().apply {
        moveTo(screenW, startY)
        cubicTo(
          x1 = screenW - distance * 0.3f * 0.85f, y1 = startY + waveSpan * 0.35f,
          x2 = screenW - distance * 0.85f * 0.85f, y2 = touchY - waveSpan * 0.2f,
          x3 = peakX, y3 = touchY
        )
        cubicTo(
          x1 = screenW - distance * 0.85f * 0.85f, y1 = touchY + waveSpan * 0.2f,
          x2 = screenW - distance * 0.3f * 0.85f, y2 = endY - waveSpan * 0.35f,
          x3 = screenW, y3 = endY
        )
        close()
      }

      drawPath(
        path = path,
        color = fillColor
      )
      drawPath(
        path = path,
        color = if (isTriggered) accentColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
        style = Stroke(width = 1.5f)
      )

      // Sleek Back Chevron Arrow `>`
      val arrowCenterX = peakX + 12f
      val arrowCenterY = touchY
      val arrowPath = Path().apply {
        moveTo(arrowCenterX - 4f, arrowCenterY - 8f)
        lineTo(arrowCenterX + 4f, arrowCenterY)
        lineTo(arrowCenterX - 4f, arrowCenterY + 8f)
      }

      drawPath(
        path = arrowPath,
        color = indicatorColor,
        style = Stroke(
          width = if (isTriggered) 3.5f else 2.5f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }

    TouchEdgeSide.BOTTOM -> {
      val screenH = size.height
      val startX = (touchX - waveSpan).coerceAtLeast(0f)
      val endX = (touchX + waveSpan).coerceAtMost(size.width)
      val peakY = screenH - distance * 0.85f

      val path = Path().apply {
        moveTo(startX, screenH)
        cubicTo(
          x1 = startX + waveSpan * 0.35f, y1 = screenH - distance * 0.25f,
          x2 = touchX - waveSpan * 0.2f, y2 = screenH - distance * 0.8f,
          x3 = touchX, y3 = peakY
        )
        cubicTo(
          x1 = touchX + waveSpan * 0.2f, y1 = screenH - distance * 0.8f,
          x2 = endX - waveSpan * 0.35f, y2 = screenH - distance * 0.25f,
          x3 = endX, y3 = screenH
        )
        close()
      }

      drawPath(
        path = path,
        color = fillColor
      )
      drawPath(
        path = path,
        color = if (isTriggered) accentColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
        style = Stroke(width = 1.5f)
      )

      // Sleek Home pill bar line
      val pillWidth = if (isTriggered) 40f else 28f
      drawLine(
        color = indicatorColor,
        start = Offset(touchX - pillWidth / 2f, peakY + 10f),
        end = Offset(touchX + pillWidth / 2f, peakY + 10f),
        strokeWidth = if (isTriggered) 4.5f else 3.5f,
        cap = StrokeCap.Round
      )
    }
  }
}
