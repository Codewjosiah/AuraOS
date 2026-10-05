package app.auraos.material.glass

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.auraos.core.system.AuraWallpaperState
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraShapes

/**
 * Base Liquid Glass Surface component.
 * Combines depth, blur, transparency, environmental color bleed,
 * specular edge lighting, and physical spring touch compression.
 */
@Composable
fun AuraGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = AuraShapes.roundedMd,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    interactionState: AuraInteractionState = AuraInteractionState.RESTING,
    borderWidth: Dp = 1.dp,
    customConfig: AuraMaterialConfig? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val profile = Aura.performanceProfile
    val baseConfig = customConfig ?: Aura.materialConfig
    val effectiveConfig = if (materialType != null && materialType != baseConfig.materialType) {
        baseConfig.copy(materialType = materialType)
    } else {
        baseConfig
    }
    val lighting = Aura.lighting
    val reducedMotion = Aura.isReducedMotion

    // Physical touch compression animation
    val animatedScale by animateFloatAsState(
        targetValue = interactionState.scaleMultiplier * depth.scaleFactor,
        animationSpec = AuraMotion.springSnap(reducedMotion),
        label = "AuraSurfaceScale"
    )

    // Wallpaper contrast and color bleed adaptation
    val wallpaperState = AuraWallpaperState()

    val baseFill = AuraMaterialEngine.resolveGlassBaseColor(
        config = effectiveConfig,
        profile = profile,
        wallpaperState = wallpaperState,
        interactionState = interactionState
    )

    val borderBrush = AuraMaterialEngine.resolveBorderBrush(
        config = effectiveConfig,
        lighting = lighting,
        wallpaperState = wallpaperState,
        interactionState = interactionState
    )

    val effectiveBlurRadius = effectiveConfig.blurRadiusDp

    // Hardware RenderEffect blur modifier (API 31+)
    val blurGraphicsLayer = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        effectiveConfig.isLiveBlurEnabled &&
        effectiveBlurRadius > 0.5f &&
        profile.isFullBlurSupported
    ) {
        Modifier.graphicsLayer {
            try {
                renderEffect = RenderEffect.createBlurEffect(
                    effectiveBlurRadius,
                    effectiveBlurRadius,
                    Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            } catch (_: Exception) {}
        }
    } else {
        Modifier
    }

    // Atmospheric shadow
    val shadowModifier = if (depth.isElevated && profile.isLightingEffectsEnabled) {
        Modifier.shadow(
            elevation = depth.elevationDp.dp,
            shape = shape,
            clip = false,
            ambientColor = Color.Black.copy(alpha = depth.shadowAlpha * effectiveConfig.shadowStrength),
            spotColor = Color.Black.copy(alpha = depth.shadowAlpha * 1.2f * effectiveConfig.shadowStrength)
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
            .then(blurGraphicsLayer)
            .background(baseFill)
            .border(width = borderWidth, brush = borderBrush, shape = shape)
    ) {
        // Specular highlight sheen layer
        if (profile.isLightingEffectsEnabled && effectiveConfig.reflectionStrength > 0.05f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        AuraMaterialEngine.resolveSurfaceSheenBrush(
                            config = effectiveConfig,
                            lighting = lighting,
                            interactionState = interactionState
                        )
                    )
            )
        }

        content()
    }
}
