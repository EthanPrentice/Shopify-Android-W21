package com.ethanprentice.shopifywordsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : WordSearchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash screen is always dark, regardless of system theme
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES

        setContentView(R.layout.splash_layout)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, R.anim.slide_fade_out)
        }, DELAY_MS)

        return super.onCreateView(name, context, attrs)
    }

    companion object {
        const val DELAY_MS = 2500L
    }
}