package app.auraos.material.glass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.auraos.design.animation.AuraMotion
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import kotlin.math.roundToInt

@Composable
fun AuraGlassSheet(
    modifier: Modifier = Modifier,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_3,
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val reducedMotion = Aura.isReducedMotion
    val animatedOffsetY by animateFloatAsState(
        targetValue = dragOffsetY,
        animationSpec = AuraMotion.springFluid(reducedMotion),
        label = "SheetDragOffset"
    )

    AuraGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
            .draggable(
                state = rememberDraggableState { delta ->
                    dragOffsetY = (dragOffsetY + delta).coerceAtLeast(0f)
                },
                orientation = Orientation.Vertical,
                onDragStopped = {
                    if (dragOffsetY > 180f) {
                        onDismissRequest?.invoke()
                    }
                    dragOffsetY = 0f
                }
            ),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        materialType = materialType,
        depth = depth
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AuraSpacing.lg, vertical = AuraSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Handle Indicator
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(AuraShapes.pill)
                    .background(AuraColors.GlassSpecularSubtle)
            )

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            content()
        }
    }
}
