package app.auraos.core.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Contextual permission manager that guards against premature permission prompts
 * and handles progressive disclosure.
 */
class AuraPermissionManager(private val context: Context) {

    private val _permissionStates = MutableStateFlow<Map<AuraPermission, AuraPermissionState>>(emptyMap())
    val permissionStates: StateFlow<Map<AuraPermission, AuraPermissionState>> = _permissionStates.asStateFlow()

    init {
        refreshAll()
    }

    fun checkPermission(permission: AuraPermission): AuraPermissionState {
        val permString = permission.androidPermission
        if (permString == null) {
            return AuraPermissionState.Granted
        }
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permString
        ) == PackageManager.PERMISSION_GRANTED

        return if (isGranted) {
            AuraPermissionState.Granted
        } else {
            val currentState = _permissionStates.value[permission]
            currentState ?: AuraPermissionState.NotRequested
        }
    }

    fun updatePermissionResult(permission: AuraPermission, isGranted: Boolean, canRequestAgain: Boolean) {
        val newState = if (isGranted) {
            AuraPermissionState.Granted
        } else {
            AuraPermissionState.Denied(canRequestAgain)
        }
        val updated = _permissionStates.value.toMutableMap()
        updated[permission] = newState
        _permissionStates.value = updated
    }

    fun refreshAll() {
        val updated = mutableMapOf<AuraPermission, AuraPermissionState>()
        for (perm in AuraPermission.entries) {
            updated[perm] = checkPermission(perm)
        }
        _permissionStates.value = updated
    }
}
