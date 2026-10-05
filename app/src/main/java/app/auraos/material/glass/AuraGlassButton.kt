package app.auraos.material.glass

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography

@Composable
fun AuraGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    materialType: AuraGlassMaterialType? = null,
    depth: AuraDepth = AuraDepth.LEVEL_1,
    shape: Shape = AuraShapes.roundedSm,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val interactionState = when {
        !enabled -> AuraInteractionState.DISABLED
        isLoading -> AuraInteractionState.LOADING
        isPressed -> AuraInteractionState.PRESSED
        isFocused -> AuraInteractionState.FOCUSED
        else -> AuraInteractionState.RESTING
    }

    AuraGlassSurface(
        modifier = modifier
            .defaultMinSize(minHeight = AuraSpacing.touchTarget, minWidth = 48.dp)
            .semantics { role = Role.Button }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = {
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    } catch (_: Exception) {}
                    onClick()
                }
            ),
        shape = shape,
        materialType = materialType,
        depth = depth,
        interactionState = interactionState
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AuraSpacing.md, vertical = AuraSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (content != null) {
                content()
            } else if (isLoading) {
                CircularProgressIndicator(
                    color = AuraColors.AuraWhite,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                if (text != null) {
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Text(
                        text = text,
                        style = AuraTypography.labelLarge,
                        color = AuraColors.Silver
                    )
                }
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) AuraColors.AuraWhite else AuraColors.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (icon != null && text != null) {
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                }
                if (text != null) {
                    Text(
                        text = text,
                        style = AuraTypography.labelLarge,
                        color = if (enabled) AuraColors.AuraWhite else AuraColors.Gray
                    )
                }
            }
        }
    }
}
