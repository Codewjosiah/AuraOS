package app.auraos.material.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes

@Composable
fun AuraGlassIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp,
    tint: Color = AuraColors.AuraWhite,
    shape: Shape = AuraShapes.roundedSm,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val interactionState = when {
        !enabled -> AuraInteractionState.DISABLED
        isPressed -> AuraInteractionState.PRESSED
        else -> AuraInteractionState.RESTING
    }

    val clickableModifier = if (onClick != null && enabled) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    AuraGlassSurface(
        modifier = modifier
            .size(size)
            .then(clickableModifier),
        shape = shape,
        materialType = materialType,
        depth = depth,
        interactionState = interactionState
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = if (enabled) tint else AuraColors.Gray
            )
        }
    }
}
