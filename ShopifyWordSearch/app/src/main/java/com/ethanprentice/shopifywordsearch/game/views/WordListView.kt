package com.ethanprentice.shopifywordsearch.game.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.Word

class WordListView(context: Context, attrs: AttributeSet?, defStyle: Int) : GridView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val words: MutableList<Word> = mutableListOf()

    init {
        adapter =
            WordListAdapter(
                context,
                words
            )
    }

    fun setWords(wordList: List<Word>) {
        words.clear()
        words.addAll(wordList)
        (adapter as? WordListAdapter)?.notifyDataSetChanged()
    }

    private class WordListAdapter(context: Context, wordList: List<Word>) : ArrayAdapter<Word>(context, 0, wordList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val textView = TextView(context)

            textView.text = getItem(position)!!.string
            textView.setTextAppearance(R.style.metadata_title2)
            textView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))

            return textView
        }

    }

}