package app.auraos.core.system

import android.content.Context
import app.auraos.core.performance.DynamicPerformanceMonitor
import app.auraos.core.permissions.AuraPermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Clean dependency and service locator for AURA core system services.
 * Keeps architecture decoupled without heavy reflection-based injection overhead.
 */
class SystemServiceLocator(context: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val performanceMonitor by lazy {
        DynamicPerformanceMonitor(context.applicationContext, applicationScope)
    }

    val permissionManager by lazy {
        AuraPermissionManager(context.applicationContext)
    }

    val appRepository: AppRepository by lazy {
        AndroidAppRepository(context.applicationContext)
    }

    val wallpaperRepository: WallpaperRepository by lazy {
        AndroidWallpaperRepository(context.applicationContext)
    }

    val batteryRepository: BatteryRepository by lazy {
        AndroidBatteryRepository(context.applicationContext)
    }

    val homePreferencesRepository: app.auraos.home.persistence.HomePreferencesRepository by lazy {
        app.auraos.home.persistence.AndroidHomePreferencesRepository(context.applicationContext)
    }

    val widgetHostManager: app.auraos.home.widget.AuraWidgetHostManager by lazy {
        app.auraos.home.widget.AuraWidgetHostManager(context.applicationContext)
    }
}
