package app.auraos

import app.auraos.core.permissions.AuraPermission
import app.auraos.core.permissions.AuraPermissionState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuraPermissionTest {

    @Test
    fun `wallpaper and package query are not runtime guarded`() {
        assertFalse(AuraPermission.WALLPAPER.isRuntimeProtected)
        assertFalse(AuraPermission.PACKAGE_QUERY.isRuntimeProtected)
    }

    @Test
    fun `notification listener is runtime guarded`() {
        assertTrue(AuraPermission.NOTIFICATION_LISTENER.isRuntimeProtected)
    }

    @Test
    fun `permission states distinguish granted from denied`() {
        val granted: AuraPermissionState = AuraPermissionState.Granted
        val denied: AuraPermissionState = AuraPermissionState.Denied(canRequestAgain = true)
        val permanentlyDenied: AuraPermissionState = AuraPermissionState.Denied(canRequestAgain = false)

        assertTrue(granted is AuraPermissionState.Granted)
        assertTrue(denied is AuraPermissionState.Denied && (denied as AuraPermissionState.Denied).canRequestAgain)
        assertFalse((permanentlyDenied as AuraPermissionState.Denied).canRequestAgain)
    }
}
