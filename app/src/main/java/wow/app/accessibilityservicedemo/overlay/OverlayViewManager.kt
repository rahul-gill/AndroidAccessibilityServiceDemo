package wow.app.accessibilityservicedemo.overlay

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import wow.app.accessibilityservicedemo.R
import wow.app.accessibilityservicedemo.common.getAnimationScale
import wow.app.accessibilityservicedemo.common.getScreenHeight
import wow.app.accessibilityservicedemo.common.getScreenWidth


class OverlayViewManager(
    private val context: Context
) {
    private val lifecycleOwner = object : LifecycleOwner, SavedStateRegistryOwner {
        val registry = LifecycleRegistry(this)
        val registryController = SavedStateRegistryController.create(this)
        override val savedStateRegistry: SavedStateRegistry =
            registryController.savedStateRegistry
        override val lifecycle: Lifecycle = registry
    }
    private val windowManager by lazy {
        ContextCompat.getSystemService(context, WindowManager::class.java)
            ?: throw IllegalStateException("Can't create window manager")
    }
    private var overlayView: View? = null
    private val overlayViewParams = buildLayoutParams()
    private var gestureEnabled = true

    fun showOverlay() {
        if (overlayView != null) {
            return
        }
        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        overlayView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                val hapticFeedback = LocalHapticFeedback.current
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .background(color = Color.Red, shape = CircleShape)
                        .clip(CircleShape)
                        .pointerInput(Unit) {
                            if (gestureEnabled) {
                                detectDragGestures(
                                    onDragStart = {
                                        println("3242 dragInProgress = true")
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragEnd = {
                                        snapViewToSide()
                                        println("3242 dragInProgress = false")
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragCancel = {
                                        snapViewToSide()
                                        println("3242 dragInProgress = false")
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        updateViewParams {
                                            x += dragAmount.x.toInt()
                                            y += dragAmount.y.toInt()
                                        }

                                    })
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(56.dp),
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null
                    )
                }
            }
        }
        val exclusionRect = Rect(
            0, 0, getScreenWidth(windowManager), getScreenHeight(windowManager)
        )
        overlayView!!.systemGestureExclusionRects = listOf(exclusionRect)
        windowManager.addView(overlayView, overlayViewParams)
    }

    private fun snapViewToSide() {
        gestureEnabled = false
        val currentX = overlayViewParams.x
        val screenWidth = getScreenWidth(windowManager)
        val finalX =
            if (currentX * 2 < screenWidth - overlayView!!.width) 0
            else screenWidth - overlayView!!.width
        val animator = ValueAnimator.ofInt(currentX, finalX)
        val durationScale = getAnimationScale(context)
        animator.duration = (300 * durationScale).toLong()
        animator.addUpdateListener { anim ->
            updateViewParams {
                x = (anim.animatedValue as Int)
            }
        }
        val finally = { _: Animator ->
            gestureEnabled = true
            updateViewParams { x = finalX }
        }
        animator.doOnEnd(finally)
        animator.doOnCancel(finally)
        animator.start()
    }

    fun hideOverlay() {
        if (overlayView == null) {
            return
        }
        windowManager.removeView(overlayView)
        overlayView = null

        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun onCreate() {
        lifecycleOwner.registryController.performAttach()
        lifecycleOwner.registryController.performRestore(null)
        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onDestroy() {
        hideOverlay()
        lifecycleOwner.registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun updateViewParams(updates: WindowManager.LayoutParams.() -> Unit) {
        overlayViewParams.updates()
        windowManager.updateViewLayout(overlayView, overlayViewParams)
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = getScreenHeight(windowManager) / 4

        return params
    }

}