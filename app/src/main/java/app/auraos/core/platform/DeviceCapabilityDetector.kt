package app.auraos.core.platform

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.WindowManager

/**
 * Robust device capability detection with safe fallbacks for all Android versions and hardware.
 */
class DeviceCapabilityDetector(private val context: Context) {

    fun detectCapabilities(): DeviceCapability {
        val apiLevel = Build.VERSION.SDK_INT
        val (totalRam, availableRam, isLowRam) = detectRam()
        val refreshRate = detectRefreshRate()
        val supportsRenderEffect = apiLevel >= Build.VERSION_CODES.S
        val (batteryLevel, isCharging) = detectBattery()
        val isPowerSave = detectPowerSaveMode()
        val thermalStatus = detectThermalStatus()
        val reducedMotion = detectReducedMotion()
        val (widthPx, heightPx, densityDpi) = detectDisplayMetrics()

        return DeviceCapability(
            apiLevel = apiLevel,
            totalRamMb = totalRam,
            availableRamMb = availableRam,
            isLowRamDevice = isLowRam,
            refreshRateHz = refreshRate,
            supportsRenderEffectBlur = supportsRenderEffect,
            supportsHardwareAcceleration = true,
            isPowerSaveMode = isPowerSave,
            batteryLevelPercent = batteryLevel,
            isBatteryCharging = isCharging,
            thermalStatus = thermalStatus,
            isReducedMotionPreferred = reducedMotion,
            screenWidthPx = widthPx,
            screenHeightPx = heightPx,
            screenDensityDpi = densityDpi
        )
    }

    private fun detectRam(): Triple<Long, Long, Boolean> {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (actManager != null) {
                val memInfo = ActivityManager.MemoryInfo()
                actManager.getMemoryInfo(memInfo)
                val totalMb = memInfo.totalMem / (1024 * 1024)
                val availMb = memInfo.availMem / (1024 * 1024)
                val lowRam = memInfo.lowMemory || actManager.isLowRamDevice
                Triple(totalMb, availMb, lowRam)
            } else {
                Triple(4096L, 2048L, false)
            }
        } catch (_: Exception) {
            Triple(4096L, 2048L, false)
        }
    }

    private fun detectRefreshRate(): Float {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.refreshRate ?: 60f
            } else {
                @Suppress("DEPRECATION")
                windowManager?.defaultDisplay?.refreshRate ?: 60f
            }
        } catch (_: Exception) {
            60f
        }
    }

    private fun detectBattery(): Pair<Int, Boolean> {
        return try {
            val batteryStatus: Intent? = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val percent = if (level >= 0 && scale > 0) (level * 100) / scale else 100

            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            Pair(percent, isCharging)
        } catch (_: Exception) {
            Pair(100, false)
        }
    }

    private fun detectPowerSaveMode(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            powerManager?.isPowerSaveMode ?: false
        } catch (_: Exception) {
            false
        }
    }

    private fun detectThermalStatus(): DeviceCapability.ThermalStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return DeviceCapability.ThermalStatus.NORMAL
        }
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            when (powerManager?.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> DeviceCapability.ThermalStatus.NORMAL
                PowerManager.THERMAL_STATUS_LIGHT -> DeviceCapability.ThermalStatus.LIGHT
                PowerManager.THERMAL_STATUS_MODERATE -> DeviceCapability.ThermalStatus.MODERATE
                PowerManager.THERMAL_STATUS_SEVERE -> DeviceCapability.ThermalStatus.SEVERE
                PowerManager.THERMAL_STATUS_CRITICAL -> DeviceCapability.ThermalStatus.CRITICAL
                PowerManager.THERMAL_STATUS_EMERGENCY -> DeviceCapability.ThermalStatus.EMERGENCY
                PowerManager.THERMAL_STATUS_SHUTDOWN -> DeviceCapability.ThermalStatus.SHUTDOWN
                else -> DeviceCapability.ThermalStatus.NORMAL
            }
        } catch (_: Exception) {
            DeviceCapability.ThermalStatus.NORMAL
        }
    }

    private fun detectReducedMotion(): Boolean {
        return try {
            val animatorScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            val transitionScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            )
            animatorScale == 0f || transitionScale == 0f
        } catch (_: Exception) {
            false
        }
    }

    private fun detectDisplayMetrics(): Triple<Int, Int, Int> {
        val dm = context.resources.displayMetrics
        return Triple(dm.widthPixels, dm.heightPixels, dm.densityDpi)
    }
}
