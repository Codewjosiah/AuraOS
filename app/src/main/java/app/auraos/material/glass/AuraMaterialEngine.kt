package app.auraos.material.glass

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.system.AuraWallpaperState
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.material.lighting.AuraLightingModel

/**
 * Core Liquid Glass Material Engine.
 * Resolves optical physics: backdrop blur, ambient color bleed, dynamic contrast,
 * specular reflection, refraction-like rim gradients, and physical touch response.
 */
object AuraMaterialEngine {

    /**
     * Resolves the primary background fill color, incorporating environmental color bleed
     * from the wallpaper and dynamic contrast adjustments.
     */
    fun resolveGlassBaseColor(
        config: AuraMaterialConfig,
        profile: AuraPerformanceProfile,
        wallpaperState: AuraWallpaperState,
        interactionState: AuraInteractionState
    ): Color {
        val isDarkBg = wallpaperState.isDarkWallpaper
        val baseAlpha = (config.surfaceAlpha * interactionState.alphaMultiplier).coerceIn(0.1f, 0.98f)

        // Environmental color bleed from wallpaper
        val bleedColor = if (config.colorBleedStrength > 0.05f) {
            Color(wallpaperState.primaryColorArgb).copy(alpha = 0.12f * config.colorBleedStrength)
        } else {
            Color.Transparent
        }

        return if (isDarkBg) {
            when (config.materialType) {
                AuraGlassMaterialType.OBSIDIAN -> AuraColors.Obsidian.copy(alpha = baseAlpha)
                AuraGlassMaterialType.CRYSTAL -> AuraColors.NearBlack.copy(alpha = (baseAlpha * 0.8f).coerceAtLeast(0.3f))
                AuraGlassMaterialType.FROST -> AuraColors.Carbon.copy(alpha = (baseAlpha * 1.1f).coerceAtMost(0.96f))
                AuraGlassMaterialType.AURORA -> {
                    // Blend environmental color bleed strongly
                    if (bleedColor != Color.Transparent) bleedColor.copy(alpha = baseAlpha)
                    else AuraColors.NearBlack.copy(alpha = baseAlpha)
                }
                AuraGlassMaterialType.SOLID -> AuraColors.Obsidian.copy(alpha = 0.96f)
                AuraGlassMaterialType.LIQUID -> AuraColors.NearBlack.copy(alpha = baseAlpha)
            }
        } else {
            // Light background: Increase opacity and dark tint to protect legibility
            when (config.materialType) {
                AuraGlassMaterialType.SOLID -> AuraColors.NearBlack.copy(alpha = 0.90f)
                else -> AuraColors.Obsidian.copy(alpha = (baseAlpha * 1.15f).coerceAtMost(0.95f))
            }
        }
    }

    /**
     * Computes the compound border brush for glass edges (specular highlight on top-left,
     * ambient shadow rim on bottom-right).
     */
    fun resolveBorderBrush(
        config: AuraMaterialConfig,
        lighting: AuraLightingModel,
        wallpaperState: AuraWallpaperState,
        interactionState: AuraInteractionState
    ): Brush {
        val effectiveBorderAlpha = (config.borderAlpha * interactionState.highlightBoost).coerceIn(0.05f, 0.95f)
        val specularBoost = config.highlightStrength

        val topSpecular = Color.White.copy(
            alpha = (0.40f * specularBoost * effectiveBorderAlpha * 2.5f).coerceIn(0.1f, 0.9f)
        )
        val midSubtle = Color.White.copy(
            alpha = (0.12f * specularBoost * effectiveBorderAlpha).coerceIn(0.04f, 0.4f)
        )
        val bottomShadow = if (wallpaperState.isDarkWallpaper) {
            Color.Black.copy(alpha = (0.35f * config.shadowStrength).coerceIn(0.1f, 0.7f))
        } else {
            Color.Black.copy(alpha = (0.50f * config.shadowStrength).coerceIn(0.2f, 0.85f))
        }

        return Brush.verticalGradient(
            0.0f to topSpecular,
            0.35f to midSubtle,
            1.0f to bottomShadow
        )
    }

    /**
     * Computes the surface specular sheen simulating overhead ambient light reflection.
     */
    fun resolveSurfaceSheenBrush(
        config: AuraMaterialConfig,
        lighting: AuraLightingModel,
        interactionState: AuraInteractionState
    ): Brush {
        val sheenAlpha = (0.15f * config.reflectionStrength * lighting.specularIntensity * interactionState.highlightBoost)
            .coerceIn(0.02f, 0.35f)

        return Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = sheenAlpha),
                Color.White.copy(alpha = sheenAlpha * 0.2f),
                Color.Transparent
            ),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    }
}
