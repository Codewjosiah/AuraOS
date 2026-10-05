package app.auraos.core.system

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Wallpaper state representation for liquid glass refraction and contrast adaptation.
 */
data class AuraWallpaperState(
    val isDarkWallpaper: Boolean = true,
    val dominantLuminance: Float = 0.15f,
    val primaryColorArgb: Int = Color.BLACK,
    val secondaryColorArgb: Int = Color.DKGRAY,
    val hasLiveWallpaper: Boolean = false,
    val cachedBitmap: Bitmap? = null
)

interface WallpaperRepository {
    val wallpaperState: StateFlow<AuraWallpaperState>
    suspend fun refreshWallpaper()
}

class AndroidWallpaperRepository(
    private val context: Context
) : WallpaperRepository {

    private val _wallpaperState = MutableStateFlow(AuraWallpaperState())
    override val wallpaperState: StateFlow<AuraWallpaperState> = _wallpaperState.asStateFlow()

    private val wallpaperManager = WallpaperManager.getInstance(context)

    override suspend fun refreshWallpaper() {
        withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    val colors: WallpaperColors? = try {
                        wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                    } catch (_: Exception) {
                        null
                    }

                    if (colors != null) {
                        val primary = colors.primaryColor.toArgb()
                        val secondary = colors.secondaryColor?.toArgb() ?: Color.DKGRAY
                        val isDark = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            (colors.colorHints and WallpaperColors.HINT_SUPPORTS_DARK_TEXT) == 0
                        } else {
                            calculateLuminance(primary) < 0.5f
                        }
                        val luminance = calculateLuminance(primary)

                        _wallpaperState.value = AuraWallpaperState(
                            isDarkWallpaper = isDark,
                            dominantLuminance = luminance,
                            primaryColorArgb = primary,
                            secondaryColorArgb = secondary,
                            hasLiveWallpaper = false
                        )
                        return@withContext
                    }
                }

                // Fallback for earlier APIs or when colors are unavailable
                val drawable = try {
                    wallpaperManager.drawable
                } catch (_: Exception) {
                    null
                }

                if (drawable is BitmapDrawable && drawable.bitmap != null) {
                    val bmp = drawable.bitmap
                    val sample = bmp.getPixel(bmp.width / 2, bmp.height / 2)
                    val lum = calculateLuminance(sample)
                    _wallpaperState.value = AuraWallpaperState(
                        isDarkWallpaper = lum < 0.5f,
                        dominantLuminance = lum,
                        primaryColorArgb = sample,
                        secondaryColorArgb = Color.DKGRAY
                    )
                }
            } catch (_: Exception) {
                // Safe default: dark wallpaper
                _wallpaperState.value = AuraWallpaperState(
                    isDarkWallpaper = true,
                    dominantLuminance = 0.15f
                )
            }
        }
    }

    private fun calculateLuminance(color: Int): Float {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f
        return (0.299f * r + 0.587f * g + 0.114f * b)
    }
}
