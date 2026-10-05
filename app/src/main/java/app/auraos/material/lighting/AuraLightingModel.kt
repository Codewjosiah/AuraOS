package app.auraos.material.lighting

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import app.auraos.design.theme.AuraColors

/**
 * Global lighting model simulating an elevated overhead specular light source.
 * Produces coherent rim reflections, top-edge highlights, and bottom-edge shadow cues.
 */
@Immutable
data class AuraLightingModel(
    val lightAngleDegrees: Float = 135f, // Top-left origin
    val lightElevation: Float = 45f,
    val specularIntensity: Float = 0.5f,
    val ambientIntensity: Float = 0.35f,
    val rimLightColor: Color = AuraColors.GlassRimLight
) {
    /**
     * Computes the specular gradient brush for a glass surface border.
     */
    fun computeBorderBrush(alphaMultiplier: Float = 1f): Brush {
        val topHighlight = Color.White.copy(alpha = 0.35f * specularIntensity * alphaMultiplier)
        val midSubtle = Color.White.copy(alpha = 0.12f * specularIntensity * alphaMultiplier)
        val bottomShadow = Color.Black.copy(alpha = 0.25f * alphaMultiplier)

        return Brush.verticalGradient(
            0.0f to topHighlight,
            0.4f to midSubtle,
            1.0f to bottomShadow
        )
    }

    /**
     * Computes the surface specular refraction brush.
     */
    fun computeSurfaceBrush(baseColor: Color, alphaMultiplier: Float = 1f): Brush {
        val topSheen = Color.White.copy(alpha = 0.08f * specularIntensity * alphaMultiplier)
        val bottomSheen = Color.Transparent

        return Brush.linearGradient(
            colors = listOf(topSheen, bottomSheen),
            start = Offset(0f, 0f),
            end = Offset(400f, 600f)
        )
    }

    companion object {
        val Default = AuraLightingModel()
    }
}
