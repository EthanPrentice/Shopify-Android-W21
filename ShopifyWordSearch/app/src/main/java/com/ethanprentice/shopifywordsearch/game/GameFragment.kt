package com.ethanprentice.shopifywordsearch.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.WSActivity
import com.ethanprentice.shopifywordsearch.game.board.BoardCoords
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.Word
import com.ethanprentice.shopifywordsearch.game.board.WordAngle
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

    var toast: Toast? = null

    private var activeLine: BoardLineView? = null
    private var lineViews = mutableListOf<BoardLineView>()

    private val model: GameViewModel by activityViewModels()

    private val actionListener
        get() = (activity as? GameActionListener)

    private val wsActivity: WSActivity?
        get() = (activity as? WSActivity)


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

        // update boardView and wordListView if wordSearch is updated
        model.wordSearch.observe(requireActivity(), Observer<WordSearch>{ wordSearch ->
            wordSearch ?: return@Observer
            boardView.setWordSearch(wordSearch)
            wordListView.setWords(wordSearch.getWords())
        })

        // Set the counter to foundWords.size and update wordListView whenever foundWords is updated
        model.foundWords.observe(requireActivity(), Observer<Set<Word>> { foundWords ->
            if (model.isWordSearchInitialized()) {
                val maxWords = model.wordSearch.value?.getWords()?.size ?: 0
                scoreView.text = getString(R.string.score_text, foundWords.size, maxWords)
                wordListView.setWordsAsFound(foundWords)

                if (foundWords.size == maxWords) {
                    actionListener?.onGameOver()
                }
            }
        })

        val shuffleBtn = view.findViewById(R.id.shuffle_btn) as WSButton
        shuffleBtn.setOnClickListener {
            wsActivity?.let {
                val busyUiManager = it.getBusyUiManager() ?: return@let
                model.shuffleBoard(busyUiManager)
            }
        }

        boardView.setOnTouchListener { _, event ->
            onBoardTouch(event)
            true
        }

        model.boardLines.observe(requireActivity(), Observer<Set<BoardLine>> { boardLines ->
            // Check for deletions
            val iter = lineViews.iterator()
            while(iter.hasNext()){
                val lineView = iter.next()
                if (!boardLines.contains(lineView.boardLine)) {
                    iter.remove()
                    root.removeView(lineView)
                }
            }

            // Check for additions
            val existingBoardLines = lineViews.map { it.boardLine }
            boardLines.forEach { boardLine ->
                if (!existingBoardLines.contains(boardLine)) {
                    val newView = BoardLineView(requireContext(), boardView, boardLine)
                    root.addView(newView)
                    lineViews.add(newView)
                }
            }
        })
    }

    override fun onPause() {
        toast?.cancel()
        super.onPause()
    }

    /**
     * Draws a line from where the user first touches to their fingers location
     * This line will persist if when the finger is off the screen it covers a word
     */
    private fun onBoardTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> handleActionDown(event)
            MotionEvent.ACTION_MOVE -> handleActionMove(event)

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_OUTSIDE -> handleActionUp()
        }
    }

    /**
     * Assigns a new active [BoardLineView] to [activeLine]
     */
    private fun handleActionDown(event: MotionEvent) {
        val boardCoords = boardView.coordsToBoardCoords(event.x.toInt(), event.y.toInt()) ?: return
        val boardLine = BoardLine(boardCoords, boardCoords, BoardLine.Status.ACTIVE)

        activeLine = BoardLineView(requireContext(), boardView, boardLine)
        root.addView(activeLine)
    }

    /**
     * Assigns [event]'s corresponding [BoardCoords] to [boardView.endCoords] so the line follows the [MotionEvent]
     */
    private fun handleActionMove(event: MotionEvent) {
        val boardCoords = boardView.coordsToBoardCoords(event.x.toInt(), event.y.toInt()) ?: return
        activeLine?.apply {
            boardLine.endCoords = boardCoords
            invalidate()
        }
    }

    /**
     * Checks whether the line is over a word or not and handles it accordingly
     * There are three states that we handle for:
     *   The line is not over a valid word
     *   The line is over a valid word that has already been found
     *   The line is over a valid word that has not been found
     */
    private fun handleActionUp() {
        activeLine?.let { line ->
            val foundWord = lineFoundWord(line)
            if (foundWord != null) {
                // Word has already been found, set the correct status and play the word already found animation
                // Also show a toast to clearly remind the user this word has already been found
                // We mainly handle for this in the rare case where random generation creates a duplicate of a valid word
                val alreadyFound = model.isWordFound(foundWord)
                if (alreadyFound) {
                    line.boardLine.status = BoardLine.Status.DUPLICATE
                    showAlreadyFoundToast(foundWord)
                    line.runExitAnimation { // cleanup callback
                        root.removeView(line)
                    }
                }
                else {
                    // If the word has not been found yet, set the status to found and add the line to the model
                    // We remove the view, but it will be re-added in the view model observer
                    model.addFoundWord(foundWord)
                    line.boardLine.status = BoardLine.Status.FOUND
                    model.addBoardLine(line.boardLine)
                    root.removeView(line)
                }
            }
            else {
                line.boardLine.status = BoardLine.Status.NOT_FOUND

                // Note we do not add lines to the view model if they will disappear soon anyways
                lineViews.add(line)
                line.runExitAnimation { // cleanup callback
                    lineViews.remove(line)
                    root.removeView(line)
                }
            }
        }
        activeLine = null
    }

    /**
     * @return the word line is over, if there is no such word return null
     */
    private fun lineFoundWord(lineView: BoardLineView): Word? {
        // Check if it is in the current word list
        val words = model.wordSearch.value?.getWords() ?: return null
        words.forEach { word ->
            if (word.startingCoords == lineView.boardLine.startCoords && word.endCoords == lineView.boardLine.endCoords ||
                word.startingCoords == lineView.boardLine.endCoords && word.endCoords == lineView.boardLine.startCoords) {
                return@lineFoundWord word
            }
        }

        // check in-case of duplicate words when randomly generating boards
        val lineString = model.wordSearch.value?.getString(lineView.boardLine) ?: return null
        val lineStringReversed = lineString.reversed()

        words.forEach { word ->
            if (word.formattedString == lineString) {
                word.startingCoords = lineView.boardLine.startCoords
                word.angle = WordAngle.getAngle(lineView.boardLine)!!
                return@lineFoundWord word
            }
            else if (word.formattedString == lineStringReversed) {
                word.startingCoords = lineView.boardLine.endCoords
                word.angle = WordAngle.getAngle(lineView.boardLine.endCoords, lineView.boardLine.startCoords)!!
                return@lineFoundWord word
            }
        }

        return null
    }

    private fun showAlreadyFoundToast(word: Word) {
        toast?.cancel()
        val str = "${word.string} has already been found!"
        toast = Toast.makeText(activity, str, Toast.LENGTH_SHORT)
        toast?.show()
    }

    interface GameActionListener {
        fun onGameOver()
    }

    companion object {
        const val TAG = "GameFragment"
    }
}