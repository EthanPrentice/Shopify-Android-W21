package com.ethanprentice.shopifywordsearch.game.board

class BoardLine(val startCoords: BoardCoords, var endCoords: BoardCoords, var type: Status) {
    enum class Status {
        ACTIVE,
        FOUND,
        NOT_FOUND
    }

    /**
     * @return whether the angle of the line is a multiple of 45 degrees
     */
    private fun isValidAngle(): Boolean {
        val xDiff = startCoords.x - endCoords.x
        val yDiff = startCoords.y - endCoords.y

        return (xDiff == yDiff || xDiff == -yDiff)
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        return when (other) {
            is BoardLine -> startCoords == other.startCoords && endCoords == other.endCoords
            else -> false
        }
    }
}