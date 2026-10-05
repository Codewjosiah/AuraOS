package app.auraos

import android.app.Application
import app.auraos.core.system.SystemServiceLocator

class AuraApplication : Application() {

    lateinit var serviceLocator: SystemServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        serviceLocator = SystemServiceLocator(this)
    }
}
