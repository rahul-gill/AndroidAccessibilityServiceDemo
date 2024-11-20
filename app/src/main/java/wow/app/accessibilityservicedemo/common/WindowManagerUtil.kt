package wow.app.accessibilityservicedemo.common

import android.view.WindowManager

fun getScreenWidth(windowManager: WindowManager) =
    windowManager.maximumWindowMetrics.bounds.width()


fun getScreenHeight(windowManager: WindowManager) =
    windowManager.maximumWindowMetrics.bounds.height()
