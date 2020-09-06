package com.ethanprentice.shopifywordsearch.game.board

class Word(var string: String, var angle: WordAngle, var startingCoords: BoardCoords) {

    val allCoords: List<BoardCoords>
        get() = List(formattedLength) {
            BoardCoords(
                startingCoords.x + it * angle.getXDelta(),
                startingCoords.y + it * angle.getYDelta()
            )
        }

    val endCoords: BoardCoords
        get() = BoardCoords(
            startingCoords.x + (formattedString.length - 1) * angle.getXDelta(),
            startingCoords.y + (formattedString.length - 1) * angle.getYDelta()
        )

    /**
     * The string formatted to be placed on the board (non-letters removed, letters converted to lowercase)
     */
    val formattedString: String = string.toLowerCase().filter { it in 'a'..'z' }

    val formattedLength: Int
        get() = formattedString.length

    override fun toString(): String {
        return "$string | ${angle.name} | $startingCoords"
    }

    companion object {
        fun getFormattedLength(wordString: String): Int {
            return wordString.filter { it in 'A'..'z' }.length
        }
    }
}