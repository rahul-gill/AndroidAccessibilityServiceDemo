package wow.app.accessibilityservicedemo.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

internal class OverlayViewService : Service() {
    private val overlayViewManager by lazy {
        OverlayViewManager(this)
    }

    override fun onCreate() {
        super.onCreate()
        overlayViewManager.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayViewManager.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        throw RuntimeException("bound mode not supported")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when {
            intent == null -> return START_NOT_STICKY
            intent.hasExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY) -> overlayViewManager.showOverlay()
            intent.hasExtra(INTENT_EXTRA_COMMAND_HIDE_OVERLAY) -> overlayViewManager.hideOverlay()
        }
        return START_NOT_STICKY
    }


    companion object {
        private const val INTENT_EXTRA_COMMAND_SHOW_OVERLAY = "INTENT_EXTRA_COMMAND_SHOW_OVERLAY"
        private const val INTENT_EXTRA_COMMAND_HIDE_OVERLAY = "INTENT_EXTRA_COMMAND_HIDE_OVERLAY"

        // return false if overlay permission is not granted
        internal fun showOverlay(context: Context): Boolean {
            if (!isOverlayPermissionGranted(context)) {
                return false
            }
            val intent = Intent(context, OverlayViewService::class.java)
            intent.putExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY, true)
            context.startService(intent)
            return true
        }

        internal fun hideOverlay(context: Context) {
            val intent = Intent(context, OverlayViewService::class.java)
            intent.putExtra(INTENT_EXTRA_COMMAND_HIDE_OVERLAY, true)
            context.startService(intent)
        }
    }
}