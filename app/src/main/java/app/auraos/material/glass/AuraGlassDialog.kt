package app.auraos.material.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing

@Composable
fun AuraGlassDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_4,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable BoxScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        AuraGlassSurface(
            modifier = modifier.padding(AuraSpacing.lg),
            shape = AuraShapes.roundedXl,
            materialType = materialType,
            depth = depth
        ) {
            Box(
                modifier = Modifier.padding(AuraSpacing.lg),
                content = content
            )
        }
    }
}
