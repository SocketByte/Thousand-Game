package pl.socketbyte.thousand.shared.terminal.stream

import pl.socketbyte.thousand.shared.terminal.OutputType
import java.util.*

/**
 * Very simple "stream-like" class to handle outputs
 * Works like a simple pseudo-queue
 */
class OutputTextStream {

    private val queue =
            mutableListOf<Map.Entry<OutputType, String>>()

    val history = mutableListOf<String>()

    fun push(type: OutputType, text: String) {
        queue.add(AbstractMap.SimpleEntry<OutputType, String>(type, text))
        history.add(text)
    }

    fun size(): Int {
        return queue.size
    }

    fun next(): Map.Entry<OutputType, String> {
        val first = queue.first()
        queue.remove(first)
        return first
    }

}