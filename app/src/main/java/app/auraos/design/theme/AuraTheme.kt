package app.auraos.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import app.auraos.core.performance.AuraPerformanceProfile
import app.auraos.design.material.AuraMaterialConfig
import app.auraos.material.lighting.AuraLightingModel

val LocalAuraPerformanceProfile = staticCompositionLocalOf { AuraPerformanceProfile.BALANCED }
val LocalAuraMaterialConfig = staticCompositionLocalOf { AuraMaterialConfig.Default }
val LocalAuraLighting = staticCompositionLocalOf { AuraLightingModel.Default }
val LocalAuraReducedMotion = staticCompositionLocalOf { false }

private val AuraDarkColorScheme = darkColorScheme(
    primary = AuraColors.AuraWhite,
    onPrimary = AuraColors.AuraBlack,
    surface = AuraColors.NearBlack,
    onSurface = AuraColors.AuraWhite,
    background = AuraColors.AuraBlack,
    onBackground = AuraColors.AuraWhite
)

@Composable
fun AuraTheme(
    performanceProfile: AuraPerformanceProfile = AuraPerformanceProfile.BALANCED,
    materialConfig: AuraMaterialConfig = AuraMaterialConfig.forProfile(performanceProfile),
    lightingModel: AuraLightingModel = AuraLightingModel.Default,
    isReducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAuraPerformanceProfile provides performanceProfile,
        LocalAuraMaterialConfig provides materialConfig,
        LocalAuraLighting provides lightingModel,
        LocalAuraReducedMotion provides isReducedMotion
    ) {
        MaterialTheme(
            colorScheme = AuraDarkColorScheme,
            content = content
        )
    }
}

object Aura {
    val colors: AuraColors
        @Composable
        @ReadOnlyComposable
        get() = AuraColors

    val typography: AuraTypography
        @Composable
        @ReadOnlyComposable
        get() = AuraTypography

    val shapes: AuraShapes
        @Composable
        @ReadOnlyComposable
        get() = AuraShapes

    val spacing: AuraSpacing
        @Composable
        @ReadOnlyComposable
        get() = AuraSpacing

    val performanceProfile: AuraPerformanceProfile
        @Composable
        @ReadOnlyComposable
        get() = LocalAuraPerformanceProfile.current

    val materialConfig: AuraMaterialConfig
        @Composable
        @ReadOnlyComposable
        get() = LocalAuraMaterialConfig.current

    val lighting: AuraLightingModel
        @Composable
        @ReadOnlyComposable
        get() = LocalAuraLighting.current

    val isReducedMotion: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalAuraReducedMotion.current
}
