package com.ethanprentice.shopifywordsearch.game.board

class Word(var string: String, var angle: WordAngle, var startingCoords: BoardCoords) {

    val length: Int
        get() = string.length

    val allCoords: List<BoardCoords>
        get() = List(length) {
            BoardCoords(
                startingCoords.x + it * angle.getXDelta(),
                startingCoords.y + it * angle.getYDelta()
            )
        }

    val lastCoords: BoardCoords
        get() = BoardCoords(
            startingCoords.x + (length - 1) * angle.getXDelta(),
            startingCoords.y + (length - 1) * angle.getYDelta()
        )

    operator fun get(i: Int): Char = string[i]

    override fun toString(): String {
        return "$string | ${angle.name} | $startingCoords"
    }
}