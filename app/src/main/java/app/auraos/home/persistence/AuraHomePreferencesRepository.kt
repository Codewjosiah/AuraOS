package app.auraos.home.persistence

import android.content.Context
import android.content.SharedPreferences
import app.auraos.core.system.AuraAppItem
import app.auraos.home.model.AuraDockItem
import app.auraos.home.model.AuraFocusContext
import app.auraos.home.model.AuraFolderData
import app.auraos.home.model.AuraIconShape
import app.auraos.home.model.AuraIconStyle
import app.auraos.home.model.AuraSmartStackData
import app.auraos.home.model.AuraWorkspaceItem
import app.auraos.material.glass.AuraGlassMaterialType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

interface HomePreferencesRepository {
    val workspaceItems: StateFlow<List<AuraWorkspaceItem>>
    val dockItems: StateFlow<List<AuraDockItem>>
    val iconStyle: StateFlow<AuraIconStyle>
    val currentFocusContext: StateFlow<AuraFocusContext>

    fun setIconStyle(style: AuraIconStyle)
    fun setFocusContext(context: AuraFocusContext)
    fun addWorkspaceItem(item: AuraWorkspaceItem)
    fun removeWorkspaceItem(id: String)
    fun updateWorkspaceItem(item: AuraWorkspaceItem)
    fun setWorkspaceItems(items: List<AuraWorkspaceItem>)
    fun setDockItems(items: List<AuraDockItem>)
    fun addToDock(app: AuraAppItem): Boolean
    fun removeFromDock(idOrPackage: String)
    fun createFolder(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem
    fun updateFolder(folder: AuraFolderData)
    fun createSmartStack(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem
    fun updateSmartStack(stack: AuraSmartStackData)
    fun populateDefaultIfEmpty(installedApps: List<AuraAppItem>)
}

class AndroidHomePreferencesRepository(
    private val context: Context
) : HomePreferencesRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aura_home_prefs", Context.MODE_PRIVATE)

    private val _workspaceItems = MutableStateFlow<List<AuraWorkspaceItem>>(emptyList())
    override val workspaceItems: StateFlow<List<AuraWorkspaceItem>> = _workspaceItems.asStateFlow()

    private val _dockItems = MutableStateFlow<List<AuraDockItem>>(emptyList())
    override val dockItems: StateFlow<List<AuraDockItem>> = _dockItems.asStateFlow()

    private val _iconStyle = MutableStateFlow(AuraIconStyle())
    override val iconStyle: StateFlow<AuraIconStyle> = _iconStyle.asStateFlow()

    private val _currentFocusContext = MutableStateFlow(AuraFocusContext.NORMAL)
    override val currentFocusContext: StateFlow<AuraFocusContext> = _currentFocusContext.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        // Load Icon Style
        val materialTypeName = prefs.getString("icon_material", AuraGlassMaterialType.LIQUID.name)
        val shapeName = prefs.getString("icon_shape", AuraIconShape.SQUIRCLE.name)
        val sizeDp = prefs.getFloat("icon_size", 58f)
        val showLabels = prefs.getBoolean("icon_labels", true)
        val breathing = prefs.getBoolean("icon_breathing", true)

        val materialType = try {
            AuraGlassMaterialType.valueOf(materialTypeName ?: AuraGlassMaterialType.LIQUID.name)
        } catch (_: Exception) {
            AuraGlassMaterialType.LIQUID
        }
        val shape = try {
            AuraIconShape.valueOf(shapeName ?: AuraIconShape.SQUIRCLE.name)
        } catch (_: Exception) {
            AuraIconShape.SQUIRCLE
        }
        _iconStyle.value = AuraIconStyle(materialType, shape, sizeDp, showLabels, breathing)

        // Load Focus Context
        val focusName = prefs.getString("focus_context", AuraFocusContext.NORMAL.name)
        _currentFocusContext.value = try {
            AuraFocusContext.valueOf(focusName ?: AuraFocusContext.NORMAL.name)
        } catch (_: Exception) {
            AuraFocusContext.NORMAL
        }

        // Load Dock Items
        val dockJson = prefs.getString("dock_items", null)
        if (!dockJson.isNullOrBlank()) {
            try {
                val array = JSONArray(dockJson)
                val list = mutableListOf<AuraDockItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        AuraDockItem(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            packageName = obj.getString("packageName"),
                            activityName = obj.optString("activityName", ""),
                            label = obj.optString("label", ""),
                            position = obj.optInt("position", i)
                        )
                    )
                }
                _dockItems.value = list
            } catch (_: Exception) {}
        }

        // Load Workspace Items
        val workspaceJson = prefs.getString("workspace_items", null)
        if (!workspaceJson.isNullOrBlank()) {
            try {
                val array = JSONArray(workspaceJson)
                val list = mutableListOf<AuraWorkspaceItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val type = AuraWorkspaceItem.Type.valueOf(obj.getString("type"))
                    val folderData = if (obj.has("folder")) {
                        val fObj = obj.getJSONObject("folder")
                        val pArray = fObj.getJSONArray("apps")
                        val pList = mutableListOf<String>()
                        for (p in 0 until pArray.length()) pList.add(pArray.getString(p))
                        AuraFolderData(
                            id = fObj.getString("id"),
                            name = fObj.getString("name"),
                            appPackages = pList,
                            cellX = fObj.optInt("cellX", 0),
                            cellY = fObj.optInt("cellY", 0)
                        )
                    } else null

                    val stackData = if (obj.has("stack")) {
                        val sObj = obj.getJSONObject("stack")
                        val pArray = sObj.getJSONArray("apps")
                        val pList = mutableListOf<String>()
                        for (p in 0 until pArray.length()) pList.add(pArray.getString(p))
                        AuraSmartStackData(
                            id = sObj.getString("id"),
                            name = sObj.optString("name", "Smart Stack"),
                            appPackages = pList,
                            activeIndex = sObj.optInt("activeIndex", 0),
                            cellX = sObj.optInt("cellX", 0),
                            cellY = sObj.optInt("cellY", 0)
                        )
                    } else null

                    list.add(
                        AuraWorkspaceItem(
                            id = obj.getString("id"),
                            type = type,
                            packageName = obj.optString("packageName", ""),
                            activityName = obj.optString("activityName", ""),
                            label = obj.optString("label", ""),
                            folderData = folderData,
                            stackData = stackData,
                            cellX = obj.optInt("cellX", 0),
                            cellY = obj.optInt("cellY", 0)
                        )
                    )
                }
                _workspaceItems.value = list
            } catch (_: Exception) {}
        }
    }

    override fun setIconStyle(style: AuraIconStyle) {
        _iconStyle.value = style
        prefs.edit()
            .putString("icon_material", style.materialType.name)
            .putString("icon_shape", style.shape.name)
            .putFloat("icon_size", style.sizeDp)
            .putBoolean("icon_labels", style.showLabels)
            .putBoolean("icon_breathing", style.breathingEnabled)
            .apply()
    }

    override fun setFocusContext(context: AuraFocusContext) {
        _currentFocusContext.value = context
        prefs.edit().putString("focus_context", context.name).apply()
    }

    override fun addWorkspaceItem(item: AuraWorkspaceItem) {
        val updated = _workspaceItems.value + item
        setWorkspaceItems(updated)
    }

    override fun removeWorkspaceItem(id: String) {
        val updated = _workspaceItems.value.filter { it.id != id }
        setWorkspaceItems(updated)
    }

    override fun updateWorkspaceItem(item: AuraWorkspaceItem) {
        val updated = _workspaceItems.value.map { if (it.id == item.id) item else it }
        setWorkspaceItems(updated)
    }

    override fun setWorkspaceItems(items: List<AuraWorkspaceItem>) {
        _workspaceItems.value = items
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("type", item.type.name)
            obj.put("packageName", item.packageName)
            obj.put("activityName", item.activityName)
            obj.put("label", item.label)
            obj.put("cellX", item.cellX)
            obj.put("cellY", item.cellY)

            item.folderData?.let { folder ->
                val fObj = JSONObject()
                fObj.put("id", folder.id)
                fObj.put("name", folder.name)
                fObj.put("cellX", folder.cellX)
                fObj.put("cellY", folder.cellY)
                val pArray = JSONArray()
                folder.appPackages.forEach { pArray.put(it) }
                fObj.put("apps", pArray)
                obj.put("folder", fObj)
            }

            item.stackData?.let { stack ->
                val sObj = JSONObject()
                sObj.put("id", stack.id)
                sObj.put("name", stack.name)
                sObj.put("activeIndex", stack.activeIndex)
                sObj.put("cellX", stack.cellX)
                sObj.put("cellY", stack.cellY)
                val pArray = JSONArray()
                stack.appPackages.forEach { pArray.put(it) }
                sObj.put("apps", pArray)
                obj.put("stack", sObj)
            }
            array.put(obj)
        }
        prefs.edit().putString("workspace_items", array.toString()).apply()
    }

    override fun setDockItems(items: List<AuraDockItem>) {
        _dockItems.value = items
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("packageName", item.packageName)
            obj.put("activityName", item.activityName)
            obj.put("label", item.label)
            obj.put("position", item.position)
            array.put(obj)
        }
        prefs.edit().putString("dock_items", array.toString()).apply()
    }

    override fun addToDock(app: AuraAppItem): Boolean {
        val current = _dockItems.value
        if (current.size >= 5) return false
        if (current.any { it.packageName == app.packageName }) return false
        val newItem = AuraDockItem(
            id = UUID.randomUUID().toString(),
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label,
            position = current.size
        )
        setDockItems(current + newItem)
        return true
    }

    override fun removeFromDock(idOrPackage: String) {
        val current = _dockItems.value
        val updated = current.filter { it.id != idOrPackage && it.packageName != idOrPackage }
            .mapIndexed { index, item -> item.copy(position = index) }
        setDockItems(updated)
    }

    override fun createFolder(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem {
        val folderId = UUID.randomUUID().toString()
        val folderData = AuraFolderData(
            id = folderId,
            name = name,
            appPackages = appPackages,
            cellX = cellX,
            cellY = cellY
        )
        val item = AuraWorkspaceItem(
            id = folderId,
            type = AuraWorkspaceItem.Type.FOLDER,
            label = name,
            folderData = folderData,
            cellX = cellX,
            cellY = cellY
        )
        addWorkspaceItem(item)
        return item
    }

    override fun updateFolder(folder: AuraFolderData) {
        val current = _workspaceItems.value
        val updated = current.map { item ->
            if (item.folderData?.id == folder.id) {
                item.copy(label = folder.name, folderData = folder)
            } else {
                item
            }
        }
        setWorkspaceItems(updated)
    }

    override fun createSmartStack(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem {
        val stackId = UUID.randomUUID().toString()
        val stackData = AuraSmartStackData(
            id = stackId,
            name = name,
            appPackages = appPackages,
            activeIndex = 0,
            cellX = cellX,
            cellY = cellY
        )
        val item = AuraWorkspaceItem(
            id = stackId,
            type = AuraWorkspaceItem.Type.SMART_STACK,
            label = name,
            stackData = stackData,
            cellX = cellX,
            cellY = cellY
        )
        addWorkspaceItem(item)
        return item
    }

    override fun updateSmartStack(stack: AuraSmartStackData) {
        val current = _workspaceItems.value
        val updated = current.map { item ->
            if (item.stackData?.id == stack.id) {
                item.copy(label = stack.name, stackData = stack)
            } else {
                item
            }
        }
        setWorkspaceItems(updated)
    }

    override fun populateDefaultIfEmpty(installedApps: List<AuraAppItem>) {
        if (installedApps.isEmpty()) return

        // Populate Dock if empty
        if (_dockItems.value.isEmpty()) {
            val dockDefaults = mutableListOf<AuraDockItem>()
            // Find browser, camera, phone, settings, messaging or first 4 apps
            val preferredCategories = listOf("browser", "camera", "dialer", "settings", "message")
            val pickedPackages = mutableSetOf<String>()

            for (term in preferredCategories) {
                if (dockDefaults.size >= 4) break
                val match = installedApps.firstOrNull {
                    !pickedPackages.contains(it.packageName) &&
                            (it.packageName.lowercase().contains(term) || it.label.lowercase().contains(term))
                }
                if (match != null) {
                    pickedPackages.add(match.packageName)
                    dockDefaults.add(
                        AuraDockItem(
                            id = UUID.randomUUID().toString(),
                            packageName = match.packageName,
                            activityName = match.activityName,
                            label = match.label,
                            position = dockDefaults.size
                        )
                    )
                }
            }

            // Fill up to 4 if needed
            for (app in installedApps) {
                if (dockDefaults.size >= 4) break
                if (!pickedPackages.contains(app.packageName)) {
                    pickedPackages.add(app.packageName)
                    dockDefaults.add(
                        AuraDockItem(
                            id = UUID.randomUUID().toString(),
                            packageName = app.packageName,
                            activityName = app.activityName,
                            label = app.label,
                            position = dockDefaults.size
                        )
                    )
                }
            }
            if (dockDefaults.isNotEmpty()) {
                setDockItems(dockDefaults)
            }
        }

        // Populate Workspace if empty
        if (_workspaceItems.value.isEmpty()) {
            val workspace = mutableListOf<AuraWorkspaceItem>()
            val remainingApps = installedApps.filter { app ->
                _dockItems.value.none { it.packageName == app.packageName }
            }

            // 1. Create a default Smart Stack with 4 apps if available
            if (remainingApps.size >= 4) {
                val stackApps = remainingApps.take(4).map { it.packageName }
                val stackId = UUID.randomUUID().toString()
                workspace.add(
                    AuraWorkspaceItem(
                        id = stackId,
                        type = AuraWorkspaceItem.Type.SMART_STACK,
                        label = "Smart Stack",
                        stackData = AuraSmartStackData(
                            id = stackId,
                            name = "Daily Stack",
                            appPackages = stackApps,
                            activeIndex = 0
                        ),
                        cellX = 0,
                        cellY = 0
                    )
                )
            }

            // 2. Add next 4-8 apps directly to workspace
            val directApps = remainingApps.drop(4).take(8)
            directApps.forEachIndexed { index, app ->
                workspace.add(
                    AuraWorkspaceItem(
                        id = UUID.randomUUID().toString(),
                        type = AuraWorkspaceItem.Type.APP,
                        packageName = app.packageName,
                        activityName = app.activityName,
                        label = app.label,
                        cellX = (index + 1) % 4,
                        cellY = (index + 1) / 4
                    )
                )
            }

            if (workspace.isNotEmpty()) {
                setWorkspaceItems(workspace)
            }
        }
    }
}
