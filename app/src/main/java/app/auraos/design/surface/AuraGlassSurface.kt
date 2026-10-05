package app.auraos.design.surface

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraInteractionState

/**
 * AuraGlassSurface composable in the design surface module.
 * Accepts AuraMaterialConfig, applies backdrop blur using RenderEffect on API 31+
 * or a performant fallback blur on older Android versions, handles state-based clipping,
 * and draws specular highlight borders.
 */
@Composable
fun AuraGlassSurface(
    modifier: Modifier = Modifier,
    config: AuraMaterialConfig = AuraMaterialConfig.LiquidDefault,
    shape: Shape = AuraShapes.roundedMd,
    depth: AuraDepth = config.depth,
    interactionState: AuraInteractionState = AuraInteractionState.RESTING,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val reducedMotion = Aura.isReducedMotion

    // Physical touch compression scale animation
    val animatedScale by animateFloatAsState(
        targetValue = interactionState.scaleMultiplier * depth.scaleFactor,
        animationSpec = AuraMotion.springSnap(reducedMotion),
        label = "AuraGlassSurfaceScale"
    )

    // Base translucent surface fill
    val effectiveAlpha = ((1.0f - config.transparency) * interactionState.alphaMultiplier).coerceIn(0.1f, 0.98f)
    val baseFillColor = AuraColors.NearBlack.copy(alpha = effectiveAlpha)

    // Specular highlight border brush based on borderOpacity and highlightStrength
    val effectiveBorderAlpha = (config.borderOpacity * interactionState.highlightBoost).coerceIn(0.05f, 0.95f)
    val topHighlight = Color.White.copy(
        alpha = (0.45f * config.highlightStrength * effectiveBorderAlpha * 2.5f).coerceIn(0.1f, 0.9f)
    )
    val midHighlight = Color.White.copy(
        alpha = (0.12f * config.highlightStrength * effectiveBorderAlpha).coerceIn(0.04f, 0.4f)
    )
    val bottomShadow = Color.Black.copy(
        alpha = (0.35f * config.shadowStrength).coerceIn(0.1f, 0.7f)
    )
    val borderBrush = Brush.verticalGradient(
        0.0f to topHighlight,
        0.35f to midHighlight,
        1.0f to bottomShadow
    )

    // Backdrop blur depending on Android API level
    val blurModifier = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        config.isLiveBlurEnabled &&
        config.blurRadius > 0.5f
    ) {
        // Hardware RenderEffect blur on Android 12+ (API 31+)
        Modifier.graphicsLayer {
            try {
                renderEffect = RenderEffect.createBlurEffect(
                    config.blurRadius,
                    config.blurRadius,
                    Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            } catch (_: Exception) {}
        }
    } else {
        // Performant fallback: clean translucent tint without expensive GPU shader passes
        Modifier
    }

    // Atmospheric depth shadow
    val shadowModifier = if (depth.isElevated) {
        Modifier.shadow(
            elevation = depth.elevationDp.dp,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = depth.shadowAlpha * config.shadowStrength),
            spotColor = Color.Black.copy(alpha = depth.shadowAlpha * 1.2f * config.shadowStrength)
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .then(shadowModifier)
            .clip(shape)
            .then(blurModifier)
            .background(baseFillColor)
            .border(width = borderWidth, brush = borderBrush, shape = shape)
    ) {
        // Surface sheen reflection
        if (config.reflectionStrength > 0.05f) {
            val sheenAlpha = (0.12f * config.reflectionStrength * interactionState.highlightBoost).coerceIn(0.02f, 0.35f)
            val sheenBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = sheenAlpha),
                    Color.White.copy(alpha = sheenAlpha * 0.2f),
                    Color.Transparent
                )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(sheenBrush)
            )
        }

        content()
    }
}
