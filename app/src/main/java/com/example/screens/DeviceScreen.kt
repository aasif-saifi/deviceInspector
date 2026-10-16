package com.example.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.GlowCard
import com.example.components.PulseLoadingShimmer
import com.example.components.StatusIndicatorRow

@Composable
fun DeviceScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val deviceState by viewModel.deviceState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("device_screen")
    ) {
        if (deviceState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PulseLoadingShimmer(height = 120.dp)
                PulseLoadingShimmer(height = 220.dp)
                PulseLoadingShimmer(height = 140.dp)
            }
        } else {
            val dev = deviceState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. General Identification
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Device Information",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Model", value = dev.model)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Manufacturer", value = dev.manufacturer)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Brand", value = dev.brand)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Board Code", value = dev.board)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Hardware Config", value = dev.hardware)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Bootloader", value = dev.bootloader)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Android ID", value = dev.androidId)
                }

                // 2. Display Metrics Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Display Metrics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Resolution", value = dev.screenResolution)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Refresh Rate", value = "${dev.refreshRateHz.toInt()} Hz")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Screen Density", value = "${dev.densityDpi} DPI")
                }

                // 3. GPU Parameters Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Graphics Processor (GPU)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "GPU Renderer", value = dev.gpuRenderer)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "GPU Vendor", value = dev.gpuVendor)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "OpenGL ES Version", value = dev.openGlVersion)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Vulkan Core Support", value = if (dev.vulkanSupport) "Supported" else "No")
                }

                // 4. Hardware Integrations (Features & Sensors)
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Device Integrations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "NFC Transmitter", value = if (dev.nfcSupported) "Available" else "No")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Fingerprint Sensor", value = if (dev.fingerprintSupport) "Available" else "No")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Face Unlock Recognition", value = if (dev.faceUnlockSupport) "Available" else "No")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Bluetooth Core", value = if (dev.bluetoothSupported) "Supported" else "No")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Wi-Fi Module", value = if (dev.wifiSupported) "Supported" else "No")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "USB Host Controller", value = if (dev.usbSupported) "Supported" else "No")
                }
            }
        }
    }
}
