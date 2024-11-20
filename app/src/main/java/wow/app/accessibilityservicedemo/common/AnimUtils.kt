package wow.app.accessibilityservicedemo.common

import android.content.Context
import android.provider.Settings

fun getAnimationScale(context: Context): Float = Settings.Global.getFloat(
    context.contentResolver,
    Settings.Global.ANIMATOR_DURATION_SCALE,
    0.0f
)