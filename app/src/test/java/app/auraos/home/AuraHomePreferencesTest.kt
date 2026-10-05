package app.auraos.home

import app.auraos.core.system.AuraAppItem
import app.auraos.home.model.AuraDockItem
import app.auraos.home.model.AuraFocusContext
import app.auraos.home.model.AuraFolderData
import app.auraos.home.model.AuraIconShape
import app.auraos.home.model.AuraIconStyle
import app.auraos.home.model.AuraSmartStackData
import app.auraos.home.model.AuraWorkspaceItem
import app.auraos.home.persistence.HomePreferencesRepository
import app.auraos.material.glass.AuraGlassMaterialType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class InMemoryHomePreferencesRepository : HomePreferencesRepository {
    private val _workspaceItems = MutableStateFlow<List<AuraWorkspaceItem>>(emptyList())
    override val workspaceItems: StateFlow<List<AuraWorkspaceItem>> = _workspaceItems.asStateFlow()

    private val _dockItems = MutableStateFlow<List<AuraDockItem>>(emptyList())
    override val dockItems: StateFlow<List<AuraDockItem>> = _dockItems.asStateFlow()

    private val _iconStyle = MutableStateFlow(AuraIconStyle())
    override val iconStyle: StateFlow<AuraIconStyle> = _iconStyle.asStateFlow()

    private val _currentFocusContext = MutableStateFlow(AuraFocusContext.NORMAL)
    override val currentFocusContext: StateFlow<AuraFocusContext> = _currentFocusContext.asStateFlow()

    override fun setIconStyle(style: AuraIconStyle) {
        _iconStyle.value = style
    }

    override fun setFocusContext(context: AuraFocusContext) {
        _currentFocusContext.value = context
    }

    override fun addWorkspaceItem(item: AuraWorkspaceItem) {
        _workspaceItems.value = _workspaceItems.value + item
    }

    override fun removeWorkspaceItem(id: String) {
        _workspaceItems.value = _workspaceItems.value.filter { it.id != id }
    }

    override fun updateWorkspaceItem(item: AuraWorkspaceItem) {
        _workspaceItems.value = _workspaceItems.value.map { if (it.id == item.id) item else it }
    }

    override fun setWorkspaceItems(items: List<AuraWorkspaceItem>) {
        _workspaceItems.value = items
    }

    override fun setDockItems(items: List<AuraDockItem>) {
        _dockItems.value = items
    }

    override fun addToDock(app: AuraAppItem): Boolean {
        if (_dockItems.value.size >= 5) return false
        if (_dockItems.value.any { it.packageName == app.packageName }) return false
        val item = AuraDockItem(UUID.randomUUID().toString(), app.packageName, app.activityName, app.label, _dockItems.value.size)
        _dockItems.value = _dockItems.value + item
        return true
    }

    override fun removeFromDock(idOrPackage: String) {
        _dockItems.value = _dockItems.value.filter { it.id != idOrPackage && it.packageName != idOrPackage }
    }

    override fun createFolder(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem {
        val folderId = UUID.randomUUID().toString()
        val data = AuraFolderData(folderId, name, appPackages, cellX, cellY)
        val item = AuraWorkspaceItem(folderId, AuraWorkspaceItem.Type.FOLDER, label = name, folderData = data, cellX = cellX, cellY = cellY)
        addWorkspaceItem(item)
        return item
    }

    override fun updateFolder(folder: AuraFolderData) {
        _workspaceItems.value = _workspaceItems.value.map {
            if (it.folderData?.id == folder.id) it.copy(label = folder.name, folderData = folder) else it
        }
    }

    override fun createSmartStack(name: String, appPackages: List<String>, cellX: Int, cellY: Int): AuraWorkspaceItem {
        val stackId = UUID.randomUUID().toString()
        val data = AuraSmartStackData(stackId, name, appPackages, 0, cellX, cellY)
        val item = AuraWorkspaceItem(stackId, AuraWorkspaceItem.Type.SMART_STACK, label = name, stackData = data, cellX = cellX, cellY = cellY)
        addWorkspaceItem(item)
        return item
    }

    override fun updateSmartStack(stack: AuraSmartStackData) {
        _workspaceItems.value = _workspaceItems.value.map {
            if (it.stackData?.id == stack.id) it.copy(label = stack.name, stackData = stack) else it
        }
    }

    override fun populateDefaultIfEmpty(installedApps: List<AuraAppItem>) {
        if (installedApps.isEmpty()) return
        if (_dockItems.value.isEmpty()) {
            val defaults = installedApps.take(4).mapIndexed { idx, app ->
                AuraDockItem(UUID.randomUUID().toString(), app.packageName, app.activityName, app.label, idx)
            }
            setDockItems(defaults)
        }
        if (_workspaceItems.value.isEmpty()) {
            val remaining = installedApps.drop(4)
            if (remaining.size >= 4) {
                createSmartStack("Daily Stack", remaining.take(4).map { it.packageName }, 0, 0)
            }
        }
    }
}

class AuraHomePreferencesTest {

    private val repository = InMemoryHomePreferencesRepository()

    @Test
    fun `dock constraints allow maximum 5 items`() {
        val app1 = AuraAppItem("com.app1", "Act1", "App 1")
        val app2 = AuraAppItem("com.app2", "Act2", "App 2")
        val app3 = AuraAppItem("com.app3", "Act3", "App 3")
        val app4 = AuraAppItem("com.app4", "Act4", "App 4")
        val app5 = AuraAppItem("com.app5", "Act5", "App 5")
        val app6 = AuraAppItem("com.app6", "Act6", "App 6")

        assertTrue(repository.addToDock(app1))
        assertTrue(repository.addToDock(app2))
        assertTrue(repository.addToDock(app3))
        assertTrue(repository.addToDock(app4))
        assertTrue(repository.addToDock(app5))
        assertFalse("Sixth item must be rejected by dock capacity", repository.addToDock(app6))

        assertEquals(5, repository.dockItems.value.size)

        repository.removeFromDock(app1.packageName)
        assertEquals(4, repository.dockItems.value.size)
    }

    @Test
    fun `icon style updates correctly`() {
        val customStyle = AuraIconStyle(
            materialType = AuraGlassMaterialType.CRYSTAL,
            shape = AuraIconShape.CIRCLE,
            sizeDp = 64f,
            showLabels = false,
            breathingEnabled = false
        )

        repository.setIconStyle(customStyle)
        val style = repository.iconStyle.value

        assertEquals(AuraGlassMaterialType.CRYSTAL, style.materialType)
        assertEquals(AuraIconShape.CIRCLE, style.shape)
        assertEquals(64f, style.sizeDp, 0.001f)
        assertFalse(style.showLabels)
        assertFalse(style.breathingEnabled)
    }

    @Test
    fun `folders and smart stacks are created and updated`() {
        val folderItem = repository.createFolder("Productivity", listOf("com.email", "com.calendar"), 0, 1)
        assertNotNull(folderItem.folderData)
        assertEquals("Productivity", folderItem.folderData?.name)
        assertEquals(2, folderItem.folderData?.appPackages?.size)

        val updatedFolder = folderItem.folderData!!.copy(name = "Work Suite")
        repository.updateFolder(updatedFolder)
        val fetched = repository.workspaceItems.value.first { it.id == folderItem.id }
        assertEquals("Work Suite", fetched.label)

        val stackItem = repository.createSmartStack("Media", listOf("com.music", "com.video"), 1, 0)
        assertNotNull(stackItem.stackData)
        assertEquals(2, stackItem.stackData?.appPackages?.size)

        val updatedStack = stackItem.stackData!!.copy(activeIndex = 1)
        repository.updateSmartStack(updatedStack)
        val fetchedStack = repository.workspaceItems.value.first { it.id == stackItem.id }
        assertEquals(1, fetchedStack.stackData?.activeIndex)
    }

    @Test
    fun `focus contexts switch seamlessly`() {
        assertEquals(AuraFocusContext.NORMAL, repository.currentFocusContext.value)

        repository.setFocusContext(AuraFocusContext.STUDY)
        assertEquals(AuraFocusContext.STUDY, repository.currentFocusContext.value)

        repository.setFocusContext(AuraFocusContext.GAMING)
        assertEquals(AuraFocusContext.GAMING, repository.currentFocusContext.value)
    }

    @Test
    fun `default workspace population creates dock and smart stack`() {
        val testApps = (1..10).map { i ->
            AuraAppItem("com.test.app$i", "Act$i", "App $i")
        }

        repository.populateDefaultIfEmpty(testApps)

        assertEquals(4, repository.dockItems.value.size)
        assertTrue(repository.workspaceItems.value.any { it.type == AuraWorkspaceItem.Type.SMART_STACK })
    }
}
