package app.auraos

import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.shell.state.AuraGlobalState
import app.auraos.shell.state.AuraSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuraShellStateTest {

    @Test
    fun `default global state starts at HOME surface with closed drawer`() {
        val state = AuraGlobalState()
        assertEquals(AuraSurface.HOME, state.currentSurface)
        assertFalse(state.isAppDrawerExpanded)
        assertFalse(state.isDebugInspectorVisible)
    }

    @Test
    fun `material config adapts according to performance profile`() {
        val fullConfig = AuraMaterialConfig.forProfile(AuraPerformanceProfile.FULL)
        assertTrue(fullConfig.isLiveBlurEnabled)
        assertEquals(24f, fullConfig.blurRadiusDp)

        val balancedConfig = AuraMaterialConfig.forProfile(AuraPerformanceProfile.BALANCED)
        assertTrue(balancedConfig.isLiveBlurEnabled)
        assertEquals(12f, balancedConfig.blurRadiusDp)

        val liteConfig = AuraMaterialConfig.forProfile(AuraPerformanceProfile.LITE)
        assertFalse(liteConfig.isLiveBlurEnabled)
        assertEquals(0f, liteConfig.blurRadiusDp)
    }

    @Test
    fun `auxiliary surfaces are classified accurately`() {
        assertFalse(AuraSurface.HOME.isAuxiliarySurface)
        assertTrue(AuraSurface.RIVER.isAuxiliarySurface)
        assertTrue(AuraSurface.ORB.isAuxiliarySurface)
        assertTrue(AuraSurface.COMMAND.isAuxiliarySurface)
    }
}
