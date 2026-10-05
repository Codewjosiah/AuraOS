package app.auraos.home.dock

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.auraos.core.system.AuraAppItem
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraSpacing
import app.auraos.home.icon.AuraAppIcon
import app.auraos.home.model.AuraDockItem
import app.auraos.home.model.AuraIconStyle
import app.auraos.material.glass.AuraDepth

/**
 * Floating Liquid Glass Dock.
 * Floats above wallpaper with dynamic edge highlights, subtle tactile spring stretch,
 * and persistent app shortcuts.
 */
@Composable
fun AuraGlassDock(
    dockItems: List<AuraDockItem>,
    installedApps: List<AuraAppItem>,
    onAppClick: (AuraAppItem) -> Unit,
    onAppLongClick: (AuraAppItem) -> Unit,
    iconStyle: AuraIconStyle,
    modifier: Modifier = Modifier
) {
    val isReducedMotion = Aura.isReducedMotion
    var isDockInteracted by remember { mutableStateOf(false) }

    val dockStretchScale by animateFloatAsState(
        targetValue = if (isDockInteracted) 1.025f else 1.0f,
        animationSpec = AuraMotion.springGentle(isReducedMotion),
        label = "AuraDockStretchScale"
    )

    AuraGlassSurface(
        modifier = modifier
            .testTag("aura_glass_dock")
            .semantics { contentDescription = "AURA Glass Application Dock" }
            .graphicsLayer {
                scaleX = dockStretchScale
                scaleY = dockStretchScale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isDockInteracted = true
                        tryAwaitRelease()
                        isDockInteracted = false
                    }
                )
            },
        shape = RoundedCornerShape(32.dp),
        depth = AuraDepth.LEVEL_3,
        config = AuraMaterialConfig.LiquidDefault.copy(
            transparency = 0.25f,
            blurRadius = 28f,
            highlightStrength = 0.65f,
            borderOpacity = 0.32f,
            shadowStrength = 0.55f
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = AuraSpacing.lg, vertical = 10.dp)
                .widthIn(min = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(AuraSpacing.md, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockItems.forEach { dockItem ->
                val app = installedApps.firstOrNull { it.packageName == dockItem.packageName }
                    ?: AuraAppItem(
                        packageName = dockItem.packageName,
                        activityName = dockItem.activityName,
                        label = dockItem.label.ifEmpty { "App" }
                    )

                AuraAppIcon(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    iconStyle = iconStyle,
                    isDock = true
                )
            }
        }
    }
}
