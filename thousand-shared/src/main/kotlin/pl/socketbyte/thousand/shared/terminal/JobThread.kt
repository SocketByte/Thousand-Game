package pl.socketbyte.thousand.shared.terminal

import pl.socketbyte.thousand.shared.*

abstract class JobThread(
        val outputThread: OutputThread)
    : Thread("job#thread") {

    fun println(text: String = "") {
        outputThread.println(text)
    }

    fun print(text: String = "") {
        outputThread.print(text)
    }

    fun printErr(text: String = "") {
        outputThread.printErr(text)
    }

    fun colored(text: String = "") {
        outputThread.println(text + RESET)
    }

    fun severe(text: String = "") {
        outputThread.println(RED_BOLD + text + RESET)
    }

    fun warn(text: String = "") {
        outputThread.println(YELLOW + text + RESET)
    }

    fun help(text: String = "") {
        outputThread.println(BLUE_BRIGHT + text + RESET)
    }

    fun success(text: String = "") {
        outputThread.println(GREEN + text + RESET)
    }

}