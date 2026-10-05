package app.auraos.home.stack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.auraos.core.system.AuraAppItem
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.icon.AuraAppIcon
import app.auraos.home.model.AuraIconStyle
import app.auraos.home.model.AuraSmartStackData
import app.auraos.material.glass.AuraDepth
import java.util.Calendar

/**
 * AURA Smart Stack Component.
 * Combines 4 apps into a single spatial liquid glass object.
 * Automatically adapts context between Morning, Day, and Night,
 * or supports manual cycling with physical rising/receding transitions.
 */
@Composable
fun AuraSmartStackView(
    stack: AuraSmartStackData,
    installedApps: List<AuraAppItem>,
    iconStyle: AuraIconStyle,
    onAppClick: (AuraAppItem) -> Unit,
    onStackLongClick: () -> Unit,
    onIndexChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isReducedMotion = Aura.isReducedMotion
    var isPressed by remember { mutableStateOf(false) }

    // Resolve Context State: Morning (5-11), Day (12-17), Night (18-4)
    val contextState = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> AuraSmartStackData.StackTimeContext.MORNING
            in 12..17 -> AuraSmartStackData.StackTimeContext.DAY
            else -> AuraSmartStackData.StackTimeContext.NIGHT
        }
    }

    val contextLabel = when (contextState) {
        AuraSmartStackData.StackTimeContext.MORNING -> "Morning Context"
        AuraSmartStackData.StackTimeContext.DAY -> "Day Context"
        AuraSmartStackData.StackTimeContext.NIGHT -> "Night Context"
        AuraSmartStackData.StackTimeContext.MANUAL -> "Custom Stack"
    }

    // Apps contained in this stack
    val appsInStack = remember(stack.appPackages, installedApps) {
        stack.appPackages.mapNotNull { pkg -> installedApps.firstOrNull { it.packageName == pkg } }
    }

    val activeApp = if (appsInStack.isNotEmpty()) {
        appsInStack[stack.activeIndex.coerceIn(0, appsInStack.size - 1)]
    } else null

    // Stretch scale on interaction
    val stretchScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = AuraMotion.springSnap(isReducedMotion),
        label = "AuraStackStretch"
    )

    AuraGlassSurface(
        modifier = modifier
            .width(180.dp)
            .height(110.dp)
            .semantics { contentDescription = "Smart Stack: ${stack.name}, showing ${activeApp?.label ?: "empty"}" }
            .graphicsLayer {
                scaleX = stretchScale
                scaleY = stretchScale
            }
            .pointerInput(stack.id) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        // Advance to next app in stack
                        if (appsInStack.isNotEmpty()) {
                            val nextIdx = (stack.activeIndex + 1) % appsInStack.size
                            onIndexChanged(nextIdx)
                        }
                    },
                    onLongPress = { onStackLongClick() }
                )
            },
        shape = RoundedCornerShape(24.dp),
        depth = AuraDepth.LEVEL_2,
        config = AuraMaterialConfig.LiquidDefault.copy(
            transparency = 0.20f,
            highlightStrength = 0.65f,
            borderOpacity = 0.32f
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AuraSpacing.sm),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Stack Name & Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = contextLabel,
                        style = AuraTypography.labelSmall.copy(fontSize = 9.sp),
                        color = AuraColors.Silver
                    )
                }

                // Page dots indicator
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(appsInStack.size.coerceAtLeast(1)) { idx ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .graphicsLayer {
                                    alpha = if (idx == stack.activeIndex) 1.0f else 0.35f
                                }
                                .background(Color.White, CircleShape)
                        )
                    }
                }
            }

            // Main Active App Content with Physical Rise/Recede Transition
            AnimatedContent(
                targetState = activeApp,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut()
                    )
                },
                label = "SmartStackMorph"
            ) { app ->
                if (app != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AuraAppIcon(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = onStackLongClick,
                            iconStyle = iconStyle.copy(showLabels = false, sizeDp = 46f)
                        )

                        Spacer(modifier = Modifier.width(AuraSpacing.sm))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.label,
                                style = AuraTypography.titleMedium.copy(fontSize = 13.sp),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Tap to launch",
                                style = AuraTypography.bodySmall.copy(fontSize = 10.sp),
                                color = AuraColors.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AuraColors.Silver,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Empty Stack",
                            style = AuraTypography.bodySmall,
                            color = AuraColors.Gray
                        )
                    }
                }
            }
        }
    }
}
