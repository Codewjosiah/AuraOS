package app.auraos.shell.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.animation.AuraMotion
import app.auraos.design.theme.Aura
import app.auraos.shell.state.AuraShellViewModel
import app.auraos.shell.state.AuraSurface

@Composable
fun AuraShellRoot(
    viewModel: AuraShellViewModel,
    modifier: Modifier = Modifier
) {
    val globalState by viewModel.globalState.collectAsState()
    val apps by viewModel.appRepository.installedApps.collectAsState()
    val isAppsLoading by viewModel.appRepository.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Back handling: close inspector, then drawer, then return to HOME
    BackHandler(
        enabled = globalState.isDebugInspectorVisible ||
                globalState.isAppDrawerExpanded ||
                globalState.currentSurface != AuraSurface.HOME
    ) {
        if (globalState.isDebugInspectorVisible) {
            viewModel.toggleDebugInspector()
        } else if (globalState.isAppDrawerExpanded) {
            viewModel.toggleAppDrawer()
        } else {
            viewModel.returnToHome()
        }
    }

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }

    val gestureModifier = Modifier.pointerInput(globalState.currentSurface) {
        detectHorizontalDragGestures(
            onDragStart = { totalDragX = 0f },
            onDragEnd = {
                if (totalDragX < -140f && globalState.currentSurface == AuraSurface.HOME) {
                    viewModel.navigateToSurface(AuraSurface.ORB)
                } else if (totalDragX > 140f && globalState.currentSurface == AuraSurface.HOME) {
                    viewModel.navigateToSurface(AuraSurface.RIVER)
                } else if (totalDragX < -140f && globalState.currentSurface == AuraSurface.RIVER) {
                    viewModel.navigateToSurface(AuraSurface.HOME)
                } else if (totalDragX > 140f && globalState.currentSurface == AuraSurface.ORB) {
                    viewModel.navigateToSurface(AuraSurface.HOME)
                }
            },
            onHorizontalDrag = { _, dragAmount ->
                totalDragX += dragAmount
            }
        )
    }.pointerInput(globalState.currentSurface) {
        detectVerticalDragGestures(
            onDragStart = { totalDragY = 0f },
            onDragEnd = {
                if (totalDragY > 150f && globalState.currentSurface == AuraSurface.HOME) {
                    viewModel.navigateToSurface(AuraSurface.COMMAND)
                } else if (totalDragY < -150f && globalState.currentSurface == AuraSurface.COMMAND) {
                    viewModel.navigateToSurface(AuraSurface.HOME)
                }
            },
            onVerticalDrag = { _, dragAmount ->
                totalDragY += dragAmount
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(gestureModifier)
            .testTag("aura_shell_root")
    ) {
        // Universal Wallpaper / Liquid Substrate Backdrop
        AuraWallpaperBackdrop(wallpaperState = globalState.wallpaperState)

        // Continuous Spatial Surfaces
        AnimatedContent(
            targetState = globalState.currentSurface,
            transitionSpec = {
                val reducedMotion = globalState.deviceCapability.isReducedMotionPreferred
                when {
                    targetState == AuraSurface.RIVER -> {
                        (slideInHorizontally(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { -it } + fadeIn())
                            .togetherWith(slideOutHorizontally(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { it } + fadeOut())
                    }
                    targetState == AuraSurface.ORB -> {
                        (slideInHorizontally(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { it } + fadeIn())
                            .togetherWith(slideOutHorizontally(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { -it } + fadeOut())
                    }
                    targetState == AuraSurface.COMMAND -> {
                        (slideInVertically(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { -it } + fadeIn())
                            .togetherWith(slideOutVertically(animationSpec = AuraMotion.auraStandard<IntOffset>(reducedMotion)) { it } + fadeOut())
                    }
                    else -> {
                        fadeIn(animationSpec = AuraMotion.auraFast<Float>(reducedMotion))
                            .togetherWith(fadeOut(animationSpec = AuraMotion.auraFast<Float>(reducedMotion)))
                    }
                }
            },
            label = "SurfaceTransition"
        ) { surface ->
            when (surface) {
                AuraSurface.HOME -> AuraHomeSurface(
                    globalState = globalState,
                    apps = apps,
                    isAppsLoading = isAppsLoading,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onAppClick = { viewModel.launchApp(it) },
                    onToggleAppDrawer = viewModel::toggleAppDrawer,
                    onOpenCommand = { viewModel.navigateToSurface(AuraSurface.COMMAND) },
                    onNavigateRiver = { viewModel.navigateToSurface(AuraSurface.RIVER) },
                    onNavigateOrb = { viewModel.navigateToSurface(AuraSurface.ORB) },
                    viewModel = viewModel
                )
                AuraSurface.RIVER -> AuraRiverSurface(
                    globalState = globalState,
                    onReturnHome = viewModel::returnToHome
                )
                AuraSurface.ORB -> AuraOrbSurface(
                    globalState = globalState,
                    onReturnHome = viewModel::returnToHome
                )
                AuraSurface.COMMAND -> AuraCommandSurface(
                    globalState = globalState,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onOpenApps = {
                        viewModel.returnToHome()
                        viewModel.toggleAppDrawer()
                    },
                    onOpenDebug = {
                        viewModel.toggleDebugInspector()
                    },
                    onOpenGallery = {
                        viewModel.navigateToSurface(AuraSurface.MATERIAL_GALLERY)
                    },
                    onCloseCommand = viewModel::returnToHome
                )
                AuraSurface.MATERIAL_GALLERY -> app.auraos.material.glass.AuraMaterialGallery(
                    currentProfile = globalState.performanceProfile,
                    onProfileChange = viewModel::setManualPerformanceOverride,
                    onClose = viewModel::returnToHome
                )
                else -> AuraHomeSurface(
                    globalState = globalState,
                    apps = apps,
                    isAppsLoading = isAppsLoading,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onAppClick = { viewModel.launchApp(it) },
                    onToggleAppDrawer = viewModel::toggleAppDrawer,
                    onOpenCommand = { viewModel.navigateToSurface(AuraSurface.COMMAND) },
                    onNavigateRiver = { viewModel.navigateToSurface(AuraSurface.RIVER) },
                    onNavigateOrb = { viewModel.navigateToSurface(AuraSurface.ORB) }
                )
            }
        }

        // Top Floating Live Pill
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        AuraLivePillView(
            globalState = globalState,
            onPillClick = viewModel::toggleDebugInspector,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusBarPadding)
        )

        // Debug Inspector Overlay
        AnimatedVisibility(
            visible = globalState.isDebugInspectorVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            AuraDebugInspector(
                globalState = globalState,
                onDismiss = viewModel::toggleDebugInspector,
                onSelectProfileOverride = viewModel::setManualPerformanceOverride
            )
        }
    }
}
