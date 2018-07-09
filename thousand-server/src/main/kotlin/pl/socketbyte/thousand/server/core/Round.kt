package pl.socketbyte.thousand.server.core

/**
 * Simple data class for storing user round stats and possible options
 */
data class Round(val points: Int, val choices: List<Choice>)