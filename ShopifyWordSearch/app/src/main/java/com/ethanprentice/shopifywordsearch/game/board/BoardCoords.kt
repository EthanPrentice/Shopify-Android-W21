package com.ethanprentice.shopifywordsearch.game.board

data class BoardCoords(
    var x: Int,
    var y: Int
) {
    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BoardCoords -> x == other.x && y == other.y
            else -> false
        }
    }
}