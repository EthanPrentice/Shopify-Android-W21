package com.ethanprentice.shopifywordsearch

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.core.content.ContextCompat
import com.ethanprentice.shopifywordsearch.util.BusyUiManager

abstract class WSActivity : AppCompatActivity(), AppCompatCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Activity", "onCreate() called for ${javaClass.name}")
        updateStatusBarIconColor()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Activity", "onResume() called for ${javaClass.name}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Activity", "onPause() called for ${javaClass.name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Activity", "onDestroy() called for ${javaClass.name}")
    }

    abstract fun getBusyUiManager(): BusyUiManager?

    protected fun isDarkMode(): Boolean {
        val uiModeMasked = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiModeMasked == Configuration.UI_MODE_NIGHT_YES
    }

    protected fun updateStatusBarIconColor() {
        if (isDarkMode()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun setStatusBarColor(resId: Int) {
        val color = ContextCompat.getColor(this, resId)
        window?.statusBarColor = color
    }

    fun clearStatusBarColor() {
        val color = ContextCompat.getColor(this, android.R.color.transparent)
        window?.statusBarColor = color
    }
}