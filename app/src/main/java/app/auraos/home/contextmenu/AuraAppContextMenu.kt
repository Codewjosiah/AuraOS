package app.auraos.home.contextmenu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.auraos.core.system.AuraAppItem
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.icon.AuraAppIcon
import app.auraos.home.model.AuraIconStyle
import app.auraos.material.glass.AuraDepth

/**
 * Long-press app morphing contextual menu.
 * Surfaces spatial glass extensions: Open, App Info, Widgets, Dock pinning, Uninstall, Share, Remove.
 */
@Composable
fun AuraAppContextMenu(
    app: AuraAppItem,
    iconStyle: AuraIconStyle,
    isDocked: Boolean,
    onDismiss: () -> Unit,
    onOpenApp: () -> Unit,
    onToggleDock: () -> Unit,
    onAddToStack: () -> Unit,
    onShowWidgets: () -> Unit,
    onRemoveFromHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_app_context_menu")
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AuraSpacing.lg)
        ) {
            // Morphing anchor icon
            AuraAppIcon(
                app = app,
                onClick = {},
                iconStyle = iconStyle.copy(sizeDp = 68f, showLabels = false)
            )

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            Text(
                text = app.label,
                style = AuraTypography.titleMedium,
                color = Color.White
            )
            Text(
                text = app.packageName,
                style = AuraTypography.bodySmall.copy(fontSize = 11.sp),
                color = AuraColors.Gray
            )

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Spatial Glass Actions Card
            AuraGlassSurface(
                modifier = Modifier
                    .width(280.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Consume taps */ },
                shape = RoundedCornerShape(24.dp),
                depth = AuraDepth.LEVEL_3,
                config = AuraMaterialConfig.LiquidDefault.copy(
                    transparency = 0.18f,
                    blurRadius = 32f,
                    highlightStrength = 0.7f,
                    borderOpacity = 0.38f
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AuraSpacing.sm)
                ) {
                    MenuActionRow(
                        icon = Icons.Default.Launch,
                        label = "Open Application",
                        onClick = {
                            onDismiss()
                            onOpenApp()
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Widgets,
                        label = "Widget Possibilities",
                        onClick = {
                            onDismiss()
                            onShowWidgets()
                        }
                    )

                    MenuActionRow(
                        icon = if (isDocked) Icons.Default.VerticalAlignBottom else Icons.Default.PushPin,
                        label = if (isDocked) "Remove from Dock" else "Pin to Dock",
                        onClick = {
                            onDismiss()
                            onToggleDock()
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Layers,
                        label = "Combine into Smart Stack",
                        onClick = {
                            onDismiss()
                            onAddToStack()
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Info,
                        label = "Application Details",
                        onClick = {
                            onDismiss()
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", app.packageName, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Share,
                        label = "Share Application",
                        onClick = {
                            onDismiss()
                            try {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, app.label)
                                    putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${app.packageName}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share ${app.label}"))
                            } catch (_: Exception) {}
                        }
                    )

                    if (!app.isSystemApp) {
                        MenuActionRow(
                            icon = Icons.Default.Delete,
                            label = "Uninstall",
                            tint = Color(0xFFFF5555),
                            onClick = {
                                onDismiss()
                                try {
                                    val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                                        data = Uri.fromParts("package", app.packageName, null)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(uninstallIntent)
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    MenuActionRow(
                        icon = Icons.Default.Delete,
                        label = "Remove from Workspace",
                        tint = AuraColors.Silver,
                        onClick = {
                            onDismiss()
                            onRemoveFromHome()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = AuraSpacing.md, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(AuraSpacing.md))
        Text(
            text = label,
            style = AuraTypography.titleMedium.copy(fontSize = 13.sp),
            color = tint
        )
    }
}
