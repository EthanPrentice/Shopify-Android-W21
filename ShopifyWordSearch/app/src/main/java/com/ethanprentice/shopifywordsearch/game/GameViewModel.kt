package com.ethanprentice.shopifywordsearch.game

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ethanprentice.shopifywordsearch.WSApplication
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.Word
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import java.util.*

class GameViewModel: ViewModel() {

    // WORD SEARCH
    private val _wordSearch = MutableLiveData<WordSearch>().apply {
        val wordSearch = WordSearch()
        postValue(wordSearch)
    }
    val wordSearch: LiveData<WordSearch> = _wordSearch
    fun shuffleBoard() {
        _wordSearch.value?.shuffleBoard()
        _wordSearch.postValue(_wordSearch.value)

        clearBoardLines()
        clearFoundWords()
    }

    // FOUND WORDS
    private val foundWordsSet = TreeSet<Word>{ w1, w2 -> w1.string.compareTo(w2.string) }
    private val _foundWords = MutableLiveData<Set<Word>>().apply {
        postValue(foundWordsSet)
    }
    val foundWords: LiveData<Set<Word>> = _foundWords
    fun addFoundWord(word: Word): Boolean {
        val alreadyFound = foundWordsSet.contains(word)
        foundWordsSet.add(word)
        if (!alreadyFound) {
            _foundWords.postValue(foundWordsSet)
        }
        return alreadyFound
    }
    fun clearFoundWords() {
        foundWordsSet.clear()
        _foundWords.postValue(foundWordsSet)
    }
    fun isWordFound(word: Word): Boolean {
        return foundWordsSet.contains(word)
    }

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
}