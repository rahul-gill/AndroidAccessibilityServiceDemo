package wow.app.accessibilityservicedemo.common

import android.app.Application

object ApplicationSingletonDependencies {
    lateinit var application: Application

    fun init(app: Application) {
        application = app
    }
}