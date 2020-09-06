package com.ethanprentice.shopifywordsearch.game.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.ethanprentice.shopifywordsearch.R
import com.ethanprentice.shopifywordsearch.game.board.BoardLine
import kotlin.math.sqrt


class BoardLineView(context: Context, private val boardView: BoardView, val boardLine: BoardLine) : View(context) {

    private var paint = Paint()

    private val strokeWidth: Float
        get() {
            val tileSize = boardView.columnWidth
            val tilePadding = 2 * context.resources.getDimensionPixelOffset(R.dimen.tile_padding)
            return sqrt(2f) * (tileSize - tilePadding) / 2
        }

    init {
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = ContextCompat.getColor(context, R.color.overlay_light)
        paint.strokeCap = Paint.Cap.ROUND

        if (boardLine.status == BoardLine.Status.FOUND) {
            wordFound()
        }
    }

    /**
     * If a word is not found in this line, display the line as red, fade it out and call the cleanup callback
     */
    private fun notFoundExitAnimation(cleanupViewCallback: () -> Any) {
        paint.color = ContextCompat.getColor(context, R.color.status_negative)
        paint.alpha = (0.75f * 255).toInt()
        invalidate()

        animate().alpha(0f)
            .setDuration(800L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    cleanupViewCallback()
                }
            })
    }

    /**
     * If a duplicate word is found in this line, fade it out and call the cleanup callback
     */
    private fun foundDuplicateExitAnimation(cleanupViewCallback: () -> Any) {
        paint.alpha = (0.75f * 255).toInt()
        invalidate()

        animate().alpha(0f)
            .setDuration(400L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    cleanupViewCallback()
                }
            })
    }

    /**
     * Run the corresponding exit animation and then call the cleanup callback
     */
    fun runExitAnimation(cleanupViewCallback: () -> Any) {
        when (boardLine.status) {
            BoardLine.Status.NOT_FOUND -> notFoundExitAnimation(cleanupViewCallback)
            BoardLine.Status.DUPLICATE -> foundDuplicateExitAnimation(cleanupViewCallback)
            else -> {}
        }
    }

    fun wordFound() {
        paint.color = ContextCompat.getColor(context, R.color.status_positive)
        paint.alpha = (0.55f * 255).toInt()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startTile = boardView.getTileAt(boardLine.startCoords) ?: return
        val endTile = boardView.getTileAt(boardLine.endCoords) ?: return

        val tileSize = boardView.getTileAt(boardLine.startCoords)?.width ?: return

        // Android will only draw one end of the line if on the same coords
        // To get all corner radi we need to show both sides so slightly increment if this is the case
        val sameTileFix = if (startTile == endTile) 0.1f else 0f

        fun getX(tile: View) = boardView.x + tile.x + tileSize / 2
        fun getY(tile: View) = boardView.y + tile.y + tileSize / 2

        canvas.drawLine(
            getX(startTile),
            getY(startTile),
            getX(endTile) + sameTileFix,
            getY(endTile),
            paint
        )
    }
}