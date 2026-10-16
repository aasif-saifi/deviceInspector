package com.example.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.GlowCard
import com.example.components.PulseLoadingShimmer
import com.example.components.RealtimeHistoryChart
import com.example.components.StatusIndicatorRow
import java.util.Locale

@Composable
fun CpuScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val cpuState by viewModel.cpuState.collectAsStateWithLifecycle()
    val cpuHistory by viewModel.cpuHistory.collectAsStateWithLifecycle()
    val freqUnit by viewModel.freqUnit.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("cpu_screen")
    ) {
        if (cpuState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PulseLoadingShimmer(height = 140.dp)
                PulseLoadingShimmer(height = 200.dp)
                PulseLoadingShimmer(height = 160.dp)
            }
        } else {
            val cpu = cpuState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Realtime Activity Graph
                RealtimeHistoryChart(
                    history = cpuHistory.map { it.toFloat() },
                    lineColor = MaterialTheme.colorScheme.primary,
                    maxVal = 100f,
                    labelSuffix = "%"
                )

                // Static Hardware Overview Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Processor Specs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Processor / SoC", value = cpu.model)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Hardware Board", value = cpu.manufacturer)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Architecture", value = cpu.architecture)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Active Governor", value = cpu.governor)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(
                        label = "Core Count",
                        value = "${cpu.numberOfCores} Cores"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(
                        label = "Frequency Range",
                        value = "${formatFreq(cpu.minFrequencyHz, freqUnit)} - ${formatFreq(cpu.maxFrequencyHz, freqUnit)}"
                    )
                    
                    cpu.temperatureCelsius?.let { temp ->
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        StatusIndicatorRow(
                            label = "SoC Temperature",
                            value = formatTemp(temp, tempUnit)
                        )
                    }
                }

                // Cores Monitoring Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Real-Time CPU Core Loads",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    cpu.cores.forEachIndexed { idx, core ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Core $idx",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${core.usagePercent}% (${formatFreq(core.currentFrequencyHz, freqUnit)})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            val animatedProgress by animateFloatAsState(
                                targetValue = core.usagePercent.toFloat() / 100f,
                                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                                label = "coreProgress"
                            )

                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatFreq(hz: Long, unit: String): String {
    val mhz = hz / 1_000_000
    return if (unit == "GHZ") {
        String.format(Locale.getDefault(), "%.2f GHz", mhz.toFloat() / 1000f)
    } else {
        "$mhz MHz"
    }
}

private fun formatTemp(celsius: Float, unit: String): String {
    return if (unit == "FAHRENHEIT") {
        val f = (celsius * 9 / 5) + 32
        String.format(Locale.getDefault(), "%.1f °F", f)
    } else {
        String.format(Locale.getDefault(), "%.1f °C", celsius)
    }
}
