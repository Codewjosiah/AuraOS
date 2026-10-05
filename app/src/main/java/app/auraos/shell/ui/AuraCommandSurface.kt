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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
 * AURA Command Palette (Swipe down from center).
 * Universal system command and fast launcher.
 */
@Composable
fun AuraCommandSurface(
    globalState: AuraGlobalState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOpenApps: () -> Unit,
    onOpenDebug: () -> Unit,
    onOpenGallery: () -> Unit,
    onCloseCommand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_command_surface")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AuraSpacing.lg)
        ) {
            Spacer(modifier = Modifier.height(AuraSpacing.xl))

            // Command Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Command",
                        tint = AuraColors.AuraWhite,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.sm))
                    Text(
                        text = "COMMAND",
                        style = AuraTypography.headlineLarge,
                        color = AuraColors.AuraWhite
                    )
                }

                AuraGlassPill(
                    onClick = onCloseCommand,
                    modifier = Modifier.testTag("command_close_pill")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Command",
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Text(
                        text = "CLOSE",
                        style = AuraTypography.labelSmall,
                        color = AuraColors.Silver
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Command Input Box
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AuraShapes.roundedLg,
                contentPadding = AuraSpacing.xs
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = AuraSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Input",
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.sm))
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text("Type command or action...", style = AuraTypography.bodyLarge, color = AuraColors.MidGray)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = AuraColors.AuraWhite,
                            unfocusedTextColor = AuraColors.AuraWhite
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("command_input_field")
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.lg))
            Text(
                text = "QUICK ACTIONS",
                style = AuraTypography.labelSmall,
                color = AuraColors.Gray
            )
            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Action Items
            CommandActionItem(
                icon = Icons.Default.Apps,
                title = "Launch Application Drawer",
                subtitle = "Browse all installed apps on device",
                onClick = onOpenApps
            )

            Spacer(modifier = Modifier.height(AuraSpacing.xs))

            CommandActionItem(
                icon = Icons.Default.BugReport,
                title = "AURA Engine Telemetry",
                subtitle = "Inspect hardware specs and toggle performance profiles",
                onClick = onOpenDebug
            )

            Spacer(modifier = Modifier.height(AuraSpacing.xs))

            CommandActionItem(
                icon = Icons.Default.Widgets,
                title = "Liquid Glass Material Gallery",
                subtitle = "Test all 9 glass components across dynamic backdrops & profiles",
                onClick = onOpenGallery
            )
        }
    }
}

@Composable
private fun CommandActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    AuraGlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AuraGlassSurface(
                modifier = Modifier.size(40.dp),
                shape = AuraShapes.roundedSm
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = AuraColors.AuraWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(AuraSpacing.md))
            Column {
                Text(text = title, style = AuraTypography.bodyLarge, color = AuraColors.AuraWhite)
                Text(text = subtitle, style = AuraTypography.bodyMedium, color = AuraColors.Gray)
            }
        }
    }
}
