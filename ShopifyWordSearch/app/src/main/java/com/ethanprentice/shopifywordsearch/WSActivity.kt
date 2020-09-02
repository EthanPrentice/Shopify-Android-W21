package com.ethanprentice.shopifywordsearch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback

open class WSActivity : AppCompatActivity(), AppCompatCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Activity", "onCreate() called for ${javaClass.name}")
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
}