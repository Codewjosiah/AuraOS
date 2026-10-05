package app.auraos

import app.auraos.design.material.AuraMaterialConfig
import app.auraos.material.glass.AuraGlassMaterialType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuraMaterialConfigPropertiesTest {

    @Test
    fun `AuraMaterialConfig properties match specification`() {
        val config = AuraMaterialConfig(
            blurRadius = 18f,
            transparency = 0.25f,
            saturation = 1.2f,
            colorBleed = 0.45f,
            refraction = 0.35f,
            highlightStrength = 0.55f,
            borderOpacity = 0.22f,
            shadowStrength = 0.38f
        )

        assertEquals(18f, config.blurRadius, 0.001f)
        assertEquals(0.25f, config.transparency, 0.001f)
        assertEquals(1.2f, config.saturation, 0.001f)
        assertEquals(0.45f, config.colorBleed, 0.001f)
        assertEquals(0.35f, config.refraction, 0.001f)
        assertEquals(0.55f, config.highlightStrength, 0.001f)
        assertEquals(0.22f, config.borderOpacity, 0.001f)
        assertEquals(0.38f, config.shadowStrength, 0.001f)
    }

    @Test
    fun `LiquidDefault provides standard Liquid material configuration`() {
        val liquid = AuraMaterialConfig.LiquidDefault

        assertNotNull(liquid)
        assertEquals(AuraGlassMaterialType.LIQUID, liquid.materialType)
        assertTrue(liquid.blurRadius > 0f)
        assertTrue(liquid.transparency in 0.0f..1.0f)
        assertTrue(liquid.borderOpacity in 0.0f..1.0f)
        assertTrue(liquid.highlightStrength in 0.0f..1.0f)
        assertTrue(liquid.isLiveBlurEnabled)
    }
}
