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
import com.ethanprentice.shopifywordsearch.util.px


class BoardLineView(context: Context, private val boardView: BoardView, val boardLine: BoardLine) : View(context) {

    private var paint = Paint()

    init {
        paint.strokeWidth = 20.px.toFloat()
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = ContextCompat.getColor(context, R.color.overlay_light)
        paint.strokeCap = Paint.Cap.ROUND

        if (boardLine.type == BoardLine.Status.FOUND) {
            wordFound()
        }
    }

    /**
     * If the word is not found in this line, display the line as red, fade it out, and remove it
     */
    fun wordNotFound(onAnimationEndCallback: () -> Any) {
        paint.color = ContextCompat.getColor(context, R.color.status_negative)
        paint.alpha = (0.75f * 255).toInt()
        invalidate()

        animate().alpha(0f)
            .setDuration(800L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEndCallback()
                }
            })
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

        canvas.drawLine(
            boardView.x + startTile.x + tileSize / 2,
            boardView.y + startTile.y + tileSize / 2,
            boardView.x + endTile.x + tileSize / 2 + sameTileFix,
            boardView.y + endTile.y + tileSize / 2,
            paint
        )
    }
}