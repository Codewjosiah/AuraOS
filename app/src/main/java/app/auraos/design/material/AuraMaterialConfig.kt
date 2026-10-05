package app.auraos.design.material

import androidx.compose.runtime.Immutable
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraGlassMaterialType

/**
 * Universal AURA material configuration specifying optical properties
 * of the liquid glass substrate across surfaces.
 */
@Immutable
data class AuraMaterialConfig(
    val blurRadius: Float = 20f,
    val transparency: Float = 0.28f,
    val saturation: Float = 1.1f,
    val colorBleed: Float = 0.5f,
    val refraction: Float = 0.4f,
    val highlightStrength: Float = 0.5f,
    val borderOpacity: Float = 0.24f,
    val shadowStrength: Float = 0.35f,
    val depth: AuraDepth = AuraDepth.LEVEL_1,
    val reflectionStrength: Float = 0.45f,
    val lightingStrength: Float = 0.6f,
    val animationScale: Float = 1.0f,
    val materialType: AuraGlassMaterialType = AuraGlassMaterialType.LIQUID,
    val isLiveBlurEnabled: Boolean = true,
    val isDynamicRefractionEnabled: Boolean = true
) {
    // Backward compatibility accessors
    val blurRadiusDp: Float get() = blurRadius
    val surfaceAlpha: Float get() = (1.0f - transparency).coerceIn(0.1f, 1.0f)
    val colorBleedStrength: Float get() = colorBleed
    val refractionStrength: Float get() = refraction
    val borderAlpha: Float get() = borderOpacity

    /**
     * Ensures all material parameters are within valid physical optical ranges.
     */
    fun clamped(): AuraMaterialConfig {
        return copy(
            blurRadius = blurRadius.coerceIn(0f, 60f),
            transparency = transparency.coerceIn(0.0f, 0.9f),
            saturation = saturation.coerceIn(0.5f, 2.0f),
            colorBleed = colorBleed.coerceIn(0.0f, 1.5f),
            refraction = refraction.coerceIn(0.0f, 1.0f),
            highlightStrength = highlightStrength.coerceIn(0.0f, 1.0f),
            borderOpacity = borderOpacity.coerceIn(0.0f, 1.0f),
            shadowStrength = shadowStrength.coerceIn(0.0f, 1.0f),
            reflectionStrength = reflectionStrength.coerceIn(0.0f, 1.0f),
            lightingStrength = lightingStrength.coerceIn(0.0f, 1.0f),
            animationScale = animationScale.coerceIn(0.1f, 2.0f)
        )
    }

    companion object {
        /**
         * Default configuration object for the Liquid material type.
         */
        val LiquidDefault = AuraMaterialConfig(
            blurRadius = 24f,
            transparency = 0.28f,
            saturation = 1.15f,
            colorBleed = 0.6f,
            refraction = 0.5f,
            highlightStrength = 0.6f,
            borderOpacity = 0.28f,
            shadowStrength = 0.40f,
            depth = AuraDepth.LEVEL_1,
            reflectionStrength = 0.55f,
            lightingStrength = 0.7f,
            materialType = AuraGlassMaterialType.LIQUID,
            isLiveBlurEnabled = true,
            isDynamicRefractionEnabled = true
        )

        val Default = LiquidDefault

        fun forProfileAndType(
            profile: AuraPerformanceProfile,
            type: AuraGlassMaterialType = AuraGlassMaterialType.LIQUID
        ): AuraMaterialConfig {
            val base = when (profile) {
                AuraPerformanceProfile.FULL -> AuraMaterialConfig(
                    blurRadius = 24f * type.blurMultiplier,
                    transparency = 1.0f - type.defaultAlpha,
                    saturation = 1.15f,
                    colorBleed = 0.6f * type.colorBleedMultiplier,
                    refraction = 0.5f,
                    highlightStrength = 0.6f * type.specularMultiplier,
                    borderOpacity = 0.28f * type.borderMultiplier,
                    shadowStrength = 0.40f,
                    reflectionStrength = 0.55f,
                    lightingStrength = 0.7f,
                    materialType = type,
                    isLiveBlurEnabled = type != AuraGlassMaterialType.SOLID,
                    isDynamicRefractionEnabled = true
                )

                AuraPerformanceProfile.BALANCED -> AuraMaterialConfig(
                    blurRadius = 12f * type.blurMultiplier,
                    transparency = (1.0f - (type.defaultAlpha + 0.10f)).coerceAtLeast(0.05f),
                    saturation = 1.0f,
                    colorBleed = 0.35f * type.colorBleedMultiplier,
                    refraction = 0.25f,
                    highlightStrength = 0.45f * type.specularMultiplier,
                    borderOpacity = 0.20f * type.borderMultiplier,
                    shadowStrength = 0.25f,
                    reflectionStrength = 0.35f,
                    lightingStrength = 0.5f,
                    materialType = type,
                    isLiveBlurEnabled = type != AuraGlassMaterialType.SOLID,
                    isDynamicRefractionEnabled = false
                )

                AuraPerformanceProfile.LITE -> AuraMaterialConfig(
                    blurRadius = 0f,
                    transparency = (1.0f - (type.defaultAlpha + 0.18f)).coerceAtLeast(0.02f),
                    saturation = 1.0f,
                    colorBleed = 0.1f * type.colorBleedMultiplier,
                    refraction = 0.0f,
                    highlightStrength = 0.35f * type.specularMultiplier,
                    borderOpacity = 0.16f * type.borderMultiplier,
                    shadowStrength = 0.18f,
                    reflectionStrength = 0.2f,
                    lightingStrength = 0.4f,
                    materialType = type,
                    isLiveBlurEnabled = false,
                    isDynamicRefractionEnabled = false
                )
            }
            return base.clamped()
        }

        fun forProfile(profile: AuraPerformanceProfile): AuraMaterialConfig {
            return forProfileAndType(profile, AuraGlassMaterialType.LIQUID)
        }
    }
}
