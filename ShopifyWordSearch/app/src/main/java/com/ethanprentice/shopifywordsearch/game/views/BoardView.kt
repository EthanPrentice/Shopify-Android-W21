package com.ethanprentice.shopifywordsearch.game.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.BoardCoords
import com.ethanprentice.shopifywordsearch.game.board.WordSearch
import com.ethanprentice.shopifywordsearch.util.px

class BoardView(context: Context, attrs: AttributeSet?, defStyle: Int) : GridView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var wordSearch: WordSearch? = null

    private val flattenedBoard = mutableListOf<Char>()


    private val boardAdapter
        get() = adapter as BoardAdapter

    private val tileSize
        get() = (measuredWidth - 2 * PADDING.px) / numColumns

    init {
        adapter = BoardAdapter(context, flattenedBoard)

        background = ResourcesCompat.getDrawable(resources, R.drawable.board_background, null)
        setPadding(PADDING.px)
    }

    fun setWordSearch(ws: WordSearch) {
        wordSearch = ws
        val board = ws.board

        flattenedBoard.clear()
        board.forEach { row ->
            row.forEach { char ->
                flattenedBoard.add(char)
            }
        }
        boardAdapter.notifyDataSetChanged()

        numColumns = board.size
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    // Force BoardView to be square
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val width = MeasureSpec.getSize(widthSpec)
        val height = MeasureSpec.getSize(heightSpec)
        val size = if (width > height) height else width

        val params = layoutParams
        params.width = size
        params.height = size
        layoutParams = params

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        updateTileSizes()
    }

    private fun updateTileSizes() {
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val view = getChildAt(i) as TextView
            val params = view.layoutParams
            params.width = tileSize
            params.height = tileSize
            view.layoutParams = params
        }
    }

    private inner class BoardAdapter(context: Context, flattenedBoard: List<Char>) : ArrayAdapter<Char>(context, 0, flattenedBoard) {

        // Helper class to properly measure board tiles so they fit the BoardView
        private inner class BoardTile(context: Context) : AppCompatTextView(context) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)

                val params = layoutParams
                params.width = tileSize
                params.height = tileSize
                layoutParams = params

                setMeasuredDimension(tileSize, tileSize)
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return BoardTile(context).apply {
                text = getItem(position)!!.toUpperCase().toString()
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                setTextAppearance(R.style.board_text)
                setTextColor(ContextCompat.getColor(context, R.color.board_text))
                gravity = Gravity.CENTER
            }
        }

    }

    /**
     * @return the appropriate board coordinates based on view-level coordinates
     */
    fun coordsToBoardCoords(x: Int, y: Int): BoardCoords? {
        val startX = PADDING.px
        val startY = PADDING.px
        val endX = measuredWidth - 2 * PADDING.px
        val endY = measuredHeight - 2 * PADDING.px

        // if out of bounds for a tile, return null
        if (x !in startX until endX || y !in startY until endY) {
            return null
        }

        return BoardCoords(
            (x - startX) / tileSize,
            (y - startY) / tileSize
        )
    }

    fun getTileAt(boardCoords: BoardCoords): TextView? {
        val position = numColumns * boardCoords.y + boardCoords.x
        return getChildAt(position) as? TextView
    }

    companion object {
        private const val PADDING = 10 // dp
    }
}