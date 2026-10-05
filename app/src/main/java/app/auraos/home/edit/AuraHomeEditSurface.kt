package app.auraos.home.edit

import android.app.WallpaperManager
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.model.AuraFocusContext
import app.auraos.home.model.AuraIconShape
import app.auraos.home.model.AuraIconStyle
import app.auraos.home.widget.AvailableWidgetInfo
import app.auraos.material.glass.AuraDepth
import app.auraos.material.glass.AuraGlassMaterialType

enum class EditSubScreen {
    MAIN,
    WIDGETS,
    ICONS,
    FOCUS
}

/**
 * Spatial Home Edit Mode overlay triggered by long-pressing empty workspace space.
 * Allows adding widgets, customizing icon shapes and materials, toggling breathing/parallax,
 * and selecting Focus modes.
 */
@Composable
fun AuraHomeEditSurface(
    iconStyle: AuraIconStyle,
    onIconStyleChange: (AuraIconStyle) -> Unit,
    availableWidgets: List<AvailableWidgetInfo>,
    onSelectWidget: (AvailableWidgetInfo) -> Unit,
    currentFocus: AuraFocusContext,
    onSelectFocus: (AuraFocusContext) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSubScreen by remember { mutableStateOf(EditSubScreen.MAIN) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_home_edit_surface")
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        AuraGlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AuraSpacing.md)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps */ },
            shape = RoundedCornerShape(32.dp),
            depth = AuraDepth.LEVEL_3,
            config = AuraMaterialConfig.LiquidDefault.copy(
                transparency = 0.16f,
                blurRadius = 36f,
                highlightStrength = 0.75f,
                borderOpacity = 0.40f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuraSpacing.lg)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HOME ENVIRONMENT",
                            style = AuraTypography.labelSmall,
                            color = AuraColors.Silver
                        )
                        Text(
                            text = when (currentSubScreen) {
                                EditSubScreen.MAIN -> "Spatial Customization"
                                EditSubScreen.WIDGETS -> "Add Widget"
                                EditSubScreen.ICONS -> "App Icon Aesthetics"
                                EditSubScreen.FOCUS -> "Focus Context"
                            },
                            style = AuraTypography.titleMedium,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = {
                        if (currentSubScreen != EditSubScreen.MAIN) {
                            currentSubScreen = EditSubScreen.MAIN
                        } else {
                            onDismiss()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Edit Mode",
                            tint = AuraColors.Silver
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AuraSpacing.md))

                when (currentSubScreen) {
                    EditSubScreen.MAIN -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EditActionButton(
                                icon = Icons.Default.Widgets,
                                label = "Widgets",
                                onClick = { currentSubScreen = EditSubScreen.WIDGETS }
                            )
                            EditActionButton(
                                icon = Icons.Default.Brush,
                                label = "Icons",
                                onClick = { currentSubScreen = EditSubScreen.ICONS }
                            )
                            EditActionButton(
                                icon = Icons.Default.Layers,
                                label = "Focus",
                                onClick = { currentSubScreen = EditSubScreen.FOCUS }
                            )
                            EditActionButton(
                                icon = Icons.Default.Wallpaper,
                                label = "Wallpaper",
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_SET_WALLPAPER).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Set Wallpaper"))
                                    } catch (_: Exception) {}
                                }
                            )
                        }
                    }

                    EditSubScreen.WIDGETS -> {
                        if (availableWidgets.isEmpty()) {
                            Text(
                                text = "No installed widgets found.",
                                style = AuraTypography.bodySmall,
                                color = AuraColors.Gray,
                                modifier = Modifier.padding(vertical = AuraSpacing.md)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.height(260.dp)) {
                                items(availableWidgets) { widgetInfo ->
                                    val iconBitmap = remember(widgetInfo.packageName) {
                                        try {
                                            widgetInfo.icon?.toBitmap(64, 64)
                                        } catch (_: Exception) {
                                            null
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSelectWidget(widgetInfo)
                                                onDismiss()
                                            }
                                            .padding(vertical = AuraSpacing.xs),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (iconBitmap != null) {
                                                Image(
                                                    bitmap = iconBitmap.asImageBitmap(),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            } else {
                                                Icon(Icons.Default.Widgets, contentDescription = null, tint = Color.White)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(AuraSpacing.md))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = widgetInfo.label, style = AuraTypography.titleMedium.copy(fontSize = 13.sp), color = Color.White)
                                            Text(text = widgetInfo.packageName, style = AuraTypography.bodySmall.copy(fontSize = 10.sp), color = AuraColors.Gray)
                                        }
                                        Icon(Icons.Default.Add, contentDescription = "Add Widget", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    EditSubScreen.ICONS -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "MATERIAL TYPE", style = AuraTypography.labelSmall, color = AuraColors.Silver)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AuraGlassMaterialType.entries.take(4).forEach { mat ->
                                    FilterChip(
                                        selected = iconStyle.materialType == mat,
                                        onClick = { onIconStyleChange(iconStyle.copy(materialType = mat)) },
                                        label = { Text(mat.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color.White,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color.White.copy(alpha = 0.1f),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(AuraSpacing.sm))

                            Text(text = "ICON SHAPE", style = AuraTypography.labelSmall, color = AuraColors.Silver)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AuraIconShape.entries.take(4).forEach { shp ->
                                    FilterChip(
                                        selected = iconStyle.shape == shp,
                                        onClick = { onIconStyleChange(iconStyle.copy(shape = shp)) },
                                        label = { Text(shp.label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color.White,
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color.White.copy(alpha = 0.1f),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(AuraSpacing.sm))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "App Breathing Animation", style = AuraTypography.bodySmall, color = Color.White)
                                Switch(
                                    checked = iconStyle.breathingEnabled,
                                    onCheckedChange = { onIconStyleChange(iconStyle.copy(breathingEnabled = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Show Icon Text Labels", style = AuraTypography.bodySmall, color = Color.White)
                                Switch(
                                    checked = iconStyle.showLabels,
                                    onCheckedChange = { onIconStyleChange(iconStyle.copy(showLabels = it)) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
                                )
                            }
                        }
                    }

                    EditSubScreen.FOCUS -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AuraFocusContext.entries.forEach { f ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectFocus(f)
                                            onDismiss()
                                        }
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = f.displayName, style = AuraTypography.titleMedium.copy(fontSize = 14.sp), color = Color.White)
                                    if (f == currentFocus) {
                                        Text(text = "Active", style = AuraTypography.labelSmall, color = AuraColors.Silver)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(AuraSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = AuraTypography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White
        )
    }
}
