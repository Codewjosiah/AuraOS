package app.auraos.shell.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * AURA River Surface (Swipe from Left).
 * The fluid contextual stream of live activities, updates, and upcoming briefings.
 */
@Composable
fun AuraRiverSurface(
    globalState: AuraGlobalState,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_river_surface")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stream,
                        contentDescription = "River Stream",
                        tint = AuraColors.AuraWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.sm))
                    Text(
                        text = "RIVER",
                        style = AuraTypography.headlineLarge,
                        color = AuraColors.AuraWhite
                    )
                }

                AuraGlassPill(
                    onClick = onReturnHome,
                    modifier = Modifier.testTag("river_return_home")
                ) {
                    Text(
                        text = "HOME",
                        style = AuraTypography.labelSmall,
                        color = AuraColors.Silver
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Return Home",
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))
            Text(
                text = "CONTINUOUS CONTEXT STREAM",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Ambient Briefing Card
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SYSTEM STATUS",
                            style = AuraTypography.labelSmall,
                            color = AuraColors.Silver
                        )
                        Text(
                            text = "LIVE",
                            style = AuraTypography.monoMetric,
                            color = AuraColors.AuraWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    Text(
                        text = "AURA Interaction Layer Running",
                        style = AuraTypography.titleLarge,
                        color = AuraColors.AuraWhite
                    )
                    Spacer(modifier = Modifier.height(AuraSpacing.xxs))
                    Text(
                        text = "Batch 01 foundation active. Memory and thermal monitors synchronizing with hardware clock.",
                        style = AuraTypography.bodyMedium,
                        color = AuraColors.Silver
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Notification / Stream Foundation Card
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuraGlassSurface(
                        modifier = Modifier.size(40.dp),
                        shape = AuraShapes.roundedSm
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Stream",
                                tint = AuraColors.AuraWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(AuraSpacing.md))
                    Column {
                        Text(
                            text = "River Feed Stream",
                            style = AuraTypography.bodyLarge,
                            color = AuraColors.AuraWhite
                        )
                        Text(
                            text = "Clean interface ready for Batch 02 live activities & notifications.",
                            style = AuraTypography.bodyMedium,
                            color = AuraColors.Gray
                        )
                    }
                }
            }
        }
    }
}
