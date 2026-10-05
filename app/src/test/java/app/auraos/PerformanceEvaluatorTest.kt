package app.auraos

import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.performance.PerformanceEvaluator
import app.auraos.core.platform.DeviceCapability
import org.junit.Assert.assertEquals
import org.junit.Test

class PerformanceEvaluatorTest {

    private val evaluator = PerformanceEvaluator()

    @Test
    fun `flagship device with abundant RAM and healthy thermals resolves to FULL`() {
        val flagship = DeviceCapability(
            apiLevel = 34,
            totalRamMb = 12000,
            availableRamMb = 8000,
            isLowRamDevice = false,
            refreshRateHz = 120f,
            supportsRenderEffectBlur = true,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = false,
            batteryLevelPercent = 90,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.NORMAL,
            isReducedMotionPreferred = false,
            screenWidthPx = 1440,
            screenHeightPx = 3120,
            screenDensityDpi = 560
        )

        val profile = evaluator.evaluateProfile(flagship)
        assertEquals(AuraPerformanceProfile.FULL, profile)
    }

    @Test
    fun `power save mode immediately enforces LITE profile`() {
        val powerSaveFlagship = DeviceCapability(
            apiLevel = 34,
            totalRamMb = 12000,
            availableRamMb = 8000,
            isLowRamDevice = false,
            refreshRateHz = 120f,
            supportsRenderEffectBlur = true,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = true,
            batteryLevelPercent = 85,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.NORMAL,
            isReducedMotionPreferred = false,
            screenWidthPx = 1440,
            screenHeightPx = 3120,
            screenDensityDpi = 560
        )

        val profile = evaluator.evaluateProfile(powerSaveFlagship)
        assertEquals(AuraPerformanceProfile.LITE, profile)
    }

    @Test
    fun `critical battery enforces LITE profile`() {
        val lowBatteryDevice = DeviceCapability(
            apiLevel = 33,
            totalRamMb = 8000,
            availableRamMb = 4000,
            isLowRamDevice = false,
            refreshRateHz = 90f,
            supportsRenderEffectBlur = true,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = false,
            batteryLevelPercent = 10,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.NORMAL,
            isReducedMotionPreferred = false,
            screenWidthPx = 1080,
            screenHeightPx = 2400,
            screenDensityDpi = 400
        )

        val profile = evaluator.evaluateProfile(lowBatteryDevice)
        assertEquals(AuraPerformanceProfile.LITE, profile)
    }

    @Test
    fun `severe thermal status enforces LITE profile`() {
        val hotDevice = DeviceCapability(
            apiLevel = 34,
            totalRamMb = 8000,
            availableRamMb = 4000,
            isLowRamDevice = false,
            refreshRateHz = 120f,
            supportsRenderEffectBlur = true,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = false,
            batteryLevelPercent = 80,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.SEVERE,
            isReducedMotionPreferred = false,
            screenWidthPx = 1080,
            screenHeightPx = 2400,
            screenDensityDpi = 400
        )

        val profile = evaluator.evaluateProfile(hotDevice)
        assertEquals(AuraPerformanceProfile.LITE, profile)
    }

    @Test
    fun `moderate thermals gracefully downgrade to BALANCED profile`() {
        val warmDevice = DeviceCapability(
            apiLevel = 34,
            totalRamMb = 8000,
            availableRamMb = 4000,
            isLowRamDevice = false,
            refreshRateHz = 120f,
            supportsRenderEffectBlur = true,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = false,
            batteryLevelPercent = 80,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.MODERATE,
            isReducedMotionPreferred = false,
            screenWidthPx = 1080,
            screenHeightPx = 2400,
            screenDensityDpi = 400
        )

        val profile = evaluator.evaluateProfile(warmDevice)
        assertEquals(AuraPerformanceProfile.BALANCED, profile)
    }

    @Test
    fun `manual override supersedes dynamic evaluation`() {
        val lowEndDevice = DeviceCapability(
            apiLevel = 26,
            totalRamMb = 2048,
            availableRamMb = 512,
            isLowRamDevice = true,
            refreshRateHz = 60f,
            supportsRenderEffectBlur = false,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = true,
            batteryLevelPercent = 5,
            isBatteryCharging = false,
            thermalStatus = DeviceCapability.ThermalStatus.SEVERE,
            isReducedMotionPreferred = false,
            screenWidthPx = 720,
            screenHeightPx = 1280,
            screenDensityDpi = 320
        )

        val profile = evaluator.evaluateProfile(lowEndDevice, manualOverride = AuraPerformanceProfile.FULL)
        assertEquals(AuraPerformanceProfile.FULL, profile)
    }
}
