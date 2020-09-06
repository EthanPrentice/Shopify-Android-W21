package com.ethanprentice.shopifywordsearch

import android.app.Application

class WSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: WSApplication? = null
            private set
    }
}