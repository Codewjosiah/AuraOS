package app.auraos.shell.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.material.glass.AuraGlassButton
import app.auraos.material.glass.AuraGlassCard
import app.auraos.material.glass.AuraGlassSurface
import app.auraos.shell.state.AuraGlobalState

@Composable
fun AuraDebugInspector(
    globalState: AuraGlobalState,
    onDismiss: () -> Unit,
    onSelectProfileOverride: (AuraPerformanceProfile?) -> Unit,
    modifier: Modifier = Modifier
) {
    AuraGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(AuraSpacing.md)
            .testTag("aura_debug_inspector"),
        shape = AuraShapes.roundedLg
    ) {
        Column(
            modifier = Modifier
                .padding(AuraSpacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Debug",
                        modifier = Modifier.size(18.dp),
                        tint = AuraColors.AuraWhite
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Text(
                        text = "AURA TELEMETRY & ENGINE",
                        style = AuraTypography.titleLarge
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Inspector",
                        tint = AuraColors.Silver
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Profile Overrides
            Text(
                text = "PERFORMANCE PROFILE (DYNAMIC / MANUAL)",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
            ) {
                ProfileChip(
                    title = "AUTO",
                    isSelected = false,
                    onClick = { onSelectProfileOverride(null) }
                )
                ProfileChip(
                    title = "FULL",
                    isSelected = globalState.performanceProfile == AuraPerformanceProfile.FULL,
                    onClick = { onSelectProfileOverride(AuraPerformanceProfile.FULL) }
                )
                ProfileChip(
                    title = "BALANCED",
                    isSelected = globalState.performanceProfile == AuraPerformanceProfile.BALANCED,
                    onClick = { onSelectProfileOverride(AuraPerformanceProfile.BALANCED) }
                )
                ProfileChip(
                    title = "LITE",
                    isSelected = globalState.performanceProfile == AuraPerformanceProfile.LITE,
                    onClick = { onSelectProfileOverride(AuraPerformanceProfile.LITE) }
                )
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Telemetry Grid
            Text(
                text = "SYSTEM METRICS",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )
            Spacer(modifier = Modifier.height(AuraSpacing.xs))

            val caps = globalState.deviceCapability
            MetricRow("Active Surface", globalState.currentSurface.name)
            MetricRow("Android API Level", "${caps.apiLevel}")
            MetricRow("Total System RAM", "${caps.totalRamMb} MB")
            MetricRow("Available RAM", "${caps.availableRamMb} MB")
            MetricRow("Low-RAM Device Flag", "${caps.isLowRamDevice}")
            MetricRow("Refresh Rate", "${caps.refreshRateHz.toInt()} Hz")
            MetricRow("RenderEffect Blur", if (caps.supportsRenderEffectBlur) "Supported" else "Fallback Canvas")
            MetricRow("Thermal State", caps.thermalStatus.name)
            MetricRow("Battery State", "${caps.batteryLevelPercent}% (Charging: ${caps.isBatteryCharging})")
            MetricRow("Power Save Mode", "${caps.isPowerSaveMode}")
            MetricRow("Reduced Motion", "${caps.isReducedMotionPreferred}")
            MetricRow("Display Resolution", "${caps.screenWidthPx} x ${caps.screenHeightPx} (${caps.screenDensityDpi} dpi)")

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Material Engine Status
            Text(
                text = "MATERIAL CONFIGURATION",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
            val mat = globalState.materialConfig
            MetricRow("Blur Radius", "${mat.blurRadiusDp} dp")
            MetricRow("Surface Opacity", "${(mat.surfaceAlpha * 100).toInt()}%")
            MetricRow("Border Opacity", "${(mat.borderAlpha * 100).toInt()}%")
            MetricRow("Live Blur State", if (mat.isLiveBlurEnabled) "ACTIVE" else "STATIC / CACHED")
        }
    }
}

@Composable
private fun ProfileChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AuraGlassButton(
        onClick = onClick,
        shape = AuraShapes.roundedSm
    ) {
        Text(
            text = title,
            style = AuraTypography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
        )
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = AuraTypography.bodyMedium, color = AuraColors.Silver)
        Text(text = value, style = AuraTypography.monoMetric, color = AuraColors.AuraWhite)
    }
}
