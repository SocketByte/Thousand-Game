package pl.socketbyte.thousand.server.core.data

/**
 * Simple data class for storing points-rules, like amount x dice = points
 */
data class Rule(val amount: Int, val dice: Int, val points: Int)