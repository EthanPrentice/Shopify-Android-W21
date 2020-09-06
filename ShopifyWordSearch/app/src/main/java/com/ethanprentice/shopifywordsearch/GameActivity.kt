package com.ethanprentice.shopifywordsearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ethanprentice.shopifywordsearch.game.GameFragment
import com.ethanprentice.shopifywordsearch.game.GameOverFragment
import com.ethanprentice.shopifywordsearch.game.GameViewModel


class GameActivity : WSActivity(), GameFragment.GameActionListener, GameOverFragment.GameOverActionListener {

    private val model: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if (savedInstanceState == null) {
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.frag_container, GameFragment(), GameFragment.TAG)
            transaction.commit()
        }
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
        model.shuffleBoard()
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