package app.auraos.shell.state

import androidx.compose.runtime.Immutable
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.platform.DeviceCapability
import app.auraos.core.system.AuraBatteryInfo
import app.auraos.core.system.AuraWallpaperState
import app.auraos.design.material.AuraMaterialConfig

/**
 * Immutable snapshot of the global AURA OS environment state.
 */
@Immutable
data class AuraGlobalState(
    val currentSurface: AuraSurface = AuraSurface.HOME,
    val targetSurface: AuraSurface = AuraSurface.HOME,
    val surfaceTransitionProgress: Float = 0f, // 0.0 to 1.0
    val activeGestureDirection: GestureDirection? = null,
    val performanceProfile: AuraPerformanceProfile = AuraPerformanceProfile.BALANCED,
    val materialConfig: AuraMaterialConfig = AuraMaterialConfig.Default,
    val wallpaperState: AuraWallpaperState = AuraWallpaperState(),
    val batteryInfo: AuraBatteryInfo = AuraBatteryInfo(),
    val deviceCapability: DeviceCapability = DeviceCapability.DefaultFallback,
    val isAppDrawerExpanded: Boolean = false,
    val isDebugInspectorVisible: Boolean = false,
    val animationScale: Float = 1.0f
) {
    enum class GestureDirection {
        HORIZONTAL_LEFT,
        HORIZONTAL_RIGHT,
        VERTICAL_DOWN,
        VERTICAL_UP
    }
}
