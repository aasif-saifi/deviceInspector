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
fun SystemScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val systemState by viewModel.systemState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("system_screen")
    ) {
        if (systemState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PulseLoadingShimmer(height = 140.dp)
                PulseLoadingShimmer(height = 180.dp)
            }
        } else {
            val sys = systemState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // OS Parameters Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Android OS & Core",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Android Version", value = "Android ${sys.androidVersion}")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "SDK / API Level", value = "${sys.sdkVersion}")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Security Patch", value = sys.securityPatch)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Build Release", value = sys.buildNumber)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Kernel Version", value = sys.kernelVersion)
                }

                // Security Diagnostics Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Security & Integrity Diagnostics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(
                        label = "Root Privilege Status",
                        value = if (sys.isRooted) "ROOTED / UNSECURE" else "Secured (No root access)"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "SELinux Policy", value = sys.selinuxStatus)
                }

                // Localization & Runtime Info
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Localization & Applications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Default Timezone", value = sys.timezone)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "System Language", value = sys.language)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Installed Applications", value = "${sys.installedAppsCount} Apps")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Running Background Services", value = "${sys.runningServicesCount} Services")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
