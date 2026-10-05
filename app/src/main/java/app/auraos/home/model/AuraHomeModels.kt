package app.auraos.home.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import app.auraos.material.glass.AuraGlassMaterialType

enum class AuraIconShape(val label: String) {
    SQUIRCLE("Squircle"),
    ROUNDED_SQUARE("Rounded Square"),
    CIRCLE("Circle"),
    PILL("Pill"),
    CRYSTAL("Crystal");

    fun toComposeShape(cornerRadiusDp: Float = 18f): Shape {
        return when (this) {
            SQUIRCLE -> RoundedCornerShape(cornerRadiusDp.dp)
            ROUNDED_SQUARE -> RoundedCornerShape(12.dp)
            CIRCLE -> CircleShape
            PILL -> RoundedCornerShape(50)
            CRYSTAL -> RoundedCornerShape(8.dp)
        }
    }
}

data class AuraIconStyle(
    val materialType: AuraGlassMaterialType = AuraGlassMaterialType.LIQUID,
    val shape: AuraIconShape = AuraIconShape.SQUIRCLE,
    val sizeDp: Float = 58f,
    val showLabels: Boolean = true,
    val breathingEnabled: Boolean = true
)

enum class AuraFocusContext(val displayName: String, val description: String) {
    NORMAL("Normal", "All applications and standard space"),
    STUDY("Study", "Focus tools, reading, and learning"),
    WORK("Work", "Productivity, email, and task management"),
    GAMING("Gaming", "Performance boost and games"),
    PERSONAL("Personal", "Social, media, and relaxation")
}

data class AuraFolderData(
    val id: String,
    val name: String,
    val appPackages: List<String> = emptyList(),
    val cellX: Int = 0,
    val cellY: Int = 0
)

data class AuraSmartStackData(
    val id: String,
    val name: String = "Smart Stack",
    val appPackages: List<String> = emptyList(), // Typically 4 apps
    val activeIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0
) {
    enum class StackTimeContext {
        MORNING, DAY, NIGHT, MANUAL
    }
}

data class AuraWidgetData(
    val id: String,
    val appWidgetId: Int,
    val providerPackage: String,
    val providerClass: String,
    val label: String = "Widget",
    val spanX: Int = 2,
    val spanY: Int = 2,
    val cellX: Int = 0,
    val cellY: Int = 0
)

data class AuraDockItem(
    val id: String,
    val packageName: String,
    val activityName: String = "",
    val label: String = "",
    val position: Int = 0
)

data class AuraWorkspaceItem(
    val id: String,
    val type: Type,
    val packageName: String = "",
    val activityName: String = "",
    val label: String = "",
    val folderData: AuraFolderData? = null,
    val stackData: AuraSmartStackData? = null,
    val widgetData: AuraWidgetData? = null,
    val focusContext: AuraFocusContext = AuraFocusContext.NORMAL,
    val cellX: Int = 0,
    val cellY: Int = 0
) {
    enum class Type {
        APP,
        FOLDER,
        SMART_STACK,
        WIDGET
    }
}
