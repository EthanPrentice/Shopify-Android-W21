package com.ethanprentice.shopifywordsearch.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.Word
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import com.ethanprentice.shopifywordsearch.game.views.BoardLineView
import com.ethanprentice.shopifywordsearch.game.views.BoardView
import com.ethanprentice.shopifywordsearch.game.views.WordListView
import com.ethanprentice.shopifywordsearch.views.WSButton


class GameFragment : Fragment() {

    lateinit var root: ConstraintLayout
    lateinit var boardView: BoardView
    lateinit var wordListView: WordListView
    lateinit var scoreView: TextView

    private var activeLine: BoardLineView? = null
    private var lineViews = mutableListOf<BoardLineView>()

    private val model: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.game_layout, container, false) as ConstraintLayout
        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardView = view.findViewById(R.id.board_view) as BoardView
        wordListView = view.findViewById(R.id.word_grid) as WordListView
        scoreView = view.findViewById(R.id.score_view) as TextView

        model.wordSearch.observe(requireActivity(), Observer<WordSearch>{ wordSearch ->
            boardView.setWordSearch(wordSearch)
            wordListView.setWords(wordSearch.getWords())
        })

        model.foundWords.observe(requireActivity(), Observer<Set<Word>> { foundWords ->
            val maxWords = model.wordSearch.value?.getWords()?.size ?: 0
            scoreView.text = getString(R.string.score_text, foundWords.size, maxWords)
            wordListView.setWordsAsFound(foundWords)

            if (foundWords.size == maxWords) {
                showGameOverDialog()
            }
        })

        val shuffleBtn = view.findViewById(R.id.shuffle_btn) as WSButton
        shuffleBtn.setOnClickListener {
            model.shuffleBoard()
        }

        boardView.setOnTouchListener { _, event ->
            onBoardTouch(event)
            true
        }

        model.boardLines.observe(requireActivity(), Observer<Set<BoardLine>> { boardLines ->
            // Check for deletions
            lineViews.forEach { lineView ->
                if (!boardLines.contains(lineView.boardLine)) {
                    root.removeView(lineView)
                }
            }

            // Check for additions
            val existingBoardLines = lineViews.map { it.boardLine }
            boardLines.forEach { boardLine ->
                if (!existingBoardLines.contains(boardLine)) {
                    val newView = BoardLineView(requireContext(), boardView, boardLine)
                    lineViews.add(newView)
                    root.addView(newView)
                }
            }
        })
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
                    val foundWord = lineFoundWord(line)
                    if (foundWord != null) {
                        model.addFoundWord(foundWord)
                        line.boardLine.type = BoardLine.Status.FOUND

                        model.addBoardLine(line.boardLine)
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
                activeLine = null
            }
        }
    }

    /**
     * @return the word line is over, if there is no such word return null
     */
    private fun lineFoundWord(lineView: BoardLineView): Word? {
        val words = model.wordSearch.value?.getWords() ?: return null
        words.forEach { word ->
            if (word.startingCoords == lineView.boardLine.startCoords && word.lastCoords == lineView.boardLine.endCoords ||
                word.startingCoords == lineView.boardLine.endCoords && word.lastCoords == lineView.boardLine.startCoords) {
                return word
            }
        }
        return null
    }

    private fun showGameOverDialog() {
        val manager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.frag_container, GameOverFragment(), GameOverFragment.TAG)
        transaction.commit()
    }

    companion object {
        const val TAG = "GameFragment"
    }
}