package app.auraos.home.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.surface.AuraGlassSurface
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography
import app.auraos.home.model.AuraWidgetData
import app.auraos.material.glass.AuraDepth

data class AvailableWidgetInfo(
    val providerInfo: AppWidgetProviderInfo,
    val label: String,
    val icon: Drawable?,
    val packageName: String,
    val minWidthDp: Int,
    val minHeightDp: Int
)

class AuraWidgetHostManager(
    private val context: Context,
    private val hostId: Int = 1042
) {
    private val appWidgetManager: AppWidgetManager? =
        AppWidgetManager.getInstance(context)
    val appWidgetHost: AppWidgetHost = AppWidgetHost(context, hostId)

    fun startListening() {
        try {
            appWidgetHost.startListening()
        } catch (_: Exception) {}
    }

    fun stopListening() {
        try {
            appWidgetHost.stopListening()
        } catch (_: Exception) {}
    }

    fun allocateAppWidgetId(): Int {
        return appWidgetHost.allocateAppWidgetId()
    }

    fun deleteAppWidgetId(appWidgetId: Int) {
        try {
            appWidgetHost.deleteAppWidgetId(appWidgetId)
        } catch (_: Exception) {}
    }

    fun getInstalledWidgets(): List<AvailableWidgetInfo> {
        val manager = appWidgetManager ?: return emptyList()
        val pm = context.packageManager
        return try {
            val providers = manager.installedProviders ?: emptyList()
            providers.map { provider ->
                val label = try {
                    provider.loadLabel(pm)
                } catch (_: Exception) {
                    provider.provider.shortClassName
                }
                val icon = try {
                    provider.loadIcon(context, 0)
                } catch (_: Exception) {
                    null
                }
                AvailableWidgetInfo(
                    providerInfo = provider,
                    label = label,
                    icon = icon,
                    packageName = provider.provider.packageName,
                    minWidthDp = provider.minWidth,
                    minHeightDp = provider.minHeight
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun createWidgetView(context: Context, appWidgetId: Int): AppWidgetHostView? {
        val manager = appWidgetManager ?: return null
        return try {
            val info = manager.getAppWidgetInfo(appWidgetId) ?: return null
            val view = appWidgetHost.createView(context, appWidgetId, info)
            view.setAppWidget(appWidgetId, info)
            view
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Spatial Liquid Glass Frame for Android App Widgets.
 * Preserves widget interactive touch, adds specular rim lighting and optional resize handles.
 */
@Composable
fun AuraGlassWidgetHost(
    widgetData: AuraWidgetData,
    widgetHostManager: AuraWidgetHostManager,
    isEditMode: Boolean = false,
    onResize: (Int, Int) -> Unit = { _, _ -> },
    onRemove: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val widgetView = remember(widgetData.appWidgetId) {
        widgetHostManager.createWidgetView(context, widgetData.appWidgetId)
    }

    AuraGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height((widgetData.spanY * 80 + 32).dp),
        shape = RoundedCornerShape(22.dp),
        depth = AuraDepth.LEVEL_2,
        config = AuraMaterialConfig.LiquidDefault.copy(
            transparency = 0.22f,
            highlightStrength = 0.6f,
            borderOpacity = 0.3f
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (widgetView != null) {
                AndroidView(
                    factory = { widgetView },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AuraSpacing.sm)
                )
            } else {
                // Elegant fallback representation if widget view cannot be instantiated
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AuraSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = widgetData.label,
                        tint = AuraColors.Silver,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(AuraSpacing.xs))
                    Text(
                        text = widgetData.label,
                        style = AuraTypography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = widgetData.providerPackage,
                        style = AuraTypography.bodySmall,
                        color = AuraColors.Gray
                    )
                }
            }

            // Edit Mode Spatial Overlay
            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                ) {
                    // Remove button
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(AuraSpacing.xs)
                            .size(32.dp)
                            .background(Color.Red.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Widget",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Resize indicator
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(AuraSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val nextSpan = if (widgetData.spanY == 2) 3 else 2
                                onResize(widgetData.spanX, nextSpan)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenWith,
                                contentDescription = "Resize Widget",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
