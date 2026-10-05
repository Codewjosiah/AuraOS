package app.auraos.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuraBatteryInfo(
    val levelPercent: Int = 100,
    val isCharging: Boolean = false,
    val isPowerSave: Boolean = false
) {
    val percentage: Int get() = levelPercent
}

interface BatteryRepository {
    val batteryInfo: StateFlow<AuraBatteryInfo>
}

class AndroidBatteryRepository(
    private val context: Context
) : BatteryRepository {

    private val _batteryInfo = MutableStateFlow(AuraBatteryInfo())
    override val batteryInfo: StateFlow<AuraBatteryInfo> = _batteryInfo.asStateFlow()

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            update()
        }
    }

    init {
        update()
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            context.registerReceiver(batteryReceiver, filter)
        } catch (_: Exception) {}
    }

    private fun update() {
        try {
            val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val percent = if (level >= 0 && scale > 0) (level * 100) / scale else 100
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            _batteryInfo.value = AuraBatteryInfo(
                levelPercent = percent,
                isCharging = isCharging
            )
        } catch (_: Exception) {
            _batteryInfo.value = AuraBatteryInfo(100, false)
        }
    }
}
