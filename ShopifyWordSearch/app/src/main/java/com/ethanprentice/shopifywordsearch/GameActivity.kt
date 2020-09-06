package com.ethanprentice.shopifywordsearch

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ethanprentice.shopifywordsearch.game.GameFragment
import com.ethanprentice.shopifywordsearch.game.GameOverFragment
import com.ethanprentice.shopifywordsearch.game.GameViewModel
import com.ethanprentice.shopifywordsearch.util.BusyUiManager


class GameActivity : WSActivity(), GameFragment.GameActionListener, GameOverFragment.GameOverActionListener {

    private val busyUiManager = BusyUiManager()

    private val model: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val rootView = findViewById<FrameLayout>(R.id.main_root)
        busyUiManager.setActivity(this)
        busyUiManager.setRootView(rootView)

        if (savedInstanceState == null) {
            model.initWordSearch(busyUiManager)

            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.frag_container, GameFragment(), GameFragment.TAG)
            transaction.commit()
        }
    }

    override fun onDestroy() {
        busyUiManager.cleanup()
        super.onDestroy()
    }

    override fun getBusyUiManager(): BusyUiManager? {
        return busyUiManager
    }

    /**
     * On game over, create and show a GameOverFragment
     */
    override fun onGameOver() {
        // Check if a game over fragment already exists, if not add one
        val manager = supportFragmentManager
        val frag = manager.findFragmentByTag(GameOverFragment.TAG)
        if (frag == null) {
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.frag_container, GameOverFragment(), GameOverFragment.TAG)
            transaction.commit()
        }
    }

    /**
     * Removes the GameOverFragment and shuffles the board
     */
    override fun playAgain() {
        viewBoard()
        model.shuffleBoard(busyUiManager)
    }

    /**
     * Removes the GameOverFragment
     */
    override fun viewBoard() {
        val manager = supportFragmentManager
        val frag = manager.findFragmentByTag(GameOverFragment.TAG)
        (frag as? GameOverFragment)?.slideContentDown {
            frag.let {
                val transaction = manager.beginTransaction()
                transaction.remove(it)
                transaction.commit()
            }
        }
    }

}