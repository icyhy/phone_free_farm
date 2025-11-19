package com.phonefocusfarm.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phonefocusfarm.core.permission.PermissionType
import com.phonefocusfarm.ui.screens.focus.FocusScreen
import com.phonefocusfarm.ui.screens.farm.FarmScreen
import com.phonefocusfarm.ui.screens.permission.PermissionScreen
import com.phonefocusfarm.ui.screens.stats.StatsScreen
import com.phonefocusfarm.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onRequestPermission: (PermissionType, (Boolean) -> Unit) -> Unit
) {
    val permissionViewModel: com.phonefocusfarm.ui.screens.permission.PermissionViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val areAllPermissionsGranted by permissionViewModel.areAllPermissionsGranted().collectAsState()
    
    var showPermissionScreen by remember { mutableStateOf(!areAllPermissionsGranted) }
    
    // 当权限状态改变时更新显示状态
    LaunchedEffect(areAllPermissionsGranted) {
        showPermissionScreen = !areAllPermissionsGranted
    }
    
    if (showPermissionScreen) {
        PermissionScreen(
            onAllPermissionsGranted = {
                showPermissionScreen = false
            },
            onRequestPermission = onRequestPermission
        )
    } else {
        NavHost(
            navController = navController,
            startDestination = Screen.Focus.route
        ) {
            composable(Screen.Focus.route) {
                FocusScreen(
                    onNavigateToFarm = { navController.navigate(Screen.Farm.route) }
                )
            }
            
            composable(Screen.Farm.route) {
                FarmScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFocus = { navController.navigate(Screen.Focus.route) },
                    onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Focus : Screen("focus")
    object Farm : Screen("farm")
    object Permission : Screen("permission")
    object Stats : Screen("stats")
    object Settings : Screen("settings")
}