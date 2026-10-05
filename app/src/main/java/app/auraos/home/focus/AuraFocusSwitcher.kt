package app.auraos.home.focus

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.model.AuraFocusContext
import app.auraos.material.glass.AuraDepth

/**
 * Two-Finger Pinch triggered Focus Switcher overlay.
 * Allows transitioning the Home environment between Normal, Study, Work, Gaming, and Personal modes.
 */
@Composable
fun AuraFocusSwitcher(
    currentFocus: AuraFocusContext,
    onSelectFocus: (AuraFocusContext) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_focus_switcher")
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AuraGlassSurface(
            modifier = Modifier
                .width(340.dp)
                .padding(AuraSpacing.md)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps */ },
            shape = RoundedCornerShape(32.dp),
            depth = AuraDepth.LEVEL_3,
            config = AuraMaterialConfig.LiquidDefault.copy(
                transparency = 0.18f,
                blurRadius = 36f,
                highlightStrength = 0.75f,
                borderOpacity = 0.4f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuraSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FOCUS CONTEXTS",
                            style = AuraTypography.labelSmall,
                            color = AuraColors.Silver
                        )
                        Text(
                            text = "Select Home Mode",
                            style = AuraTypography.titleMedium,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Focus Switcher",
                            tint = AuraColors.Silver
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AuraSpacing.md))

                // List of Focus Options
                AuraFocusContext.entries.forEach { focus ->
                    val isSelected = focus == currentFocus
                    val icon = getFocusIcon(focus)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .semantics { contentDescription = "Focus mode ${focus.displayName}" }
                            .clickable {
                                onSelectFocus(focus)
                                onDismiss()
                            }
                            .background(
                                color = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = AuraSpacing.md, vertical = AuraSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(AuraSpacing.md))

                            Column {
                                Text(
                                    text = focus.displayName,
                                    style = AuraTypography.titleMedium.copy(fontSize = 14.sp),
                                    color = Color.White
                                )
                                Text(
                                    text = focus.description,
                                    style = AuraTypography.bodySmall.copy(fontSize = 11.sp),
                                    color = AuraColors.Gray
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getFocusIcon(focus: AuraFocusContext): ImageVector {
    return when (focus) {
        AuraFocusContext.NORMAL -> Icons.Default.Radio
        AuraFocusContext.STUDY -> Icons.Default.Book
        AuraFocusContext.WORK -> Icons.Default.Work
        AuraFocusContext.GAMING -> Icons.Default.Gamepad
        AuraFocusContext.PERSONAL -> Icons.Default.Person
    }
}
