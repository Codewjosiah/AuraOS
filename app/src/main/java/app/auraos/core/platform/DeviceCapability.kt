package app.auraos.core.platform

/**
 * Represents comprehensive hardware, OS, and rendering capabilities
 * detected for the current device.
 */
data class DeviceCapability(
    val apiLevel: Int,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val isLowRamDevice: Boolean,
    val refreshRateHz: Float,
    val supportsRenderEffectBlur: Boolean,
    val supportsHardwareAcceleration: Boolean,
    val isPowerSaveMode: Boolean,
    val batteryLevelPercent: Int,
    val isBatteryCharging: Boolean,
    val thermalStatus: ThermalStatus,
    val isReducedMotionPreferred: Boolean,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val screenDensityDpi: Int
) {
    enum class ThermalStatus {
        NORMAL,
        LIGHT,
        MODERATE,
        SEVERE,
        CRITICAL,
        EMERGENCY,
        SHUTDOWN,
        UNKNOWN
    }

    companion object {
        val DefaultFallback = DeviceCapability(
            apiLevel = 26,
            totalRamMb = 4096,
            availableRamMb = 2048,
            isLowRamDevice = false,
            refreshRateHz = 60f,
            supportsRenderEffectBlur = false,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = false,
            batteryLevelPercent = 100,
            isBatteryCharging = false,
            thermalStatus = ThermalStatus.NORMAL,
            isReducedMotionPreferred = false,
            screenWidthPx = 1080,
            screenHeightPx = 2400,
            screenDensityDpi = 420
        )
    }
}
