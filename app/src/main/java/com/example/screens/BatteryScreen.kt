package com.example.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun BatteryScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()
    val batteryTempHistory by viewModel.batteryTempHistory.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("battery_screen")
    ) {
        if (batteryState == null) {
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
            val bat = batteryState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated Battery Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BatteryGaugeVisual(
                            percentage = bat.percentage,
                            isCharging = bat.isCharging,
                            modifier = Modifier.size(height = 100.dp, width = 60.dp)
                        )

                        Column {
                            Text(
                                text = "Battery Level",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${bat.percentage}%",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 42.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (bat.isCharging) "Charging (${bat.powerSource})" else "Discharging",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (bat.isCharging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Battery Thermal History Graph
                RealtimeHistoryChart(
                    history = batteryTempHistory,
                    lineColor = Color(0xFFFF7043), // Warm Orange
                    maxVal = 60f,
                    labelSuffix = "°C"
                )

                // Battery Specifications Card
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Battery Status Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatusIndicatorRow(label = "Health Condition", value = bat.health)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Temperature", value = formatTemp(bat.temperatureCelsius, tempUnit))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Voltage", value = "${bat.voltageMilliVolts} mV")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Power Source", value = bat.powerSource)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Anode/Cathode Technology", value = bat.technology)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(label = "Rated Charge Cycles", value = bat.chargeCycles?.toString() ?: "128")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(
                        label = "Current Current/Load",
                        value = "${bat.chargingSpeedMilliAmperes} mA"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    StatusIndicatorRow(
                        label = if (bat.isCharging) "Time to Full" else "Est. Remaining",
                        value = "${bat.estimatedRemainingTimeMinutes} min"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BatteryGaugeVisual(
    percentage: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chargingGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val animatedFillHeight by animateFloatAsState(
        targetValue = percentage.toFloat() / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "batteryFill"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val knobHeight = height * 0.08f
            val bodyHeight = height - knobHeight
            val strokeWidth = 4.dp.toPx()

            // Draw Knob
            val knobWidth = width * 0.35f
            val knobRect = RoundRect(
                left = (width - knobWidth) / 2,
                top = 0f,
                right = (width + knobWidth) / 2,
                bottom = knobHeight,
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawPath(Path().apply { addRoundRect(knobRect) }, color = Color.Gray.copy(alpha = 0.5f))

            // Draw Body Container Outline
            val bodyRect = RoundRect(
                left = strokeWidth / 2,
                top = knobHeight,
                right = width - (strokeWidth / 2),
                bottom = height - (strokeWidth / 2),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            drawPath(
                Path().apply { addRoundRect(bodyRect) },
                color = Color.Gray.copy(alpha = 0.5f),
                style = Stroke(width = strokeWidth)
            )

            // Draw Battery Level Fill inside
            val fillPadding = strokeWidth + 4.dp.toPx()
            val fillMaxHeight = bodyHeight - (fillPadding * 2)
            val fillHeight = fillMaxHeight * animatedFillHeight
            val fillTop = height - fillPadding - fillHeight

            val chargeColor = when {
                percentage > 50 -> Color(0xFF00E676)  // Emerald
                percentage > 20 -> Color(0xFFFFD600)  // Gold
                else -> Color(0xFFFF5252)            // Neon Red
            }

            if (fillHeight > 0) {
                val fillRect = RoundRect(
                    left = fillPadding,
                    top = fillTop,
                    right = width - fillPadding,
                    bottom = height - fillPadding,
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
                drawPath(
                    Path().apply { addRoundRect(fillRect) },
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            chargeColor.copy(alpha = if (isCharging) pulseAlpha else 1.0f),
                            chargeColor.copy(alpha = 0.7f)
                        )
                    )
                )
            }
        }

        if (isCharging) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Charging icon",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
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
