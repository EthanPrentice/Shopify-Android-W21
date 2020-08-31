package com.ethanprentice.myapplication

import android.os.Bundle

class MainActivity : WordSearchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
    }

}