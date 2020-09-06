package com.ethanprentice.shopifywordsearch.game.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
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

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * Forces the [BoardView] to have a 1:1 aspect ratio
     */
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

    /**
     * Sets [wordSearch] and flattens the board to update [boardAdapter]
     */
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

    fun getTileAt(boardCoords: BoardCoords): BoardTileView? {
        val position = numColumns * boardCoords.y + boardCoords.x
        return getChildAt(position) as? BoardTileView
    }


    private inner class BoardAdapter(context: Context, flattenedBoard: List<Char>) : ArrayAdapter<Char>(context, R.layout.board_tile, flattenedBoard) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = (convertView as? BoardTileView) ?: run {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.board_tile, parent, false) as BoardTileView
            }

            return view.apply {
                setGetTileSize {
                    tileSize
                }
                text = getItem(position)!!.toUpperCase().toString()
            }
        }

    }

    companion object {
        private const val PADDING = 10 // dp
    }
}