package com.ethanprentice.shopifywordsearch.game.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class BoardTileView(context: Context, attrs: AttributeSet?, defStyle: Int) : AppCompatTextView(context, attrs, defStyle) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    private var getTileSize: (() -> Int)? = null
    private val tileSize: Int
        get() = getTileSize?.invoke() ?: 0

    /**
     * Ensures that the board tiles are the correct sizing once the [BoardView] is measured
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val params = layoutParams
        params.width = tileSize
        params.height = tileSize
        layoutParams = params

        setMeasuredDimension(tileSize, tileSize)
    }

    fun setGetTileSize(fn: () -> Int) {
        getTileSize = fn
    }
}