package com.ethanprentice.shopifywordsearch.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import com.ethanprentice.shopifywordsearch.game.board.WordSearch

class GameViewModel: ViewModel() {

    private val _wordSearch = MutableLiveData<WordSearch>().apply {
        val wordSearch = WordSearch()
        postValue(wordSearch)
    }
    val wordSearch: LiveData<WordSearch> = _wordSearch

    val boardLines = mutableListOf<BoardLine>()

    fun shuffleBoard() {
        _wordSearch.value?.shuffleBoard()
        _wordSearch.postValue(_wordSearch.value)

        boardLines.clear()
    }
}