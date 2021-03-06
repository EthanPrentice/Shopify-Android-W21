package com.ethanprentice.shopifywordsearch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.ethanprentice.shopifywordsearch.util.BusyUiManager

class SplashActivity : WSActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash screen is always dark, regardless of system theme
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES

        setContentView(R.layout.splash_layout)

        handler.postDelayed({
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, R.anim.slide_fade_out)
        }, DELAY_MS)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun getBusyUiManager(): BusyUiManager? {
        return null
    }

    companion object {
        const val DELAY_MS = 2500L
    }
}