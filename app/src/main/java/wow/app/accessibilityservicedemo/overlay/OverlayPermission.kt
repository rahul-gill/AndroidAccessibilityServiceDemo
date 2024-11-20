package wow.app.accessibilityservicedemo.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import wow.app.accessibilityservicedemo.common.addHighlightEntryExtras


fun isOverlayPermissionGranted(context: Context) = Settings.canDrawOverlays(context)

fun requestOverlayPermission(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    intent.addHighlightEntryExtras(context.packageName)
    context.startActivity(intent)
}