package com.ethanprentice.shopifywordsearch

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ethanprentice.shopifywordsearch.game.GameFragment


class MainActivity : WSActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if (savedInstanceState == null) {
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.frag_container, GameFragment())
            transaction.commit()
        }
    }

}