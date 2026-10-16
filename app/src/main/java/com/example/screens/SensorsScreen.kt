package com.example.screens

import android.hardware.Sensor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.GlowCard
import com.example.components.StatusIndicatorRow
import com.example.model.SensorState
import java.util.Locale

@Composable
fun SensorsScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val sensorsList by viewModel.sensorsList.collectAsStateWithLifecycle()
    val activeSensorType by viewModel.activeSensorType.collectAsStateWithLifecycle()
    val liveSensorValues by viewModel.liveSensorValues.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sensors_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Onboard Device Sensors",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (sensorsList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(sensorsList, key = { it.id }) { sensor ->
                val isExpanded = activeSensorType == sensor.type
                
                SensorCard(
                    sensor = sensor,
                    isExpanded = isExpanded,
                    liveValues = if (isExpanded) liveSensorValues else sensor.currentValues,
                    onExpandClick = {
                        if (isExpanded) {
                            viewModel.observeSensorRealtime(null) // Collapse
                        } else {
                            viewModel.observeSensorRealtime(sensor.type) // Expand and listen
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SensorCard(
    sensor: SensorState,
    isExpanded: Boolean,
    liveValues: List<Float>,
    onExpandClick: () -> Unit
) {
    val sensorIcon = getSensorIcon(sensor.type)
    val tintColor = if (sensor.isAvailable) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = sensor.isAvailable) { onExpandClick() },
        borderColor = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = sensorIcon,
                    contentDescription = sensor.name,
                    tint = tintColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = sensor.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (sensor.isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = if (sensor.isAvailable) "Manufacturer: ${sensor.vendor}" else "Not physically present on hardware",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            if (sensor.isAvailable) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand Status",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Live Stream Values Reading
                Text(
                    text = "Live Hardware Telemetry Stream",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (liveValues.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        liveValues.forEachIndexed { idx, value ->
                            val axisLabel = when (sensor.type) {
                                Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD -> {
                                    when (idx) {
                                        0 -> "X"
                                        1 -> "Y"
                                        2 -> "Z"
                                        else -> "V$idx"
                                    }
                                }
                                else -> "Value"
                            }
                            
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = axisLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.3f", value),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Polling sensor event callbacks...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                // Sensor Specs Details
                StatusIndicatorRow(label = "Hardware Vendor", value = sensor.vendor)
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                StatusIndicatorRow(label = "API Version", value = "${sensor.version}")
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                StatusIndicatorRow(label = "Maximum Range limit", value = String.format(Locale.getDefault(), "%.2f", sensor.maximumRange))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                StatusIndicatorRow(label = "Sensitivity Resolution", value = String.format(Locale.getDefault(), "%.5f", sensor.resolution))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                StatusIndicatorRow(label = "Power Draw Rate", value = "${sensor.power} mA")
            }
        }
    }
}

private fun getSensorIcon(type: Int): ImageVector {
    return when (type) {
        Sensor.TYPE_ACCELEROMETER -> Icons.Default.DirectionsRun
        Sensor.TYPE_GYROSCOPE -> Icons.Default.Sync
        Sensor.TYPE_MAGNETIC_FIELD -> Icons.Default.Explore
        Sensor.TYPE_LIGHT -> Icons.Default.LightMode
        Sensor.TYPE_PRESSURE -> Icons.Default.Compress
        Sensor.TYPE_RELATIVE_HUMIDITY -> Icons.Default.WaterDrop
        Sensor.TYPE_AMBIENT_TEMPERATURE -> Icons.Default.Thermostat
        Sensor.TYPE_GRAVITY -> Icons.Default.South
        Sensor.TYPE_ROTATION_VECTOR -> Icons.Default.ScreenRotation
        Sensor.TYPE_HEART_RATE -> Icons.Default.Favorite
        Sensor.TYPE_STEP_COUNTER -> Icons.Default.PedalBike
        Sensor.TYPE_PROXIMITY -> Icons.Default.CellTower
        else -> Icons.Default.Sensors
    }
}
