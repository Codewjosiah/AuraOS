package app.auraos.shell.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.material.glass.AuraGlassCard
import app.auraos.material.glass.AuraGlassPill
import app.auraos.material.glass.AuraGlassSurface
import app.auraos.shell.state.AuraGlobalState

/**
 * AURA Orb Surface (Swipe from Right).
 * Spatial intelligence core and contextual action plane.
 */
@Composable
fun AuraOrbSurface(
    globalState: AuraGlobalState,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_orb_surface")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AuraSpacing.lg)
        ) {
            Spacer(modifier = Modifier.height(AuraSpacing.xl))

            // Surface Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AuraGlassPill(
                    onClick = onReturnHome,
                    modifier = Modifier.testTag("orb_return_home")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Return Home",
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Text(
                        text = "HOME",
                        style = AuraTypography.labelSmall,
                        color = AuraColors.Silver
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ORB",
                        style = AuraTypography.headlineLarge,
                        color = AuraColors.AuraWhite
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.sm))
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Orb",
                        tint = AuraColors.AuraWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))
            Text(
                text = "CONTEXT & INTELLIGENCE CORE",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )

            Spacer(modifier = Modifier.height(AuraSpacing.lg))

            // Central Orb Lens Core Card
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AuraGlassSurface(
                        modifier = Modifier.size(72.dp),
                        shape = AuraShapes.pill
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Intelligence Core",
                                tint = AuraColors.AuraWhite,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AuraSpacing.md))

                    Text(
                        text = "AURA Spatial Plane",
                        style = AuraTypography.headlineMedium,
                        color = AuraColors.AuraWhite
                    )
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    Text(
                        text = "Hardware acceleration: ${if (globalState.deviceCapability.supportsHardwareAcceleration) "ACTIVE" else "SOFTWARE"}\nRefresh clock: ${globalState.deviceCapability.refreshRateHz.toInt()} Hz",
                        style = AuraTypography.bodyMedium,
                        color = AuraColors.Silver,
                        lineHeight = AuraTypography.bodyMedium.lineHeight
                    )
                }
            }
        }
    }
}
