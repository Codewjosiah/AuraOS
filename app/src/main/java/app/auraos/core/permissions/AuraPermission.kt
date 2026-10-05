package app.auraos.core.permissions

/**
 * High-level conceptual permissions for AURA OS capabilities.
 */
enum class AuraPermission(
    val androidPermission: String?,
    val title: String,
    val description: String,
    val isCriticalForBoot: Boolean = false
) {
    WALLPAPER(
        androidPermission = null, // Accessed via WallpaperManager without runtime prompt
        title = "Wallpaper Dynamics",
        description = "Allows AURA liquid glass to sample wallpaper luminance and contrast."
    ),
    PACKAGE_QUERY(
        androidPermission = null, // Normal install-time permission
        title = "App Discovery",
        description = "Enables AURA Home to launch installed applications and show icons."
    ),
    NOTIFICATION_LISTENER(
        androidPermission = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
        title = "Live Notifications",
        description = "Powers the AURA Live Pill and River notification stream."
    ),
    VIBRATION(
        androidPermission = "android.permission.VIBRATE",
        title = "Haptic Engine",
        description = "Provides subtle physical tactile feedback for gesture snaps."
    );

    val isRuntimeProtected: Boolean
        get() = androidPermission != null
}

sealed interface AuraPermissionState {
    data object Granted : AuraPermissionState
    data object NotRequested : AuraPermissionState
    data class Denied(val canRequestAgain: Boolean) : AuraPermissionState
    data object UnsupportedOnDevice : AuraPermissionState
}
