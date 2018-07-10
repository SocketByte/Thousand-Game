package pl.socketbyte.thousand.server.core.data

import pl.socketbyte.thousand.server.core.data.Choice

/**
 * Simple data class for storing user round stats and possible options
 */
data class Round(val points: Int, val choices: List<Choice>)