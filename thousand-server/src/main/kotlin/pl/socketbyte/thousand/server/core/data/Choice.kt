package pl.socketbyte.thousand.server.core.data

/**
 * Similar to Rule class, but this can be modified based on user roll
 *
 * I could use Rule, but this will be much better for code clarity and further usage
 * Because using the Rule class for this would make a lot of confusion in the later stages of the game
 */
data class Choice(val amount: Int, val dice: Int, val points: Int)