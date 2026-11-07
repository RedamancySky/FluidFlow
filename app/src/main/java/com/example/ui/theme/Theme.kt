package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CyanAccent,
  onPrimary = Color(0xFF031628),
  primaryContainer = Color(0xFF0A304E),
  onPrimaryContainer = Color(0xFFB8EAFF),
  secondary = VioletAccent,
  onSecondary = Color(0xFF1E0A40),
  secondaryContainer = Color(0xFF381E72),
  onSecondaryContainer = Color(0xFFE9DDFF),
  tertiary = EmeraldSuccess,
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextSecondaryDark,
  error = RoseDanger,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = CyanAccentDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD6F3FF),
  onPrimaryContainer = Color(0xFF00364A),
  secondary = VioletAccentDark,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEDE7F6),
  onSecondaryContainer = Color(0xFF2E1065),
  tertiary = EmeraldSuccess,
  background = BackgroundLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = TextSecondaryLight,
  error = RoseDanger,
  onError = Color.White
)

@Composable
fun FluidFlowTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep distinctive cyber-fluid branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> DarkColorScheme // Default to sleek dark mode for high-tech gesture aesthetic
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
