package com.example

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.GesturePreferencesRepository
import com.example.ui.components.EmergencyRescueDialog
import com.example.ui.screens.AppearanceScreen
import com.example.ui.screens.ChineseOemOptimizationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GesturePlaygroundScreen
import com.example.ui.screens.HideNavBarScreen
import com.example.ui.screens.ZoneConfigScreen
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.theme.FluidFlowTheme

class MainActivity : ComponentActivity() {

  companion object {
    const val EXTRA_EMERGENCY_RESCUE = "extra_emergency_rescue"
  }

  private lateinit var repository: GesturePreferencesRepository
  private var showEmergencyRescueModal by mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    hideSystemNavigationBar()
    repository = GesturePreferencesRepository(applicationContext)

    if (intent?.getBooleanExtra(EXTRA_EMERGENCY_RESCUE, false) == true) {
      showEmergencyRescueModal = true
    }

    setContent {
      FluidFlowTheme {
        FluidFlowApp(
          repository = repository,
          showEmergencyRescueModal = showEmergencyRescueModal,
          onDismissEmergencyRescueModal = { showEmergencyRescueModal = false }
        )
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    if (intent.getBooleanExtra(EXTRA_EMERGENCY_RESCUE, false)) {
      showEmergencyRescueModal = true
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      hideSystemNavigationBar()
    }
  }

  private fun hideSystemNavigationBar() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
  }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
  object Dashboard : Screen("dashboard", "Tổng Quan", Icons.Default.Dashboard)
  object Playground : Screen("playground", "Sandbox", Icons.Default.PlayCircle)
  object Zones : Screen("zones?side={side}", "Viền Cử Chỉ", Icons.Default.Tune) {
    fun createRoute(side: String = "LEFT") = "zones?side=$side"
  }
  object OemOptimization : Screen("oem_optimization", "Pin & Khóa", Icons.Default.BatterySaver)
  object HideNavBar : Screen("hide_navbar", "Ẩn 3 Nút", Icons.Default.VisibilityOff)
  object Appearance : Screen("appearance", "Giao Diện", Icons.Default.Palette)
}

@Composable
fun FluidFlowApp(
  repository: GesturePreferencesRepository,
  showEmergencyRescueModal: Boolean = false,
  onDismissEmergencyRescueModal: () -> Unit = {}
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  if (showEmergencyRescueModal) {
    EmergencyRescueDialog(
      repository = repository,
      onDismiss = onDismissEmergencyRescueModal
    )
  }

  val context = LocalContext.current
  var lastBackPressMs by remember { mutableLongStateOf(0L) }

  // Double back to exit guard when at the root Dashboard screen to prevent accidental app exit during swipe test
  BackHandler(enabled = currentRoute == Screen.Dashboard.route) {
    val now = System.currentTimeMillis()
    if (now - lastBackPressMs < 2000L) {
      (context as? Activity)?.finish()
    } else {
      lastBackPressMs = now
      Toast.makeText(context, "Vuốt lùi lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show()
    }
  }

  val showBottomBar = currentRoute != "playground"

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    bottomBar = {
      AnimatedVisibility(
        visible = showBottomBar,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        BottomNavigationBar(
          currentRoute = currentRoute,
          onNavigate = { screen ->
            val route = when (screen) {
              is Screen.Zones -> screen.createRoute("LEFT")
              else -> screen.route
            }
            navController.navigate(route) {
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Dashboard.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      composable(Screen.Dashboard.route) {
        DashboardScreen(
          repository = repository,
          onNavigateToPlayground = {
            navController.navigate(Screen.Playground.route)
          },
          onNavigateToZoneConfig = { side ->
            navController.navigate(Screen.Zones.createRoute(side))
          },
          onNavigateToOemOptimization = {
            navController.navigate(Screen.OemOptimization.route)
          },
          onNavigateToHideNavBar = {
            navController.navigate(Screen.HideNavBar.route)
          }
        )
      }

      composable(Screen.Playground.route) {
        GesturePlaygroundScreen(
          repository = repository,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(
        route = Screen.Zones.route,
        arguments = listOf(
          navArgument("side") {
            type = NavType.StringType
            defaultValue = "LEFT"
          }
        )
      ) { backStackEntry ->
        val side = backStackEntry.arguments?.getString("side") ?: "LEFT"
        ZoneConfigScreen(
          initialSide = side,
          repository = repository,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.OemOptimization.route) {
        ChineseOemOptimizationScreen(
          repository = repository,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.HideNavBar.route) {
        HideNavBarScreen(
          repository = repository,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.Appearance.route) {
        AppearanceScreen(repository = repository)
      }
    }
  }
}

@Composable
fun BottomNavigationBar(
  currentRoute: String?,
  onNavigate: (Screen) -> Unit
) {
  val items = listOf(
    Screen.Dashboard,
    Screen.Playground,
    Screen.Zones,
    Screen.OemOptimization,
    Screen.Appearance
  )

  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    modifier = Modifier.testTag("main_bottom_nav")
  ) {
    items.forEach { screen ->
      val isSelected = when (screen) {
        is Screen.Zones -> currentRoute?.startsWith("zones") == true
        else -> currentRoute == screen.route
      }

      NavigationBarItem(
        icon = {
          Icon(
            imageVector = screen.icon,
            contentDescription = screen.title
          )
        },
        label = {
          Text(
            text = screen.title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
          )
        },
        selected = isSelected,
        onClick = { onNavigate(screen) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimary,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          indicatorColor = MaterialTheme.colorScheme.primary,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag("nav_item_${screen.route.split("?").first()}")
      )
    }
  }
}
