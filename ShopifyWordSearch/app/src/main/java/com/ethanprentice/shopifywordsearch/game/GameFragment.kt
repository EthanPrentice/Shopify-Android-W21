package com.ethanprentice.shopifywordsearch.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import com.ethanprentice.shopifywordsearch.game.views.BoardLineView
import com.ethanprentice.shopifywordsearch.game.views.BoardView
import com.ethanprentice.shopifywordsearch.game.views.WordListView
import com.ethanprentice.shopifywordsearch.views.WSButton


class GameFragment : Fragment() {

    lateinit var root: ConstraintLayout
    lateinit var boardView: BoardView
    lateinit var wordListView: WordListView

    private var activeLine: BoardLineView? = null
    private var lineViews = mutableListOf<BoardLineView>()

    private val model: GameViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.game_layout, container, false) as ConstraintLayout
        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardView = view.findViewById(R.id.board_view) as BoardView
        wordListView = view.findViewById(R.id.word_grid) as WordListView

        model.wordSearch.observe(requireActivity(), Observer<WordSearch>{ wordSearch ->
            boardView.setWordSearch(wordSearch)
            wordListView.setWords(wordSearch.getWords())
        })

        val shuffleBtn = view.findViewById(R.id.shuffle_btn) as WSButton
        shuffleBtn.setOnClickListener {
            model.shuffleBoard()
            clearLines()
        }

        boardView.setOnTouchListener { _, event ->
            onBoardTouch(event)
            true
        }

        model.boardLines.forEach { line ->
            val lineView = BoardLineView(requireContext(), boardView, line)
            lineViews.add(lineView)
            root.addView(lineView)
        }
    }

    /**
     * Draws a line from where the user first touches to their fingers location
     * This line will persist if when the finger is off the screen it covers a word
     */
    private fun onBoardTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val boardCoords = boardView.coordsToBoardCoords(event.x.toInt(), event.y.toInt()) ?: return
                val boardLine = BoardLine(boardCoords, boardCoords, BoardLine.Status.ACTIVE)

                activeLine = BoardLineView(requireContext(), boardView, boardLine)
                root.addView(activeLine)
            }
            MotionEvent.ACTION_MOVE -> {
                val boardCoords = boardView.coordsToBoardCoords(event.x.toInt(), event.y.toInt()) ?: return
                activeLine?.apply {
                    boardLine.endCoords = boardCoords
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_OUTSIDE -> {
                activeLine?.let { line ->
                    if (lineFoundWord(line)) {
                        line.boardLine.type = BoardLine.Status.FOUND

                        model.boardLines.add(line.boardLine)
                        lineViews.add(line)

                        line.wordFound()
                    }
                    else {
                        line.boardLine.type = BoardLine.Status.NOT_FOUND

                        // Note we do not add lines to the view model if they will disappear soon anyways
                        lineViews.add(line)

                        line.wordNotFound { // onAnimationEnd callback
                            lineViews.remove(line)
                            root.removeView(line)
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes all lines from the board and view model
     */
    private fun clearLines() {
        lineViews.forEach { line ->
            model.boardLines.remove(line.boardLine)
            root.removeView(line)
        }
        lineViews.clear()
    }

    /**
     * @return whether [lineView] contains a word from the word list
     */
    private fun lineFoundWord(lineView: BoardLineView): Boolean {
        val words = model.wordSearch.value?.getWords() ?: return false
        words.forEach { word ->
            if (word.startingCoords == lineView.boardLine.startCoords && word.lastCoords == lineView.boardLine.endCoords) {
                return true
            }
        }
        return false
    }
}