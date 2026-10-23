package com.example.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.ui.theme.*

sealed class Screen(
    val route: String,
    val title: String,
    val navLabel: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", "Dash", Icons.Default.Dashboard)
    object Cpu : Screen("cpu", "CPU", "CPU", Icons.Default.Memory)
    object Device : Screen("device", "Device", "Device", Icons.Default.PhoneAndroid)
    object System : Screen("system", "System", "System", Icons.Default.SettingsSystemDaydream)
    object Battery : Screen("battery", "Battery", "Battery", Icons.Default.BatteryChargingFull)
    object Memory : Screen("memory", "Memory", "Memory", Icons.Default.Storage)
    object Sensors : Screen("sensors", "Sensors", "Sensors", Icons.Default.Sensors)
    object Settings : Screen("settings", "Settings", "Settings", Icons.Default.Settings)
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                            .height(68.dp)
                            .padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navigationItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(vertical = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(42.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(if (isSelected) SophisticatedSecondary else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) SophisticatedHeaderTitleAccent else SophisticatedMutedText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = screen.navLabel,
                                    fontSize = 9.5.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SophisticatedOnSurface else SophisticatedMutedText,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
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
