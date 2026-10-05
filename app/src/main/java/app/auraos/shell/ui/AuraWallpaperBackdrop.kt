package app.auraos.shell.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import app.auraos.core.system.AuraWallpaperState
import app.auraos.design.theme.AuraColors

/**
 * Backdrop layer rendering wallpaper-aware ambient depth and dark liquid substrate.
 */
@Composable
fun AuraWallpaperBackdrop(
    wallpaperState: AuraWallpaperState,
    modifier: Modifier = Modifier
) {
    // Elegant deep monochrome radial backdrop reflecting AURA's black+white signature
    val ambientGradient = Brush.radialGradient(
        colors = listOf(
            AuraColors.Obsidian,
            AuraColors.AuraBlack
        ),
        center = Offset(500f, 400f),
        radius = 1200f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ambientGradient)
    )
}
