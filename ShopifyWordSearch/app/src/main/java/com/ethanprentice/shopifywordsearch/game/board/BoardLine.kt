package com.ethanprentice.shopifywordsearch.game.board

class BoardLine(val startCoords: BoardCoords, var endCoords: BoardCoords, var status: Status) {
    enum class Status {
        ACTIVE,
        FOUND,
        NOT_FOUND,
        DUPLICATE
    }

    /**
     * @return whether the angle of the line is a multiple of 45 degrees
     */
    fun isValidAngle(): Boolean {
        val xDiff = startCoords.x - endCoords.x
        val yDiff = startCoords.y - endCoords.y

        return (xDiff == yDiff || xDiff == -yDiff) || (xDiff == 0 || yDiff == 0)
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        return when (other) {
            is BoardLine -> {
                (startCoords == other.startCoords && endCoords == other.endCoords) ||
                (startCoords == other.endCoords && endCoords == other.startCoords)
            }
            else -> false
        }
    }

    override fun toString(): String {
        return "[$startCoords, $endCoords]"
    }
}