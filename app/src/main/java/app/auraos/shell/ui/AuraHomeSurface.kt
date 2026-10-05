package app.auraos.shell.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.system.AuraAppItem
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.contextmenu.AuraAppContextMenu
import app.auraos.home.dock.AuraGlassDock
import app.auraos.home.edit.AuraHomeEditSurface
import app.auraos.home.focus.AuraFocusSwitcher
import app.auraos.home.folder.AuraExpandedFolderSheet
import app.auraos.home.folder.AuraFolderThumbnail
import app.auraos.home.icon.AuraAppIcon
import app.auraos.home.model.AuraFolderData
import app.auraos.home.model.AuraWorkspaceItem
import app.auraos.home.parallax.rememberAuraParallaxOffset
import app.auraos.home.stack.AuraSmartStackView
import app.auraos.home.widget.AuraGlassWidgetHost
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraGlassCard
import app.auraos.material.glass.AuraGlassPill
import app.auraos.shell.state.AuraGlobalState
import app.auraos.shell.state.AuraShellViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuraHomeSurface(
    globalState: AuraGlobalState,
    apps: List<AuraAppItem>,
    isAppsLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAppClick: (AuraAppItem) -> Unit,
    onToggleAppDrawer: () -> Unit,
    onOpenCommand: () -> Unit,
    onNavigateRiver: () -> Unit,
    onNavigateOrb: () -> Unit,
    viewModel: AuraShellViewModel? = null,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val currentDate = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    // Parallax offset on tilt
    val parallaxOffset by rememberAuraParallaxOffset(
        profile = globalState.performanceProfile,
        isReducedMotion = globalState.deviceCapability.isReducedMotionPreferred
    )

    // Reactive State from ViewModel
    val workspaceItems = viewModel?.workspaceItems?.collectAsState()?.value ?: emptyList()
    val dockItems = viewModel?.dockItems?.collectAsState()?.value ?: emptyList()
    val iconStyle = viewModel?.iconStyle?.collectAsState()?.value ?: app.auraos.home.model.AuraIconStyle()
    val currentFocus = viewModel?.currentFocusContext?.collectAsState()?.value ?: app.auraos.home.model.AuraFocusContext.NORMAL

    // Local UI modal states
    var selectedAppForMenu by remember { mutableStateOf<AuraAppItem?>(null) }
    var expandedFolder by remember { mutableStateOf<AuraFolderData?>(null) }
    var isEditModeVisible by remember { mutableStateOf(false) }
    var isFocusSwitcherVisible by remember { mutableStateOf(false) }

    val widgetHostManager = viewModel?.widgetHostManager
    val availableWidgets = remember(widgetHostManager) {
        widgetHostManager?.getInstalledWidgets() ?: emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_home_surface")
            // Two-finger pinch gesture detection for Focus Switcher
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom < 0.85f && !isFocusSwitcherVisible && !isEditModeVisible) {
                        isFocusSwitcherVisible = true
                    }
                }
            }
            // Long-press empty workspace to enter Edit Mode
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isEditModeVisible = true
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AuraSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(AuraSpacing.xl))

            // Upper Ambient Environment Header (Status, Clock, Focus, Battery)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = parallaxOffset.x * 0.5f
                        translationY = parallaxOffset.y * 0.5f
                    }
            ) {
                // Top status pills row: Battery, Focus Context, Performance Mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AuraSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery capsule
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (globalState.batteryInfo.isCharging) Icons.Default.Bolt else Icons.Default.BatteryFull,
                            contentDescription = "Battery ${globalState.batteryInfo.percentage}%",
                            tint = if (globalState.batteryInfo.isCharging) Color(0xFF00FF88) else AuraColors.Silver,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${globalState.batteryInfo.percentage}%",
                            style = AuraTypography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White
                        )
                    }

                    // Ambient Focus pill (Tap to switch focus)
                    AuraGlassPill(
                        text = currentFocus.displayName.uppercase(),
                        icon = Icons.Default.Layers,
                        onClick = { isFocusSwitcherVisible = true },
                        depth = AuraDepth.LEVEL_1,
                        modifier = Modifier.testTag("home_focus_pill")
                    )

                    // Performance Profile pill
                    AuraGlassPill(
                        text = globalState.performanceProfile.name,
                        icon = Icons.Default.Speed,
                        onClick = { viewModel?.toggleDebugInspector() },
                        depth = AuraDepth.LEVEL_1
                    )
                }

                Spacer(modifier = Modifier.height(AuraSpacing.sm))

                // Ambient Clock & Date
                Text(
                    text = currentTime,
                    style = AuraTypography.displayLarge,
                    color = AuraColors.AuraWhite,
                    modifier = Modifier.testTag("home_clock")
                )
                Text(
                    text = currentDate.uppercase(),
                    style = AuraTypography.labelSmall,
                    color = AuraColors.Silver,
                    letterSpacing = AuraTypography.labelSmall.letterSpacing
                )
            }

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Central Floating Workspace Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = parallaxOffset.x
                        translationY = parallaxOffset.y
                    },
                contentAlignment = Alignment.Center
            ) {
                if (workspaceItems.isEmpty() && !isAppsLoading) {
                    // Intentional sparse AURA mark when empty
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(AuraSpacing.xl)
                    ) {
                        AuraGlassSurface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            depth = AuraDepth.LEVEL_2,
                            config = AuraMaterialConfig.LiquidDefault.copy(transparency = 0.25f)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "A", style = AuraTypography.displayLarge.copy(fontSize = 32.sp), color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(AuraSpacing.sm))
                        Text(
                            text = "AURA OS",
                            style = AuraTypography.labelSmall,
                            color = AuraColors.Silver
                        )
                        Text(
                            text = "Long press anywhere to customize workspace",
                            style = AuraTypography.bodySmall.copy(fontSize = 11.sp),
                            color = AuraColors.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Spatial Workspace Scrollable Items
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AuraSpacing.md)
                    ) {
                        // Render Widgets first
                        workspaceItems.filter { it.type == AuraWorkspaceItem.Type.WIDGET }.forEach { wItem ->
                            val wData = wItem.widgetData
                            if (wData != null && widgetHostManager != null) {
                                AuraGlassWidgetHost(
                                    widgetData = wData,
                                    widgetHostManager = widgetHostManager,
                                    isEditMode = isEditModeVisible,
                                    onResize = { spanX, spanY -> viewModel.resizeWidget(wItem.id, spanX, spanY) },
                                    onRemove = { viewModel.removeWidget(wData.appWidgetId, wItem.id) }
                                )
                            }
                        }

                        // Render Smart Stacks
                        workspaceItems.filter { it.type == AuraWorkspaceItem.Type.SMART_STACK }.forEach { sItem ->
                            val sData = sItem.stackData
                            if (sData != null) {
                                AuraSmartStackView(
                                    stack = sData,
                                    installedApps = apps,
                                    iconStyle = iconStyle,
                                    onAppClick = onAppClick,
                                    onStackLongClick = { isEditModeVisible = true },
                                    onIndexChanged = { newIdx ->
                                        viewModel?.updateSmartStack(sData.copy(activeIndex = newIdx))
                                    }
                                )
                            }
                        }

                        // Render Folders & Apps in Flow / Grid layout
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.lg, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(AuraSpacing.md)
                        ) {
                            workspaceItems.filter {
                                it.type == AuraWorkspaceItem.Type.APP || it.type == AuraWorkspaceItem.Type.FOLDER
                            }.forEach { item ->
                                when (item.type) {
                                    AuraWorkspaceItem.Type.FOLDER -> {
                                        item.folderData?.let { folder ->
                                            AuraFolderThumbnail(
                                                folder = folder,
                                                installedApps = apps,
                                                onClick = { expandedFolder = folder },
                                                onLongClick = { isEditModeVisible = true },
                                                iconStyle = iconStyle
                                            )
                                        }
                                    }
                                    AuraWorkspaceItem.Type.APP -> {
                                        val app = apps.firstOrNull { it.packageName == item.packageName }
                                            ?: AuraAppItem(packageName = item.packageName, activityName = item.activityName, label = item.label)
                                        AuraAppIcon(
                                            app = app,
                                            onClick = { onAppClick(app) },
                                            onLongClick = { selectedAppForMenu = app },
                                            iconStyle = iconStyle
                                        )
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Action / Dock Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AuraSpacing.lg)
            ) {
                // Swipe Up for Drawer indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleAppDrawer() }
                        .padding(horizontal = AuraSpacing.sm, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Swipe Up for All Applications",
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "APPLICATIONS",
                        style = AuraTypography.labelSmall.copy(fontSize = 10.sp),
                        color = AuraColors.Silver
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Liquid Glass Floating Dock
                AuraGlassDock(
                    dockItems = dockItems,
                    installedApps = apps,
                    onAppClick = onAppClick,
                    onAppLongClick = { selectedAppForMenu = it },
                    iconStyle = iconStyle
                )
            }
        }

        // Expanded Folder Modal Sheet
        expandedFolder?.let { folder ->
            AuraExpandedFolderSheet(
                folder = folder,
                installedApps = apps,
                iconStyle = iconStyle,
                onAppClick = onAppClick,
                onClose = { expandedFolder = null },
                onRenameFolder = { newName ->
                    viewModel?.updateFolder(folder.copy(name = newName))
                    expandedFolder = folder.copy(name = newName)
                },
                onRemoveFromFolder = { pkg ->
                    val updatedList = folder.appPackages.filter { it != pkg }
                    val updated = folder.copy(appPackages = updatedList)
                    viewModel?.updateFolder(updated)
                    expandedFolder = updated
                }
            )
        }

        // Long Press Context Menu
        selectedAppForMenu?.let { app ->
            val isDocked = dockItems.any { it.packageName == app.packageName }
            AuraAppContextMenu(
                app = app,
                iconStyle = iconStyle,
                isDocked = isDocked,
                onDismiss = { selectedAppForMenu = null },
                onOpenApp = { onAppClick(app) },
                onToggleDock = {
                    if (isDocked) {
                        viewModel?.removeFromDock(app.packageName)
                    } else {
                        viewModel?.addToDock(app)
                    }
                },
                onAddToStack = {
                    val targetStack = workspaceItems.firstOrNull { it.type == AuraWorkspaceItem.Type.SMART_STACK }?.stackData
                    if (targetStack != null) {
                        if (!targetStack.appPackages.contains(app.packageName)) {
                            viewModel?.updateSmartStack(targetStack.copy(appPackages = targetStack.appPackages + app.packageName))
                        }
                    } else {
                        viewModel?.createSmartStack("Daily Stack", listOf(app.packageName), 0, 0)
                    }
                },
                onShowWidgets = {
                    isEditModeVisible = true
                },
                onRemoveFromHome = {
                    val matchingItem = workspaceItems.firstOrNull { it.packageName == app.packageName }
                    if (matchingItem != null) {
                        viewModel?.removeWorkspaceItem(matchingItem.id)
                    }
                }
            )
        }

        // Two-Finger Pinch Focus Switcher
        if (isFocusSwitcherVisible) {
            AuraFocusSwitcher(
                currentFocus = currentFocus,
                onSelectFocus = { newFocus ->
                    viewModel?.setFocusContext(newFocus)
                },
                onDismiss = { isFocusSwitcherVisible = false }
            )
        }

        // Spatial Home Edit Surface
        if (isEditModeVisible) {
            AuraHomeEditSurface(
                iconStyle = iconStyle,
                onIconStyleChange = { viewModel?.setIconStyle(it) },
                availableWidgets = availableWidgets,
                onSelectWidget = { widgetInfo ->
                    viewModel?.addWidget(widgetInfo)
                },
                currentFocus = currentFocus,
                onSelectFocus = { viewModel?.setFocusContext(it) },
                onDismiss = { isEditModeVisible = false }
            )
        }

        // Translucent Deep Glass App Drawer (Slide-up)
        AnimatedVisibility(
            visible = globalState.isAppDrawerExpanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            AuraGlassSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("aura_app_drawer"),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                depth = AuraDepth.LEVEL_3,
                config = AuraMaterialConfig.LiquidDefault.copy(
                    transparency = 0.12f,
                    blurRadius = 36f,
                    highlightStrength = 0.75f,
                    borderOpacity = 0.35f
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AuraSpacing.lg)
                ) {
                    Spacer(modifier = Modifier.height(AuraSpacing.xl))

                    // Drawer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onToggleAppDrawer) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Return to Home",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "ALL APPLICATIONS",
                            style = AuraTypography.titleMedium,
                            color = Color.White
                        )

                        IconButton(onClick = onOpenCommand) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Command Surface",
                                tint = AuraColors.Silver
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AuraSpacing.sm))

                    // Instant Search Input
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_search_field"),
                        placeholder = {
                            Text(
                                text = "Search installed applications...",
                                style = AuraTypography.bodyMedium,
                                color = AuraColors.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AuraColors.Silver
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.08f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(AuraSpacing.md))

                    // Filtered Apps Grid
                    val filteredApps = if (searchQuery.isBlank()) {
                        apps
                    } else {
                        apps.filter {
                            it.label.contains(searchQuery, ignoreCase = true) ||
                                    it.packageName.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (isAppsLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AuraSpacing.md)
                        ) {
                            items(filteredApps, key = { it.packageName }) { app ->
                                AuraAppIcon(
                                    app = app,
                                    onClick = {
                                        onAppClick(app)
                                        onToggleAppDrawer()
                                    },
                                    onLongClick = {
                                        selectedAppForMenu = app
                                    },
                                    iconStyle = iconStyle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
