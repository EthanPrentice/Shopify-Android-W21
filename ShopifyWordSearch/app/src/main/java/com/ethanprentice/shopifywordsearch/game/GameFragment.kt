package com.ethanprentice.shopifywordsearch.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import com.ethanprentice.shopifywordsearch.game.views.BoardView
import com.ethanprentice.shopifywordsearch.views.WSButton
import com.ethanprentice.shopifywordsearch.game.views.WordListView


class GameFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardView = view.findViewById(R.id.board_view) as BoardView
        val wordListView = view.findViewById(R.id.word_grid) as WordListView

        val model: GameViewModel by viewModels()

        model.wordSearch.observe(requireActivity(), Observer<WordSearch>{ wordSearch ->
            boardView.setWordSearch(wordSearch)
            wordListView.setWords(wordSearch.getWords())
        })

        val shuffleBtn = view.findViewById(R.id.shuffle_btn) as WSButton
        shuffleBtn.setOnClickListener {
            model.shuffleBoard()
        }
    }
}