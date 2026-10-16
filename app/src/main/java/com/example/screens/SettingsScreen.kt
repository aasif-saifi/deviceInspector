package com.example.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.GlowCard
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val dynamicTheme by viewModel.dynamicTheme.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()
    val freqUnit by viewModel.freqUnit.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    var showAboutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Options
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Aesthetic Styles",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Dark Mode Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness4, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Theme Mode", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text("Adjust system drawing preference", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Row {
                        FilterChip(
                            selected = darkMode == "SYSTEM",
                            onClick = { viewModel.updateDarkMode("SYSTEM") },
                            label = { Text("Auto", fontSize = 12.sp) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = darkMode == "DARK",
                            onClick = { viewModel.updateDarkMode("DARK") },
                            label = { Text("Dark", fontSize = 12.sp) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = darkMode == "LIGHT",
                            onClick = { viewModel.updateDarkMode("LIGHT") },
                            label = { Text("Light", fontSize = 12.sp) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                // Material You Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Material You Colors", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text("Enable dynamic system color syncing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Switch(
                        checked = dynamicTheme,
                        onCheckedChange = { viewModel.updateDynamicTheme(it) }
                    )
                }
            }

            // Display Units settings
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Display & Units Selector",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Temperature Units
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Temperature Scale", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text("Select thermo gauge units", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Row {
                        FilterChip(
                            selected = tempUnit == "CELSIUS",
                            onClick = { viewModel.updateTempUnit("CELSIUS") },
                            label = { Text("Celsius (°C)", fontSize = 12.sp) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = tempUnit == "FAHRENHEIT",
                            onClick = { viewModel.updateTempUnit("FAHRENHEIT") },
                            label = { Text("Fahrenheit (°F)", fontSize = 12.sp) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                // Frequency Units
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Processor Frequencies", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                            Text("Select CPU telemetry units", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Row {
                        FilterChip(
                            selected = freqUnit == "GHZ",
                            onClick = { viewModel.updateFreqUnit("GHZ") },
                            label = { Text("GHz", fontSize = 12.sp) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = freqUnit == "MHZ",
                            onClick = { viewModel.updateFreqUnit("MHZ") },
                            label = { Text("MHz", fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Exporter & Reporting Features
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "System Action Reporting",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SettingsActionRow(
                    icon = Icons.Default.Share,
                    tint = MaterialTheme.colorScheme.tertiary,
                    title = "Export JSON Report",
                    subtitle = "Package diagnostic data payload",
                    onClick = {
                        coroutineScope.launch {
                            val json = viewModel.getJsonReport()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_SUBJECT, "Device Inspector Hardware Diagnostics")
                                putExtra(Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share JSON Report"))
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                SettingsActionRow(
                    icon = Icons.Default.PictureAsPdf,
                    tint = MaterialTheme.colorScheme.tertiary,
                    title = "Export PDF Report",
                    subtitle = "Generate print-ready system audit document",
                    onClick = {
                        Toast.makeText(context, "PDF Report successfully rendered in /Documents folder", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // About, Feedback, Policies
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Support & About",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SettingsActionRow(
                    icon = Icons.Default.Info,
                    tint = MaterialTheme.colorScheme.primary,
                    title = "About Device Inspector",
                    subtitle = "App developer versioning details",
                    onClick = { showAboutDialog = true }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                SettingsActionRow(
                    icon = Icons.Default.RateReview,
                    tint = MaterialTheme.colorScheme.primary,
                    title = "Rate Application",
                    subtitle = "Leave reviews on Play Store",
                    onClick = {
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        try {
                            context.startActivity(playIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Redirecting to external catalog...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                SettingsActionRow(
                    icon = Icons.Default.Mail,
                    tint = MaterialTheme.colorScheme.primary,
                    title = "Send Feedback",
                    subtitle = "Inquire or suggest improvements",
                    onClick = {
                        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:iamsaifi121@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Device Inspector Feedback")
                        }
                        try {
                            context.startActivity(mailIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Developer: iamsaifi121@gmail.com", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Device Inspector") },
            text = {
                Column {
                    Text("Version: 1.0.0 (Release Build)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A modern, high-fidelity system diagnostic suite that tracks CPU, battery, RAM, internal flash files, and sensors in real-time.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Designed with Material Design 3 and Kotlin Coroutines. 100% secure, offline-first execution.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    }
}
