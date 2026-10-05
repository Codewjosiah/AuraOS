package app.auraos.core.performance

import app.auraos.core.platform.DeviceCapability

/**
 * Pure evaluator logic determining the optimal AuraPerformanceProfile
 * from device capabilities and real-time system pressures.
 */
class PerformanceEvaluator {

    /**
     * Determines the optimal profile given hardware specs and current dynamic conditions.
     */
    fun evaluateProfile(
        capabilities: DeviceCapability,
        isSevereMemoryPressure: Boolean = false,
        sustainedFpsDegradation: Boolean = false,
        manualOverride: AuraPerformanceProfile? = null
    ): AuraPerformanceProfile {
        if (manualOverride != null) {
            return manualOverride
        }

        // 1. Extreme pressures enforce LITE mode immediately
        if (capabilities.isPowerSaveMode) {
            return AuraPerformanceProfile.LITE
        }

        if (capabilities.batteryLevelPercent <= 15 && !capabilities.isBatteryCharging) {
            return AuraPerformanceProfile.LITE
        }

        if (capabilities.thermalStatus in setOf(
                DeviceCapability.ThermalStatus.SEVERE,
                DeviceCapability.ThermalStatus.CRITICAL,
                DeviceCapability.ThermalStatus.EMERGENCY,
                DeviceCapability.ThermalStatus.SHUTDOWN
            )
        ) {
            return AuraPerformanceProfile.LITE
        }

        if (isSevereMemoryPressure || capabilities.isLowRamDevice || capabilities.totalRamMb < 3000) {
            return AuraPerformanceProfile.LITE
        }

        // 2. Moderate pressures downgrade to BALANCED mode
        if (capabilities.thermalStatus in setOf(
                DeviceCapability.ThermalStatus.LIGHT,
                DeviceCapability.ThermalStatus.MODERATE
            )
        ) {
            return AuraPerformanceProfile.BALANCED
        }

        if (sustainedFpsDegradation) {
            return AuraPerformanceProfile.BALANCED
        }

        if (capabilities.batteryLevelPercent <= 25 && !capabilities.isBatteryCharging) {
            return AuraPerformanceProfile.BALANCED
        }

        if (capabilities.totalRamMb < 5500 || !capabilities.supportsRenderEffectBlur) {
            return AuraPerformanceProfile.BALANCED
        }

        // 3. Flagship hardware with abundant RAM and healthy thermals
        return AuraPerformanceProfile.FULL
    }
}
