package app.auraos.material.glass

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.auraos.design.animation.AuraMotion
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography

data class AuraNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector? = null
)

@Composable
fun AuraGlassNavigation(
    items: List<AuraNavItem>,
    selectedId: String,
    onItemSelected: (AuraNavItem) -> Unit,
    modifier: Modifier = Modifier,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_2
) {
    val view = LocalView.current
    val reducedMotion = Aura.isReducedMotion
    val selectedIndex = items.indexOfFirst { it.id == selectedId }.coerceAtLeast(0)

    AuraGlassSurface(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp),
        shape = AuraShapes.pill,
        materialType = materialType,
        depth = depth
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(AuraSpacing.xxs)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalItems = items.size.coerceAtLeast(1)
            val segmentWidth = maxWidth / totalItems

            // Animated sliding selection capsule
            val indicatorOffset by animateDpAsState(
                targetValue = segmentWidth * selectedIndex,
                animationSpec = AuraMotion.springFluid(reducedMotion),
                label = "NavIndicatorOffset"
            )

            AuraGlassSurface(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(segmentWidth)
                    .defaultMinSize(minHeight = 48.dp),
                shape = AuraShapes.pill,
                materialType = AuraGlassMaterialType.CRYSTAL,
                depth = AuraDepth.LEVEL_2
            ) {}

            // Items Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.id == selectedId
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .semantics {
                                role = Role.Tab
                                selected = isSelected
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    if (!isSelected) {
                                        try {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        } catch (_: Exception) {}
                                        onItemSelected(item)
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isSelected) AuraColors.AuraWhite else AuraColors.Gray
                                )
                                Spacer(modifier = Modifier.width(AuraSpacing.xxs))
                            }
                            Text(
                                text = item.label,
                                style = AuraTypography.labelSmall,
                                color = if (isSelected) AuraColors.AuraWhite else AuraColors.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
