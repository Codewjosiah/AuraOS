package app.auraos.material.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing

@Composable
fun AuraGlassPill(
    modifier: Modifier = Modifier,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val interactionState = if (isPressed) AuraInteractionState.PRESSED else AuraInteractionState.RESTING

    val clickableModifier = if (onClick != null) {
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
            .defaultMinSize(minHeight = 36.dp)
            .then(clickableModifier),
        shape = AuraShapes.pill,
        materialType = materialType,
        depth = depth,
        interactionState = interactionState
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AuraSpacing.md, vertical = AuraSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun AuraGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    onClick: (() -> Unit)? = null
) {
    AuraGlassPill(
        modifier = modifier,
        materialType = materialType,
        depth = depth,
        onClick = onClick
    ) {
        if (icon != null) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.defaultMinSize(minWidth = 13.dp, minHeight = 13.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 4.dp))
        }
        androidx.compose.material3.Text(
            text = text,
            style = app.auraos.design.theme.AuraTypography.labelSmall,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}
