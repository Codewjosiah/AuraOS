package app.auraos.shell.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.material.glass.AuraGlassPill
import app.auraos.shell.state.AuraGlobalState

@Composable
fun AuraLivePillView(
    globalState: AuraGlobalState,
    onPillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AuraSpacing.md, vertical = AuraSpacing.xs),
        contentAlignment = Alignment.Center
    ) {
        AuraGlassPill(
            modifier = Modifier.testTag("aura_live_pill"),
            onClick = onPillClick
        ) {
            // Surface Indicator
            Text(
                text = "AURA",
                style = AuraTypography.labelSmall,
                color = AuraColors.AuraWhite
            )

            Spacer(modifier = Modifier.width(AuraSpacing.xs))
            Text(
                text = "•",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )
            Spacer(modifier = Modifier.width(AuraSpacing.xs))

            // Profile Tag
            val profileText = when (globalState.performanceProfile) {
                AuraPerformanceProfile.FULL -> "FULL"
                AuraPerformanceProfile.BALANCED -> "BALANCED"
                AuraPerformanceProfile.LITE -> "LITE"
            }
            Text(
                text = profileText,
                style = AuraTypography.labelSmall,
                color = AuraColors.NearWhite
            )

            Spacer(modifier = Modifier.width(AuraSpacing.sm))

            // Battery metric
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (globalState.batteryInfo.isCharging) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Charging",
                        modifier = Modifier.size(12.dp),
                        tint = AuraColors.AuraWhite
                    )
                }
                Text(
                    text = "${globalState.batteryInfo.levelPercent}%",
                    style = AuraTypography.monoMetric,
                    color = AuraColors.Silver
                )
            }

            Spacer(modifier = Modifier.width(AuraSpacing.xs))

            // Debug gear icon hint
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = "Debug Telemetry",
                modifier = Modifier.size(12.dp),
                tint = if (globalState.isDebugInspectorVisible) AuraColors.AuraWhite else AuraColors.Gray
            )
        }
    }
}
