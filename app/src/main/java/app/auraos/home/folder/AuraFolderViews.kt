package app.auraos.home.folder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import app.auraos.core.system.AuraAppItem
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.icon.AuraAppIcon
import app.auraos.home.model.AuraFolderData
import app.auraos.home.model.AuraIconStyle
import app.auraos.material.glass.AuraDepth

/**
 * Closed spatial glass folder icon preview on the workspace.
 * Displays a 2x2 miniature cluster of contained app logos within a liquid glass substrate.
 */
@Composable
fun AuraFolderThumbnail(
    folder: AuraFolderData,
    installedApps: List<AuraAppItem>,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    iconStyle: AuraIconStyle = AuraIconStyle(),
    modifier: Modifier = Modifier
) {
    val folderApps = remember(folder.appPackages, installedApps) {
        folder.appPackages.mapNotNull { pkg -> installedApps.firstOrNull { it.packageName == pkg } }
    }

    val shape = iconStyle.shape.toComposeShape()
    val sizeDp = iconStyle.sizeDp.dp

    Column(
        modifier = modifier
            .semantics { contentDescription = "Folder ${folder.name}, contains ${folderApps.size} apps" }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuraGlassSurface(
            modifier = Modifier.size(sizeDp),
            shape = shape,
            depth = AuraDepth.LEVEL_2,
            config = AuraMaterialConfig.LiquidDefault.copy(
                transparency = 0.22f,
                highlightStrength = 0.65f,
                borderOpacity = 0.35f
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                // 2x2 Mini Cluster
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        MiniIcon(folderApps.getOrNull(0))
                        MiniIcon(folderApps.getOrNull(1))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        MiniIcon(folderApps.getOrNull(2))
                        MiniIcon(folderApps.getOrNull(3))
                    }
                }
            }
        }

        if (iconStyle.showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = folder.name,
                style = AuraTypography.bodySmall.copy(fontSize = 11.sp),
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(width = sizeDp + 16.dp, height = 16.dp)
            )
        }
    }
}

@Composable
private fun MiniIcon(app: AuraAppItem?) {
    val bitmap = remember(app?.packageName) {
        try {
            app?.icon?.toBitmap(48, 48)
        } catch (_: Exception) {
            null
        }
    }
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Expanded spatial liquid glass sheet for folders.
 * Features inline folder renaming, apps grid, and physical launch.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraExpandedFolderSheet(
    folder: AuraFolderData,
    installedApps: List<AuraAppItem>,
    iconStyle: AuraIconStyle,
    onAppClick: (AuraAppItem) -> Unit,
    onClose: () -> Unit,
    onRenameFolder: (String) -> Unit,
    onRemoveFromFolder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRenaming by remember { mutableStateOf(false) }
    var folderNameInput by remember(folder.name) { mutableStateOf(folder.name) }

    val folderApps = remember(folder.appPackages, installedApps) {
        folder.appPackages.mapNotNull { pkg -> installedApps.firstOrNull { it.packageName == pkg } }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() },
        contentAlignment = Alignment.Center
    ) {
        AuraGlassSurface(
            modifier = Modifier
                .width(320.dp)
                .padding(AuraSpacing.md)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps inside */ },
            shape = RoundedCornerShape(28.dp),
            depth = AuraDepth.LEVEL_3,
            config = AuraMaterialConfig.LiquidDefault.copy(
                transparency = 0.20f,
                blurRadius = 32f,
                highlightStrength = 0.7f,
                borderOpacity = 0.38f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuraSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Rename & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRenaming) {
                        OutlinedTextField(
                            value = folderNameInput,
                            onValueChange = { folderNameInput = it },
                            singleLine = true,
                            textStyle = AuraTypography.titleMedium.copy(color = Color.White),
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = AuraColors.Gray
                            )
                        )
                        IconButton(onClick = {
                            if (folderNameInput.isNotBlank()) {
                                onRenameFolder(folderNameInput.trim())
                            }
                            isRenaming = false
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Save Name", tint = Color.White)
                        }
                    } else {
                        Text(
                            text = folder.name,
                            style = AuraTypography.titleMedium,
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isRenaming = true }
                        )
                        IconButton(onClick = { isRenaming = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = AuraColors.Silver, modifier = Modifier.size(18.dp))
                        }
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close Folder", tint = AuraColors.Silver)
                    }
                }

                Spacer(modifier = Modifier.height(AuraSpacing.md))

                // Folder Apps Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(AuraSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AuraSpacing.md)
                ) {
                    items(folderApps, key = { it.packageName }) { app ->
                        AuraAppIcon(
                            app = app,
                            onClick = {
                                onAppClick(app)
                                onClose()
                            },
                            onLongClick = { onRemoveFromFolder(app.packageName) },
                            iconStyle = iconStyle
                        )
                    }
                }
            }
        }
    }
}
