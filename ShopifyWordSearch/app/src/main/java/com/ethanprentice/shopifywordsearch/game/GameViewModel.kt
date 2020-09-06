package com.ethanprentice.shopifywordsearch.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.Word
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import com.ethanprentice.shopifywordsearch.util.BusyUiManager
import java.util.*
import kotlin.concurrent.thread

class GameViewModel: ViewModel() {

    // WORD SEARCH
    private val _wordSearch = MutableLiveData<WordSearch>()
    val wordSearch: LiveData<WordSearch> = _wordSearch

    fun initWordSearch(busyUiManager: BusyUiManager) {
        var ws: WordSearch? = null
        busyUiManager.showUntilCondition({
            ws != null
        }) {
            _wordSearch.postValue(ws)
        }
        thread(start=true) {
            ws = WordSearch()
        }
    }

    fun shuffleBoard(busyUiManager: BusyUiManager) {
        val preShuffleCount = wordSearch.value?.shuffleCount ?: return
        busyUiManager.showUntilCondition({
            preShuffleCount != wordSearch.value?.shuffleCount
        }) {
            _wordSearch.postValue(_wordSearch.value)

            clearBoardLines()
            clearFoundWords()
        }
        thread(start=true) {
            _wordSearch.value?.shuffleBoard()
        }
    }

    fun isWordSearchInitialized(): Boolean {
        return wordSearch.value != null
    }
    // END OF WORD SEARCH

    // FOUND WORDS
    private val foundWordsSet = TreeSet<Word>{ w1, w2 -> w1.string.compareTo(w2.string) }
    private val _foundWords = MutableLiveData<Set<Word>>().apply {
        postValue(foundWordsSet)
    }
    val foundWords: LiveData<Set<Word>> = _foundWords

    fun addFoundWord(word: Word) {
        foundWordsSet.add(word)
         _foundWords.postValue(foundWordsSet)
    }

    fun clearFoundWords() {
        foundWordsSet.clear()
        _foundWords.postValue(foundWordsSet)
    }

    fun isWordFound(word: Word): Boolean {
        return foundWordsSet.contains(word)
    }
    // END OF FOUND WORDS

    // BOARD LINES
    private val boardLinesList = mutableSetOf<BoardLine>()
    private val _boardLines = MutableLiveData<Set<BoardLine>>().apply {
        value = boardLinesList
    }
    val boardLines: LiveData<Set<BoardLine>> = _boardLines

    fun addBoardLine(line: BoardLine) {
        boardLinesList.add(line)
        _boardLines.postValue(boardLinesList)
    }

    fun removeBoardLine(line: BoardLine) {
        boardLinesList.remove(line)
        _boardLines.postValue(boardLinesList)
    }

    fun clearBoardLines() {
        boardLinesList.clear()
        _boardLines.postValue(boardLinesList)
    }
    // END OF BOARD LINES
}