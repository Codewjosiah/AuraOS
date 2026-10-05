package app.auraos.material.glass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.design.theme.AuraColors
import app.auraos.design.theme.AuraShapes
import app.auraos.design.theme.AuraSpacing
import app.auraos.design.theme.AuraTypography

/**
 * Developer-only Live AURA Material Inspector.
 * Allows real-time fine-tuning of all optical parameters.
 */
@Composable
fun AuraMaterialInspector(
    config: AuraMaterialConfig,
    onConfigChange: (AuraMaterialConfig) -> Unit,
    currentProfile: AuraPerformanceProfile,
    onProfileChange: (AuraPerformanceProfile) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuraGlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(AuraSpacing.md)
            .testTag("aura_material_inspector"),
        shape = AuraShapes.roundedXl,
        depth = AuraDepth.LEVEL_4
    ) {
        Column(
            modifier = Modifier
                .padding(AuraSpacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Inspector",
                        tint = AuraColors.AuraWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(AuraSpacing.xs))
                    Text(
                        text = "MATERIAL INSPECTOR",
                        style = AuraTypography.titleLarge
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        onConfigChange(AuraMaterialConfig.forProfileAndType(currentProfile, config.materialType))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = AuraColors.Silver
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AuraColors.AuraWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.sm))

            // Performance Profile
            Text(text = "PERFORMANCE PROFILE", style = AuraTypography.labelSmall, color = AuraColors.Gray)
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AuraSpacing.xs)
            ) {
                AuraPerformanceProfile.entries.forEach { profile ->
                    val isSelected = currentProfile == profile
                    AuraGlassButton(
                        onClick = { onProfileChange(profile) },
                        modifier = Modifier.weight(1f),
                        shape = AuraShapes.roundedSm
                    ) {
                        Text(
                            text = profile.name,
                            style = AuraTypography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Material Type Selector
            Text(text = "GLASS MATERIAL TYPE", style = AuraTypography.labelSmall, color = AuraColors.Gray)
            Spacer(modifier = Modifier.height(AuraSpacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AuraGlassMaterialType.entries.take(3).forEach { matType ->
                    val isSelected = config.materialType == matType
                    AuraGlassButton(
                        onClick = {
                            onConfigChange(AuraMaterialConfig.forProfileAndType(currentProfile, matType))
                        },
                        modifier = Modifier.weight(1f),
                        shape = AuraShapes.roundedSm
                    ) {
                        Text(
                            text = matType.title,
                            style = AuraTypography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AuraGlassMaterialType.entries.drop(3).forEach { matType ->
                    val isSelected = config.materialType == matType
                    AuraGlassButton(
                        onClick = {
                            onConfigChange(AuraMaterialConfig.forProfileAndType(currentProfile, matType))
                        },
                        modifier = Modifier.weight(1f),
                        shape = AuraShapes.roundedSm
                    ) {
                        Text(
                            text = matType.title,
                            style = AuraTypography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuraColors.AuraWhite else AuraColors.Silver
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.md))

            // Sliders for all optical parameters
            Text(text = "OPTICAL PARAMETERS", style = AuraTypography.labelSmall, color = AuraColors.Gray)
            Spacer(modifier = Modifier.height(AuraSpacing.xs))

            AuraGlassSlider(
                label = "Blur Radius",
                value = config.blurRadius,
                valueRange = 0f..48f,
                valueDisplay = "${config.blurRadius.toInt()} dp",
                onValueChange = { onConfigChange(config.copy(blurRadius = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Transparency",
                value = config.transparency,
                valueRange = 0.0f..0.9f,
                valueDisplay = "${(config.transparency * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(transparency = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Edge Highlight Strength",
                value = config.highlightStrength,
                valueRange = 0.0f..1.0f,
                valueDisplay = "${(config.highlightStrength * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(highlightStrength = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Border Opacity",
                value = config.borderOpacity,
                valueRange = 0.0f..1.0f,
                valueDisplay = "${(config.borderOpacity * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(borderOpacity = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Color Bleed (Wallpaper)",
                value = config.colorBleed,
                valueRange = 0.0f..1.5f,
                valueDisplay = "${(config.colorBleed * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(colorBleed = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Specular Reflection",
                value = config.reflectionStrength,
                valueRange = 0.0f..1.0f,
                valueDisplay = "${(config.reflectionStrength * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(reflectionStrength = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Shadow Strength",
                value = config.shadowStrength,
                valueRange = 0.0f..1.0f,
                valueDisplay = "${(config.shadowStrength * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(shadowStrength = it).clamped()) }
            )

            AuraGlassSlider(
                label = "Lighting Intensity",
                value = config.lightingStrength,
                valueRange = 0.0f..1.0f,
                valueDisplay = "${(config.lightingStrength * 100).toInt()}%",
                onValueChange = { onConfigChange(config.copy(lightingStrength = it).clamped()) }
            )
        }
    }
}
