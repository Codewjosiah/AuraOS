package app.auraos.shell.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.performance.DynamicPerformanceMonitor
import app.auraos.core.system.AppRepository
import app.auraos.core.system.AuraAppItem
import app.auraos.core.system.BatteryRepository
import app.auraos.core.system.WallpaperRepository
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.home.model.AuraDockItem
import app.auraos.home.model.AuraFocusContext
import app.auraos.home.model.AuraFolderData
import app.auraos.home.model.AuraIconStyle
import app.auraos.home.model.AuraSmartStackData
import app.auraos.home.model.AuraWidgetData
import app.auraos.home.model.AuraWorkspaceItem
import app.auraos.home.persistence.HomePreferencesRepository
import app.auraos.home.widget.AuraWidgetHostManager
import app.auraos.home.widget.AvailableWidgetInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AuraShellViewModel(
    val appRepository: AppRepository,
    val wallpaperRepository: WallpaperRepository,
    val batteryRepository: BatteryRepository,
    val performanceMonitor: DynamicPerformanceMonitor,
    val homePreferencesRepository: HomePreferencesRepository? = null,
    val widgetHostManager: AuraWidgetHostManager? = null
) : ViewModel() {

    private val _surfaceState = MutableStateFlow(AuraSurface.HOME)
    private val _isAppDrawerExpanded = MutableStateFlow(false)
    private val _isDebugInspectorVisible = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _globalState = MutableStateFlow(AuraGlobalState())
    val globalState: StateFlow<AuraGlobalState> = _globalState.asStateFlow()

    // Home Workspace State
    val workspaceItems: StateFlow<List<AuraWorkspaceItem>> =
        homePreferencesRepository?.workspaceItems ?: MutableStateFlow(emptyList())

    val dockItems: StateFlow<List<AuraDockItem>> =
        homePreferencesRepository?.dockItems ?: MutableStateFlow(emptyList())

    val iconStyle: StateFlow<AuraIconStyle> =
        homePreferencesRepository?.iconStyle ?: MutableStateFlow(AuraIconStyle())

    val currentFocusContext: StateFlow<AuraFocusContext> =
        homePreferencesRepository?.currentFocusContext ?: MutableStateFlow(AuraFocusContext.NORMAL)

    init {
        viewModelScope.launch {
            appRepository.refreshApps()
            wallpaperRepository.refreshWallpaper()
        }

        // Initialize default workspace and dock when apps are loaded
        viewModelScope.launch {
            appRepository.installedApps.collect { apps ->
                if (apps.isNotEmpty()) {
                    homePreferencesRepository?.populateDefaultIfEmpty(apps)
                }
            }
        }

        // Start listening for widget updates
        widgetHostManager?.startListening()

        // Combine reactive sources into unified AuraGlobalState
        viewModelScope.launch {
            combine(
                _surfaceState,
                _isAppDrawerExpanded,
                _isDebugInspectorVisible,
                performanceMonitor.currentProfile,
                performanceMonitor.capabilities
            ) { surface, drawer, debug, profile, caps ->
                _globalState.update { current ->
                    current.copy(
                        currentSurface = surface,
                        isAppDrawerExpanded = drawer,
                        isDebugInspectorVisible = debug,
                        performanceProfile = profile,
                        materialConfig = AuraMaterialConfig.forProfile(profile),
                        deviceCapability = caps,
                        animationScale = if (caps.isReducedMotionPreferred) 0.5f else 1.0f
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            wallpaperRepository.wallpaperState.collect { wall ->
                _globalState.update { it.copy(wallpaperState = wall) }
            }
        }

        viewModelScope.launch {
            batteryRepository.batteryInfo.collect { batt ->
                _globalState.update { it.copy(batteryInfo = batt) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        widgetHostManager?.stopListening()
    }

    fun navigateToSurface(surface: AuraSurface) {
        _surfaceState.value = surface
        _isAppDrawerExpanded.value = false
    }

    fun returnToHome() {
        if (_isAppDrawerExpanded.value) {
            _isAppDrawerExpanded.value = false
        } else {
            _surfaceState.value = AuraSurface.HOME
        }
    }

    fun toggleAppDrawer() {
        _isAppDrawerExpanded.update { !it }
    }

    fun toggleDebugInspector() {
        _isDebugInspectorVisible.update { !it }
    }

    fun setManualPerformanceOverride(profile: AuraPerformanceProfile?) {
        performanceMonitor.setManualOverride(profile)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun launchApp(app: AuraAppItem): Boolean {
        return appRepository.launchApp(app)
    }

    fun refreshApps() {
        viewModelScope.launch {
            appRepository.refreshApps()
        }
    }

    // Home Operations
    fun addToDock(app: AuraAppItem): Boolean {
        return homePreferencesRepository?.addToDock(app) ?: false
    }

    fun removeFromDock(idOrPackage: String) {
        homePreferencesRepository?.removeFromDock(idOrPackage)
    }

    fun setIconStyle(style: AuraIconStyle) {
        homePreferencesRepository?.setIconStyle(style)
    }

    fun setFocusContext(context: AuraFocusContext) {
        homePreferencesRepository?.setFocusContext(context)
    }

    fun addAppToWorkspace(app: AuraAppItem, cellX: Int = 0, cellY: Int = 0) {
        val item = AuraWorkspaceItem(
            id = UUID.randomUUID().toString(),
            type = AuraWorkspaceItem.Type.APP,
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label,
            cellX = cellX,
            cellY = cellY
        )
        homePreferencesRepository?.addWorkspaceItem(item)
    }

    fun removeWorkspaceItem(id: String) {
        homePreferencesRepository?.removeWorkspaceItem(id)
    }

    fun createFolder(name: String, appPackages: List<String>, cellX: Int = 0, cellY: Int = 0) {
        homePreferencesRepository?.createFolder(name, appPackages, cellX, cellY)
    }

    fun updateFolder(folder: AuraFolderData) {
        homePreferencesRepository?.updateFolder(folder)
    }

    fun createSmartStack(name: String, appPackages: List<String>, cellX: Int = 0, cellY: Int = 0) {
        homePreferencesRepository?.createSmartStack(name, appPackages, cellX, cellY)
    }

    fun updateSmartStack(stack: AuraSmartStackData) {
        homePreferencesRepository?.updateSmartStack(stack)
    }

    fun addWidget(info: AvailableWidgetInfo) {
        val hostMgr = widgetHostManager ?: return
        val appWidgetId = hostMgr.allocateAppWidgetId()
        val widgetItem = AuraWorkspaceItem(
            id = UUID.randomUUID().toString(),
            type = AuraWorkspaceItem.Type.WIDGET,
            label = info.label,
            widgetData = AuraWidgetData(
                id = UUID.randomUUID().toString(),
                appWidgetId = appWidgetId,
                providerPackage = info.packageName,
                providerClass = info.providerInfo.provider.className,
                label = info.label,
                spanX = 2,
                spanY = 2
            ),
            cellX = 0,
            cellY = 0
        )
        homePreferencesRepository?.addWorkspaceItem(widgetItem)
    }

    fun removeWidget(appWidgetId: Int, itemId: String) {
        widgetHostManager?.deleteAppWidgetId(appWidgetId)
        homePreferencesRepository?.removeWorkspaceItem(itemId)
    }

    fun resizeWidget(itemId: String, spanX: Int, spanY: Int) {
        val current = workspaceItems.value
        val item = current.firstOrNull { it.id == itemId } ?: return
        val wData = item.widgetData ?: return
        val updated = item.copy(widgetData = wData.copy(spanX = spanX, spanY = spanY))
        homePreferencesRepository?.updateWorkspaceItem(updated)
    }
}
