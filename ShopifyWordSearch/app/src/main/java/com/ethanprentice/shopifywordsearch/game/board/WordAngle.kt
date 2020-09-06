package com.ethanprentice.shopifywordsearch.game.board

import java.lang.Integer.max
import kotlin.math.abs

enum class WordAngle(
    private val xDelta: Int,
    private val yDelta: Int
) {
    DEGREES_0(0, -1),
    DEGREES_45(1, -1),
    DEGREES_90(1, 0),
    DEGREES_135(1, 1),
    DEGREES_180(0, 1),
    DEGREES_225(-1, 1),
    DEGREES_270(-1, 0),
    DEGREES_315(-1, -1);

    fun getXDelta() = xDelta
    fun getYDelta() = yDelta

    companion object {
        fun leftToRightValues() = arrayOf(DEGREES_45, DEGREES_90, DEGREES_135, DEGREES_180)

        /**
         * @return the angle between [startCoords] and [endCoords], null if the angle is invalid
         */
        fun getAngle(startCoords: BoardCoords, endCoords: BoardCoords): WordAngle? {
            val xDelta = (endCoords.x - startCoords.x)
            val yDelta = (endCoords.y - startCoords.y)
            val maxDelta = max(abs(xDelta), abs(yDelta)).toFloat()

            if (maxDelta == 0f) {
                return DEGREES_0
            }

            val xFactor = xDelta / maxDelta
            val yFactor = yDelta / maxDelta

            val angle = values().find { angle ->
                angle.xDelta.toFloat() == xFactor && angle.yDelta.toFloat() == yFactor
            }
            if (angle != null) {
                return angle
            }

            if (xFactor == 0f && yFactor == 0f) {
                return DEGREES_0
            }

            return null
        }

        fun getAngle(line: BoardLine): WordAngle? {
            return getAngle(line.startCoords, line.endCoords)
        }
    }
}