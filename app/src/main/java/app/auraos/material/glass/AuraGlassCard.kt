package app.auraos.material.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = AuraShapes.roundedLg,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = AuraSpacing.md,
    content: @Composable BoxScope.() -> Unit
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
        modifier = modifier.then(clickableModifier),
        shape = shape,
        materialType = materialType,
        depth = depth,
        interactionState = interactionState
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
