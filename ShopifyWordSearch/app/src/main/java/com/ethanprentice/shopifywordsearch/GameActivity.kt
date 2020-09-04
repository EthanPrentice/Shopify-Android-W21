package com.ethanprentice.shopifywordsearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ethanprentice.shopifywordsearch.game.GameFragment
import com.ethanprentice.shopifywordsearch.game.GameOverFragment
import com.ethanprentice.shopifywordsearch.game.GameViewModel


class GameActivity : WSActivity(), GameOverFragment.GameOverActionListener {

    private val model: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if (savedInstanceState == null) {
            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.frag_container, GameFragment(), "game_fragment")
            transaction.commit()
        }
    }

    override fun playAgain() {
        viewBoard()
        model.shuffleBoard()
    }

    override fun viewBoard() {
        // Remove game over fragment
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