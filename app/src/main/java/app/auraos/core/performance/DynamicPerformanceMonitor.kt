package app.auraos.core.performance

import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.PowerManager
import app.auraos.core.platform.DeviceCapability
import app.auraos.core.platform.DeviceCapabilityDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Lifecycle-aware monitor that emits real-time performance profiles
 * as device conditions (thermals, battery, power-saver, memory) fluctuate.
 */
class DynamicPerformanceMonitor(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val detector: DeviceCapabilityDetector = DeviceCapabilityDetector(context),
    private val evaluator: PerformanceEvaluator = PerformanceEvaluator()
) : ComponentCallbacks2 {

    private val _capabilities = MutableStateFlow(detector.detectCapabilities())
    val capabilities: StateFlow<DeviceCapability> = _capabilities.asStateFlow()

    private val _manualOverride = MutableStateFlow<AuraPerformanceProfile?>(null)
    val manualOverride: StateFlow<AuraPerformanceProfile?> = _manualOverride.asStateFlow()

    private var isMemoryPressureSevere = false

    private val _currentProfile = MutableStateFlow(
        evaluator.evaluateProfile(_capabilities.value)
    )
    val currentProfile: StateFlow<AuraPerformanceProfile> = _currentProfile.asStateFlow()

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            recalculate()
        }
    }

    private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null

    init {
        registerSystemListeners()
    }

    private fun registerSystemListeners() {
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            }
            context.registerReceiver(systemReceiver, filter)
        } catch (_: Exception) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                thermalListener = PowerManager.OnThermalStatusChangedListener {
                    recalculate()
                }
                powerManager?.addThermalStatusListener(thermalListener!!)
            } catch (_: Exception) {}
        }

        try {
            context.registerComponentCallbacks(this)
        } catch (_: Exception) {}
    }

    fun setManualOverride(profile: AuraPerformanceProfile?) {
        _manualOverride.value = profile
        recalculate()
    }

    fun recalculate() {
        coroutineScope.launch {
            val freshCaps = detector.detectCapabilities()
            _capabilities.value = freshCaps
            val evaluated = evaluator.evaluateProfile(
                capabilities = freshCaps,
                isSevereMemoryPressure = isMemoryPressureSevere,
                manualOverride = _manualOverride.value
            )
            _currentProfile.value = evaluated
        }
    }

    fun cleanup() {
        try {
            context.unregisterReceiver(systemReceiver)
        } catch (_: Exception) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && thermalListener != null) {
            try {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                powerManager?.removeThermalStatusListener(thermalListener!!)
            } catch (_: Exception) {}
        }

        try {
            context.unregisterComponentCallbacks(this)
        } catch (_: Exception) {}
    }

    override fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            isMemoryPressureSevere = true
            recalculate()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        recalculate()
    }

    override fun onLowMemory() {
        isMemoryPressureSevere = true
        recalculate()
    }
}
