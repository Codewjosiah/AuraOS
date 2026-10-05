package app.auraos.material.glass

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.auraos.design.animation.AuraMotion
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import kotlin.math.roundToInt

@Composable
fun AuraGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    label: String? = null,
    valueDisplay: String? = null,
    enabled: Boolean = true,
    materialType: AuraGlassMaterialType? = null
) {
    val view = LocalView.current
    var isDragging by remember { mutableStateOf(false) }
    val reducedMotion = Aura.isReducedMotion

    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AuraSpacing.xs)
    ) {
        if (label != null || valueDisplay != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = AuraTypography.labelSmall,
                        color = AuraColors.Silver
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (valueDisplay != null) {
                    Text(
                        text = valueDisplay,
                        style = AuraTypography.monoMetric,
                        color = AuraColors.AuraWhite
                    )
                }
            }
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val knobRadiusPx = with(LocalDensity.current) { 14.dp.toPx() }
            val trackRangePx = (widthPx - knobRadiusPx * 2).coerceAtLeast(1f)

            // Touch gesture detection
            val gestureModifier = if (enabled) {
                Modifier.pointerInput(trackRangePx) {
                    detectTapGestures { offset ->
                        val newFraction = ((offset.x - knobRadiusPx) / trackRangePx).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start)
                        try {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        } catch (_: Exception) {}
                        onValueChange(newValue)
                    }
                }.pointerInput(trackRangePx) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        val newFraction = ((change.position.x - knobRadiusPx) / trackRangePx).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                }
            } else {
                Modifier
            }

            // Inactive Track Base
            AuraGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                shape = AuraShapes.pill,
                materialType = AuraGlassMaterialType.OBSIDIAN,
                depth = AuraDepth.LEVEL_0
            ) {}

            // Active Track Fill
            val activeWidth = (normalizedValue * widthPx).coerceAtLeast(8f)
            Box(
                modifier = Modifier
                    .size(width = with(LocalDensity.current) { activeWidth.toDp() }, height = 8.dp)
                    .clip(AuraShapes.pill)
                    .background(AuraColors.AuraWhite.copy(alpha = 0.85f))
            )

            // Sliding Liquid Glass Knob
            val knobOffsetPx = (normalizedValue * trackRangePx)
            val knobInteractionState = when {
                !enabled -> AuraInteractionState.DISABLED
                isDragging -> AuraInteractionState.DRAGGED
                else -> AuraInteractionState.RESTING
            }

            AuraGlassSurface(
                modifier = Modifier
                    .offset { IntOffset(knobOffsetPx.roundToInt(), 0) }
                    .size(28.dp),
                shape = AuraShapes.pill,
                materialType = materialType ?: AuraGlassMaterialType.CRYSTAL,
                depth = if (isDragging) AuraDepth.LEVEL_3 else AuraDepth.LEVEL_2,
                interactionState = knobInteractionState
            ) {
                // Subtle center dot inside the knob
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(AuraShapes.pill)
                        .background(AuraColors.AuraWhite)
                        .align(Alignment.Center)
                )
            }

            // Invisible full-height touch overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(gestureModifier)
            )
        }
    }
}
