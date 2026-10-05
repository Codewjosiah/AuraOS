package app.auraos.home.icon

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.core.system.AuraAppItem
import app.auraos.design.animation.AuraMotion
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.Aura
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.model.AuraIconStyle
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraGlassMaterialType
import app.auraos.material.glass.AuraInteractionState
import java.util.Calendar

/**
 * Universal AURA Glass App Icon.
 * Transforms installed Android application icons into physical AURA liquid glass objects
 * with global overhead specular lighting, live dynamic state indicators, subtle breathing,
 * and spring touch compression.
 */
@Composable
fun AuraAppIcon(
    app: AuraAppItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    iconStyle: AuraIconStyle = AuraIconStyle(),
    modifier: Modifier = Modifier,
    isDock: Boolean = false,
    hasNotification: Boolean = false,
    playbackActive: Boolean = false
) {
    val context = LocalContext.current
    val profile = Aura.performanceProfile
    val isReducedMotion = Aura.isReducedMotion
    var isPressed by remember { mutableStateOf(false) }

    // Touch spring compression
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1.0f,
        animationSpec = AuraMotion.springSnap(isReducedMotion),
        label = "AuraIconPressScale"
    )

    // Subtle idle breathing animation (only on FULL profile when enabled and not reduced motion)
    val breathingScale = if (
        profile == AuraPerformanceProfile.FULL &&
        iconStyle.breathingEnabled &&
        !isReducedMotion
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "IconBreathing")
        val breath by infiniteTransition.animateFloat(
            initialValue = 0.995f,
            targetValue = 1.008f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "IconBreathingScale"
        )
        breath
    } else {
        1.0f
    }

    val iconBitmap = remember(app.packageName) {
        try {
            app.icon?.toBitmap(width = 144, height = 144)
        } catch (_: Exception) {
            null
        }
    }

    val shape = iconStyle.shape.toComposeShape()
    val iconSize = (if (isDock) iconStyle.sizeDp - 4f else iconStyle.sizeDp).dp

    // Dynamic Live Information Detection
    val isClockApp = remember(app.packageName) {
        app.packageName.contains("clock") || app.packageName.contains("deskclock")
    }
    val isBatteryOrSettings = remember(app.packageName) {
        app.packageName.contains("settings") || app.packageName.contains("battery")
    }
    val isMediaApp = remember(app.packageName) {
        app.packageName.contains("music") || app.packageName.contains("spotify") ||
                app.packageName.contains("youtube") || app.packageName.contains("podcast")
    }

    // Material configuration matching selected icon material
    val materialType = iconStyle.materialType
    val materialConfig = remember(materialType, profile) {
        AuraMaterialConfig.forProfileAndType(profile, materialType)
    }

    // Coherent Global Overhead Lighting Model (Upper-Left source)
    val topSheen = Color.White.copy(
        alpha = if (isPressed) 0.55f else (0.28f * materialConfig.highlightStrength)
    )
    val bottomShadow = Color.Black.copy(
        alpha = (0.45f * materialConfig.shadowStrength).coerceIn(0.1f, 0.7f)
    )
    val specularRimBrush = Brush.linearGradient(
        0.0f to topSheen,
        0.4f to Color.White.copy(alpha = 0.08f * materialConfig.highlightStrength),
        1.0f to bottomShadow
    )

    Column(
        modifier = modifier
            .semantics { contentDescription = "${app.label} application" }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    scaleX = animatedScale * breathingScale
                    scaleY = animatedScale * breathingScale
                }
                .shadow(
                    elevation = if (isPressed) 2.dp else 6.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.55f)
                )
                .clip(shape)
                .background(AuraColors.NearBlack.copy(alpha = 0.78f))
                .border(width = 1.dp, brush = specularRimBrush, shape = shape),
            contentAlignment = Alignment.Center
        ) {
            // App Logo
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize * 0.72f)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = app.label.take(1).uppercase(),
                    style = AuraTypography.titleMedium,
                    color = Color.White
                )
            }

            // Overhead Optical Sheen Reflection
            if (materialConfig.reflectionStrength > 0.05f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.16f * materialConfig.reflectionStrength),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Live State Indicator: Clock hands or live dot
            if (isClockApp) {
                val cal = Calendar.getInstance()
                val minutes = cal.get(Calendar.MINUTE)
                val hours = cal.get(Calendar.HOUR)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(14.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            // Live State Indicator: Media playback dot
            if (isMediaApp && playbackActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(8.dp)
                        .background(Color(0xFF00FF66), CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                )
            }

            // Live State Indicator: Notification Pip
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(9.dp)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, AuraColors.NearBlack, CircleShape)
                )
            }
        }

        // App Label (hidden in dock or when configured off)
        if (!isDock && iconStyle.showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                style = AuraTypography.bodySmall.copy(fontSize = 11.sp),
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(width = iconSize + 16.dp, height = 16.dp)
            )
        }
    }
}
