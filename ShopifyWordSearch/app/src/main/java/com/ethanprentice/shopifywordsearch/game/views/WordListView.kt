package com.ethanprentice.shopifywordsearch.game.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.Word
import java.util.*


class WordListView(context: Context, attrs: AttributeSet?, defStyle: Int) : GridView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * Maps words to whether or not they've been found
     * Use a TreeMap to guarantee correct ordering in the adapter
     */
    private val words = TreeMap<Word, Boolean> { w1, w2 -> w1.string.compareTo(w2.string) }

    private val wordListAdapter
        get() = (adapter as WordListAdapter)


    init {
        adapter = WordListAdapter(context, words)
    }

    /**
     * Populates [words] with [wordList] as keys with false as the default entry and notifies the adapter of changes
     */
    fun setWords(wordList: Collection<Word>) {
        words.clear()
        for (word in wordList) {
            words[word] = false
        }
        wordListAdapter.notifyDataSetChanged()
    }

    fun setWordAsFound(word: Word) {
        words[word] = true
        wordListAdapter.notifyDataSetChanged()
    }

    fun setWordsAsFound(wordList: Collection<Word>) {
        wordList.forEach { word ->
            words[word] = true
        }
        wordListAdapter.notifyDataSetChanged()
    }

    class WordListAdapter(private val context: Context, private var data: TreeMap<Word, Boolean>) : BaseAdapter() {
        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(pos: Int): Map.Entry<Word, Boolean> {
            return data.entries.toList()[pos]
        }

        override fun getItemId(arg0: Int): Long {
            return arg0.toLong()
        }

        override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
            val textView = TextView(context)

            textView.text = getItem(pos).key.string
            textView.setTextAppearance(R.style.metadata_title2)
            textView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            textView.setPadding(context.resources.getDimensionPixelOffset(R.dimen.LU_1))

            if (getItem(pos).value) {
                textView.setBackgroundResource(R.drawable.found_word_bg)
            }

            textView.setOnClickListener(null)

            return textView
        }
    }

}