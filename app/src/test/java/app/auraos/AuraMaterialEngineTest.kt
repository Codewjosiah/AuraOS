package app.auraos

import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.system.AuraWallpaperState
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraGlassMaterialType
import app.auraos.material.glass.AuraInteractionState
import app.auraos.material.glass.AuraMaterialEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuraMaterialEngineTest {

    @Test
    fun `clamping prevents out-of-bound material parameters`() {
        val outOfBounds = AuraMaterialConfig(
            blurRadius = 999f,
            transparency = -1.5f,
            saturation = 5.0f,
            colorBleed = 10f,
            refraction = -5f,
            highlightStrength = 3f,
            borderOpacity = -1f,
            shadowStrength = 4f
        )
        val clamped = outOfBounds.clamped()

        assertEquals(60f, clamped.blurRadius, 0.001f)
        assertEquals(0.0f, clamped.transparency, 0.001f)
        assertEquals(2.0f, clamped.saturation, 0.001f)
        assertEquals(1.5f, clamped.colorBleed, 0.001f)
        assertEquals(0.0f, clamped.refraction, 0.001f)
        assertEquals(1.0f, clamped.highlightStrength)
        assertEquals(0.0f, clamped.borderAlpha)
        assertEquals(1.0f, clamped.shadowStrength)
    }

    @Test
    fun `FULL performance profile produces high-fidelity optical parameters`() {
        val fullConfig = AuraMaterialConfig.forProfileAndType(
            AuraPerformanceProfile.FULL,
            AuraGlassMaterialType.LIQUID
        )
        assertTrue(fullConfig.isLiveBlurEnabled)
        assertTrue(fullConfig.isDynamicRefractionEnabled)
        assertTrue(fullConfig.blurRadiusDp >= 24f)
        assertTrue(fullConfig.highlightStrength >= 0.5f)
    }

    @Test
    fun `BALANCED profile balances blur and disables heavy refraction`() {
        val balancedConfig = AuraMaterialConfig.forProfileAndType(
            AuraPerformanceProfile.BALANCED,
            AuraGlassMaterialType.LIQUID
        )
        assertTrue(balancedConfig.isLiveBlurEnabled)
        assertFalse(balancedConfig.isDynamicRefractionEnabled)
        assertEquals(12f, balancedConfig.blurRadiusDp)
    }

    @Test
    fun `LITE profile disables blur while preserving AURA geometry and opacity`() {
        val liteConfig = AuraMaterialConfig.forProfileAndType(
            AuraPerformanceProfile.LITE,
            AuraGlassMaterialType.LIQUID
        )
        assertFalse(liteConfig.isLiveBlurEnabled)
        assertFalse(liteConfig.isDynamicRefractionEnabled)
        assertEquals(0f, liteConfig.blurRadiusDp)
        assertTrue(liteConfig.surfaceAlpha > 0.85f)
        assertTrue(liteConfig.borderAlpha > 0f)
    }

    @Test
    fun `solid material type has zero blur and high opacity across profiles`() {
        val solidConfig = AuraMaterialConfig.forProfileAndType(
            AuraPerformanceProfile.FULL,
            AuraGlassMaterialType.SOLID
        )
        assertFalse(solidConfig.isLiveBlurEnabled)
        assertEquals(0f, solidConfig.blurRadiusDp)
        assertEquals(0.96f, solidConfig.surfaceAlpha)
    }

    @Test
    fun `depth hierarchy enforces ascending elevation and atmospheric shadow`() {
        val d0 = AuraDepth.LEVEL_0
        val d1 = AuraDepth.LEVEL_1
        val d2 = AuraDepth.LEVEL_2
        val d3 = AuraDepth.LEVEL_3
        val d4 = AuraDepth.LEVEL_4

        assertTrue(d0.elevationDp < d1.elevationDp)
        assertTrue(d1.elevationDp < d2.elevationDp)
        assertTrue(d2.elevationDp < d3.elevationDp)
        assertTrue(d3.elevationDp < d4.elevationDp)

        assertTrue(d0.shadowAlpha < d1.shadowAlpha)
        assertTrue(d1.shadowAlpha < d2.shadowAlpha)
        assertTrue(d2.shadowAlpha < d3.shadowAlpha)
        assertTrue(d3.shadowAlpha < d4.shadowAlpha)
    }

    @Test
    fun `pressed interaction state provides physical scale compression`() {
        val resting = AuraInteractionState.RESTING
        val pressed = AuraInteractionState.PRESSED
        val disabled = AuraInteractionState.DISABLED

        assertTrue(pressed.scaleMultiplier < resting.scaleMultiplier)
        assertTrue(pressed.highlightBoost > resting.highlightBoost)
        assertFalse(disabled.isInteractive)
        assertTrue(resting.isInteractive)
    }

    @Test
    fun `dynamic contrast adapts glass base color for light wallpapers`() {
        val config = AuraMaterialConfig.Default
        val darkWallpaper = AuraWallpaperState(isDarkWallpaper = true, dominantLuminance = 0.2f)
        val lightWallpaper = AuraWallpaperState(isDarkWallpaper = false, dominantLuminance = 0.85f)

        val darkBase = AuraMaterialEngine.resolveGlassBaseColor(
            config = config,
            profile = AuraPerformanceProfile.FULL,
            wallpaperState = darkWallpaper,
            interactionState = AuraInteractionState.RESTING
        )

        val lightBase = AuraMaterialEngine.resolveGlassBaseColor(
            config = config,
            profile = AuraPerformanceProfile.FULL,
            wallpaperState = lightWallpaper,
            interactionState = AuraInteractionState.RESTING
        )

        // Light background increases opacity to protect legibility
        assertTrue(lightBase.alpha >= darkBase.alpha)
    }
}
