package app.auraos.core.system

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Represents a launchable application installed on the device.
 */
data class AuraAppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false
)

interface AppRepository {
    val installedApps: StateFlow<List<AuraAppItem>>
    val isLoading: StateFlow<Boolean>
    suspend fun refreshApps()
    fun launchApp(app: AuraAppItem): Boolean
}

class AndroidAppRepository(
    private val context: Context
) : AppRepository {

    private val _installedApps = MutableStateFlow<List<AuraAppItem>>(emptyList())
    override val installedApps: StateFlow<List<AuraAppItem>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override suspend fun refreshApps() {
        _isLoading.value = true
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfos = try {
                pm.queryIntentActivities(mainIntent, 0)
            } catch (_: Exception) {
                emptyList()
            }

            val ownPackage = context.packageName

            val items = resolveInfos
                .filter { it.activityInfo.packageName != ownPackage }
                .map { info ->
                    val isSystem = (info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val label = try {
                        info.loadLabel(pm).toString()
                    } catch (_: Exception) {
                        info.activityInfo.packageName
                    }
                    val icon = try {
                        info.loadIcon(pm)
                    } catch (_: Exception) {
                        null
                    }
                    AuraAppItem(
                        packageName = info.activityInfo.packageName,
                        activityName = info.activityInfo.name,
                        label = label,
                        icon = icon,
                        isSystemApp = isSystem
                    )
                }
                .sortedBy { it.label.lowercase() }

            _installedApps.value = items
            _isLoading.value = false
        }
    }

    override fun launchApp(app: AuraAppItem): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}
