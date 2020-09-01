package com.ethanprentice.shopifywordsearch.game.board

data class BoardCoords(
    var x: Int,
    var y: Int
) {
    override fun toString(): String {
        return "($x, $y)"
    }
}