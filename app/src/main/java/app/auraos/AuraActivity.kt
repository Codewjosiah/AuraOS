package app.auraos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.auraos.design.theme.AuraTheme
import app.auraos.shell.state.AuraShellViewModel
import app.auraos.shell.ui.AuraShellRoot

class AuraActivity : ComponentActivity() {

    private val viewModel: AuraShellViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as AuraApplication
                val locator = app.serviceLocator
                return AuraShellViewModel(
                    appRepository = locator.appRepository,
                    wallpaperRepository = locator.wallpaperRepository,
                    batteryRepository = locator.batteryRepository,
                    performanceMonitor = locator.performanceMonitor,
                    homePreferencesRepository = locator.homePreferencesRepository,
                    widgetHostManager = locator.widgetHostManager
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val globalState by viewModel.globalState.collectAsState()

            AuraTheme(
                performanceProfile = globalState.performanceProfile,
                materialConfig = globalState.materialConfig,
                isReducedMotion = globalState.deviceCapability.isReducedMotionPreferred
            ) {
                AuraShellRoot(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }
}
