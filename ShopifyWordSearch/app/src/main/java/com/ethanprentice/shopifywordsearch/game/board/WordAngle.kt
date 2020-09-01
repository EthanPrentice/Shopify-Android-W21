package com.ethanprentice.shopifywordsearch.game.board

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
    }
}