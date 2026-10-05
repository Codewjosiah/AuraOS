package app.auraos.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
object AuraShapes {
    val roundedXs = RoundedCornerShape(8.dp)
    val roundedSm = RoundedCornerShape(14.dp)
    val roundedMd = RoundedCornerShape(20.dp)
    val roundedLg = RoundedCornerShape(28.dp)
    val roundedXl = RoundedCornerShape(36.dp)
    val pill = RoundedCornerShape(999.dp)
}

@Immutable
object AuraSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val touchTarget = 48.dp
}
