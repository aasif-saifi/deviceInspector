package com.example.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Cpu : Screen("cpu", "CPU", Icons.Default.Memory)
    object Device : Screen("device", "Device", Icons.Default.PhoneAndroid)
    object System : Screen("system", "System", Icons.Default.SettingsSystemDaydream)
    object Battery : Screen("battery", "Battery", Icons.Default.BatteryChargingFull)
    object Memory : Screen("memory", "Memory", Icons.Default.Storage)
    object Sensors : Screen("sensors", "Sensors", Icons.Default.Sensors)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val navigationItems = listOf(
        Screen.Dashboard,
        Screen.Cpu,
        Screen.Device,
        Screen.System,
        Screen.Battery,
        Screen.Memory,
        Screen.Sensors
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.Settings.route -> "Settings"
                            Screen.Dashboard.route -> "Device Inspector"
                            Screen.Cpu.route -> "CPU Diagnostics"
                            Screen.Device.route -> "Device Parameters"
                            Screen.System.route -> "System Metrics"
                            Screen.Battery.route -> "Battery Health"
                            Screen.Memory.route -> "Memory Pools"
                            Screen.Sensors.route -> "Live Sensors"
                            else -> "Device Inspector"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = SophisticatedHeaderTitleAccent
                ),
                navigationIcon = {
                    if (currentRoute == Screen.Settings.route) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "Diagnostics Active",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                },
                actions = {
                    if (currentRoute != Screen.Settings.route) {
                        // Quick Refresh Button
                        IconButton(onClick = {
                            viewModel.forceRefreshAll()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Force Refresh Specs"
                            )
                        }

                        // Report sharing shortcut
                        IconButton(onClick = {
                            coroutineScope.launch {
                                val json = viewModel.getJsonReport()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_SUBJECT, "Device Inspector Diagnostic Audit")
                                    putExtra(Intent.EXTRA_TEXT, json)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share JSON Report"))
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Specs"
                            )
                        }

                        // Go to Settings page
                        IconButton(onClick = {
                            navController.navigate(Screen.Settings.route) {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentRoute != Screen.Settings.route,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    navigationItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SophisticatedOnSurface else SophisticatedMutedText
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SophisticatedHeaderTitleAccent,
                                unselectedIconColor = SophisticatedMutedText,
                                indicatorColor = SophisticatedSecondary,
                                selectedTextColor = SophisticatedOnSurface,
                                unselectedTextColor = SophisticatedMutedText
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel = viewModel)
            }
            composable(Screen.Cpu.route) {
                CpuScreen(viewModel = viewModel)
            }
            composable(Screen.Device.route) {
                DeviceScreen(viewModel = viewModel)
            }
            composable(Screen.System.route) {
                SystemScreen(viewModel = viewModel)
            }
            composable(Screen.Battery.route) {
                BatteryScreen(viewModel = viewModel)
            }
            composable(Screen.Memory.route) {
                MemoryScreen(viewModel = viewModel)
            }
            composable(Screen.Sensors.route) {
                SensorsScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
