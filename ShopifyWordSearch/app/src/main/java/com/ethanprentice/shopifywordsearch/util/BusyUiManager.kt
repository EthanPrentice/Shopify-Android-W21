package com.ethanprentice.shopifywordsearch.util

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.WSActivity
import kotlin.concurrent.thread

class BusyUiManager {

    private var timeShown: Long = 0
    private val hideHandler = Handler()

    private var isShowing = false

    private var rootView: ViewGroup? = null
    private var activity: WSActivity? = null


    /**
     * Shows the [ProgressBar] if it is not already visible
     */
    fun show() {
        if (isShowing) {
            Log.i(TAG, "The progress bar is already being shown")
            return
        }

        rootView?.let {
            val inflater = it.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.progress_overlay, it, true)

            timeShown = System.currentTimeMillis()
            isShowing = true
        }
    }

    /**
     * Hides the [ProgressBar] if it is visible
     * If the [ProgressBar] has been visible for less than [MIN_SHOW_TIME_MS] we wait and hide it once the time requirement is met
     */
    fun hide() {
        if (!isShowing) {
            Log.i(TAG, "Cannot hide progress before it is shown")
            return
        }

        val currTime = System.currentTimeMillis()
        if (currTime - timeShown < MIN_SHOW_TIME_MS) {
            hideHandler.postDelayed({
                hide()
            }, MIN_SHOW_TIME_MS - (currTime - timeShown))
        }

        val progressView = rootView?.findViewById(R.id.progress_overlay) as? FrameLayout
        progressView ?: return

        rootView?.removeView(progressView)
        isShowing = false
    }

    /**
     * Shows the [ProgressBar] if [breakCondition] is not met after [MIN_WAIT_MS] milliseconds
     * Hides the [ProgressBar] after [breakCondition] is met if it was shown, and invokes [finishedCallback]
     */
    fun showUntilCondition(breakCondition: () -> Boolean, finishedCallback: () -> Any?) {
        thread(start=true) {
            val startTime = System.currentTimeMillis()
            while (!breakCondition()) {
                if (!isShowing) {
                    if (System.currentTimeMillis() - startTime > MIN_WAIT_MS) {
                        activity?.runOnUiThread {
                            show()
                        }
                    }
                }
                Thread.sleep(50)
            }

            activity?.runOnUiThread {
                hide()
            }

            finishedCallback()
        }
    }

    fun cleanup() {
        rootView = null
        activity = null
        hideHandler.removeCallbacksAndMessages(null)
    }

    fun setActivity(newActivity: WSActivity) {
        activity = newActivity
    }

    fun setRootView(v: ViewGroup) {
        rootView = v
    }

    companion object {
        const val TAG = "BusyUiManager"
        const val MIN_SHOW_TIME_MS = 500L // Minimum time the progress bar will show to prevent flickering
        const val MIN_WAIT_MS = 200L // Time until the progress bar will show after waiting
    }

}