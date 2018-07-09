package pl.socketbyte.thousand.shared.terminal

import pl.socketbyte.thousand.shared.terminal.stream.OutputTextStream

/**
 * Output thread to handle all outputs
 * Checks if theres anything to send, and sends it
 *
 * That allows to make non-blocking output stream
 */
class OutputThread : Thread("output#thread") {

    val textStream = OutputTextStream()

    override fun run() {
        while (true) {
            Thread.sleep(1)
            if (textStream.size() > 0) {
                val entry = textStream.next()

                when (entry.key) {
                    OutputType.PRINT -> System.out.print(entry.value)
                    OutputType.PRINT_ERR -> System.err.println(entry.value)
                    OutputType.PRINT_LINE -> System.out.println(entry.value)
                }
            }
        }
    }

    fun println(text: String) {
        textStream.push(OutputType.PRINT_LINE, text)
    }

    fun print(text: String) {
        textStream.push(OutputType.PRINT, text)
    }

    fun printErr(text: String) {
        textStream.push(OutputType.PRINT_ERR, text)
    }

}