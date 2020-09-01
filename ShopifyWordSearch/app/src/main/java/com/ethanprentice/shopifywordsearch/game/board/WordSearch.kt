package com.ethanprentice.shopifywordsearch.game.board

import android.util.Log
import kotlin.collections.ArrayList

class WordSearch {

    // used to limit object creation when there's few possible ways to create a board
    private val tmpBoards = Array(WORD_LIST.size) {
        Array(BOARD_SIZE) {
            CharArray(BOARD_SIZE) {
                EMPTY_CHAR
            }
        }
    }

    // 2D Char array
    val board: Array<CharArray> = Array(BOARD_SIZE) {
        CharArray(BOARD_SIZE) {
            EMPTY_CHAR
        }
    }

    private val words: MutableList<Word> = ArrayList()

    init {
        generateBoard()
    }

    private fun generateBoard() {
        val wordList = WORD_LIST.shuffled()

        val placedWords = placeWord(board, wordList, 0)
        if (placedWords) {
            Log.i(TAG, "Successfully generated word search")
            fillEmptyTiles()

            Log.i(TAG, "Board: ")
            board.forEach {
                Log.i(TAG, it.contentToString())
            }
            Log.i(TAG, "Words: ")
            words.forEach {
                Log.i(TAG, it.toString())
            }
        }
        else {
            Log.e(TAG, "Word search could not be generated!!")
        }
    }

    private fun fillEmptyTiles() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (board[i][j] == EMPTY_CHAR) {
                    board[i][j] = ALPHABET.random()
                }
            }
        }
    }

    /**
     * @return all coordinates that the word could possible start on and still fit on the board
     */
    private fun getPossibleCoords(word: String, angle: WordAngle): List<BoardCoords> {
        fun getRange(direction: Int) =  when(direction) {
            1 -> 0..(BOARD_SIZE - word.length)
            0 -> 0 until BOARD_SIZE
            -1 -> (word.length - 1) until BOARD_SIZE
            else -> IntRange.EMPTY
        }

        val coords = ArrayList<BoardCoords>()
        for (x in getRange(angle.getXDelta())) {
            for (y in getRange(angle.getYDelta())) {
                coords.add(BoardCoords(x, y))
            }
        }

        return coords
    }

    /**
     * Checks whether a word can be placed in it's current state
     * @returns true if placement was successful, false otherwise
     */
    private fun canPlaceWord(board: Array<CharArray>, word: Word): Boolean {
        val currentCoords = BoardCoords(word.startingCoords.x, word.startingCoords.y)

        for (i in 0 until word.length) {
            val tile = board[currentCoords.y][currentCoords.x]
            if (!(tile == EMPTY_CHAR || tile == word[i])) {
                return false
            }

            currentCoords.x += word.angle.getXDelta()
            currentCoords.y += word.angle.getYDelta()
        }

        return true
    }

    /**
     * Recursively places words with i >= wordIndex in wordList onto the passed in board if possible
     *
     * @returns whether all words at i >= wordIndex can be placed on the passed in board
     */
    private fun placeWord(board: Array<CharArray>, wordList: List<String>, wordIndex: Int): Boolean {
        val wordString = wordList[wordIndex]

        // go over angles in randomized order
        WordAngle.leftToRightValues().toList().shuffled().forEach { angle ->
            val possibleCoords = getPossibleCoords(wordString, angle).shuffled()
            possibleCoords.forEach { coords ->
                val word = Word(wordString, angle, coords)

                // create a copy of the board at this recursive level to pass down to the next words
                val tmpBoard = tmpBoards[wordIndex]
                copy2DArray(board, tmpBoard)

                if (canPlaceWord(tmpBoard, word)) {
                    // if the word can be placed on the temp board, place it
                    val currentCoords = BoardCoords(word.startingCoords.x, word.startingCoords.y)
                    for (i in 0 until word.length) {
                        tmpBoard[currentCoords.y][currentCoords.x] = word[i]

                        currentCoords.x += word.angle.getXDelta()
                        currentCoords.y += word.angle.getYDelta()
                    }

                    // if this word is successfully placed and all of the successor words can be placed with this word in it's location, add it to the board
                    // then bubble up by returning success
                    if (wordIndex == wordList.size - 1 || placeWord(tmpBoard, wordList, wordIndex + 1)) {
                        currentCoords.x = word.startingCoords.x
                        currentCoords.y = word.startingCoords.y

                        for (i in 0 until word.length) {
                            this.board[currentCoords.y][currentCoords.x] = word[i]

                            currentCoords.x += word.angle.getXDelta()
                            currentCoords.y += word.angle.getYDelta()
                        }
                        words.add(word)

                        return true
                    }
                }
            }
        }

        // no possible placements found for this level
        // bubble up to fix placements at higher levels so this word can be placed
        return false
    }

    fun getWords(): List<Word> {
        return words
    }

    companion object {
        private const val TAG = "WordSearch"

        private val ALPHABET = CharArray(26) { 'a' + it }

        private val WORD_LIST = listOf(
            "swift", "kotlin", "objectivec",
            "variable", "java", "mobile", "shopify",
            "ecommerce", "waterloo", "android"
        )

        private val WORD_LIST_INTENSIVE = listOf( // Debug Only
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee",
            "ffffffffff",
            "gggggggggg",
            "hhhhhhhhhh",
            "iiiiiiiiii",
            "jjjjjjjjjj",
            "abcdef"
        )

        private const val BOARD_SIZE = 10
        private const val EMPTY_CHAR = '*'

        private fun copy2DArray(src: Array<CharArray>, dst: Array<CharArray>) {
            for (i in src.indices) {
                for (j in src[i].indices) {
                    dst[i][j] = src[i][j]
                }
            }
        }
    }
}