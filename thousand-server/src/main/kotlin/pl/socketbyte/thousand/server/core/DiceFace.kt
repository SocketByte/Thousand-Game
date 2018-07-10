package pl.socketbyte.thousand.server.core

import pl.socketbyte.thousand.server.exception.FatalServerException

/**
 * Simple utility class to store all Unicode-based dice faces
 */
enum class DiceFace(val face: String) {

    ONE(" ———————\n" +
            "│       │\n" +
            "│   ●   │  1\n" +
            "│       │\n" +
            " ———————"),
    TWO(" ———————\n" +
            "│ ●     │\n" +
            "│       │  2\n" +
            "│     ● │\n" +
            " ———————"),
    THREE(" ———————\n" +
            "│ ●     │\n" +
            "│   ●   │  3\n" +
            "│     ● │\n" +
            " ———————"),
    FOUR(" ———————\n" +
            "│ ●   ● │\n" +
            "│       │  4\n" +
            "│ ●   ● │\n" +
            " ———————"),
    FIVE(" ———————\n" +
            "│ ●   ● │\n" +
            "│   ●   │  5\n" +
            "│ ●   ● │\n" +
            " ———————"),
    SIX(" ———————\n" +
            "│ ●   ● │\n" +
            "│ ●   ● │  6\n" +
            "│ ●   ● │\n" +
            " ———————");

    companion object {
        fun getByNumber(roll: Int): DiceFace {
            return when (roll) {
                1 -> ONE
                2 -> TWO
                3 -> THREE
                4 -> FOUR
                5 -> FIVE
                6 -> SIX
                else -> throw FatalServerException("That should not happen. " +
                        "$roll is greater than 6 (?)")
            }
        }
    }
}