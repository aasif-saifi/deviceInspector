package com.example.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.GlowCard
import com.example.components.PulseLoadingShimmer
import com.example.components.RealtimeHistoryChart
import com.example.components.StatusIndicatorRow
import java.util.Locale

@Composable
fun MemoryScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val memoryState by viewModel.memoryState.collectAsStateWithLifecycle()
    val ramHistory by viewModel.ramHistory.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("memory_screen")
    ) {
        if (memoryState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PulseLoadingShimmer(height = 140.dp)
                PulseLoadingShimmer(height = 200.dp)
            }
        } else {
            val mem = memoryState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Realtime RAM Activity Graph
                RealtimeHistoryChart(
                    history = ramHistory.map { it.toFloat() },
                    lineColor = MaterialTheme.colorScheme.secondary,
                    maxVal = 100f,
                    labelSuffix = "%"
                )

                // 1. Random Access Memory (RAM) Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Random Access Memory (RAM)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val totalRamGb = mem.totalRamBytes.toFloat() / (1024 * 1024 * 1024)
                    val availRamGb = mem.availableRamBytes.toFloat() / (1024 * 1024 * 1024)
                    val usedRamGb = mem.usedRamBytes.toFloat() / (1024 * 1024 * 1024)
                    val ramPct = if (mem.totalRamBytes > 0) (mem.usedRamBytes.toFloat() / mem.totalRamBytes.toFloat() * 100).toInt() else 0

                    StatusIndicatorRow(label = "Total System Memory", value = String.format(Locale.getDefault(), "%.2f GB", totalRamGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Available Free Memory", value = String.format(Locale.getDefault(), "%.2f GB", availRamGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Active Used Memory", value = String.format(Locale.getDefault(), "%.2f GB", usedRamGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "RAM Utilization", value = "$ramPct%")
                }

                // 2. Storage Metrics Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Disk Partition & Storage",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val totalInternalGb = mem.totalStorageBytes.toFloat() / (1024 * 1024 * 1024)
                    val availInternalGb = mem.availableStorageBytes.toFloat() / (1024 * 1024 * 1024)
                    val usedInternalGb = mem.usedStorageBytes.toFloat() / (1024 * 1024 * 1024)
                    val internalPct = if (mem.totalStorageBytes > 0) (mem.usedStorageBytes.toFloat() / mem.totalStorageBytes.toFloat() * 100).toInt() else 0

                    StatusIndicatorRow(label = "Internal Disk Volume", value = String.format(Locale.getDefault(), "%.2f GB", totalInternalGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Free Disk Space", value = String.format(Locale.getDefault(), "%.2f GB", availInternalGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Used Disk Space", value = String.format(Locale.getDefault(), "%.2f GB", usedInternalGb))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Disk Utilization", value = "$internalPct%")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    
                    val cacheMb = mem.cacheSizeBytes.toFloat() / (1024 * 1024)
                    StatusIndicatorRow(label = "Application App Cache", value = String.format(Locale.getDefault(), "%.1f MB", cacheMb))

                    mem.externalTotalStorageBytes?.let { totalExt ->
                        mem.externalAvailableStorageBytes?.let { availExt ->
                            val totalExtGb = totalExt.toFloat() / (1024 * 1024 * 1024)
                            val availExtGb = availExt.toFloat() / (1024 * 1024 * 1024)
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            StatusIndicatorRow(label = "SD Card Size", value = String.format(Locale.getDefault(), "%.2f GB", totalExtGb))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            StatusIndicatorRow(label = "SD Card Free", value = String.format(Locale.getDefault(), "%.2f GB", availExtGb))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
