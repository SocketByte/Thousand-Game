package pl.socketbyte.thousand.client

import pl.socketbyte.thousand.client.jna.NativeConsole
import pl.socketbyte.thousand.shared.*
import pl.socketbyte.thousand.shared.terminal.JobThread
import pl.socketbyte.thousand.shared.terminal.OutputThread
import pl.socketbyte.thousand.shared.terminal.TimedInput
import java.util.*
import java.io.BufferedReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class GameThread : JobThread(OutputThread()) {

    init {
        outputThread.start()
    }

    /**
     * Main game thread
     */
    override fun run() {
        clearScreen()

        sleep(200)
        colored(BLUE_BOLD_BRIGHT + "  _____   _                                                 _ \n" +
                " |_   _| | |__     ___    _   _   ___    __ _   _ __     __| |\n" +
                "   | |   | '_ \\   / _ \\  | | | | / __|  / _` | | '_ \\   / _` |\n" +
                "   | |   | | | | | (_) | | |_| | \\__ \\ | (_| | | | | | | (_| |\n" +
                "   |_|   |_| |_|  \\___/   \\__,_| |___/  \\__,_| |_| |_|  \\__,_|")
        sleep(200)
        colored("$BLUE                              _   __  \n" +
                "                         __ _/ | /  \\ \n" +
                "                         \\ V / || () |\n" +
                "                          \\_/|_(_)__/ \n")

        sleep(500)
        colored(CYAN_BOLD + "Welcome to Thousand! \n" + YELLOW_BRIGHT +
                "I recommend you to check if your terminal supports ANSI/Unicode before proceeding!\n" +
                "I hope you will have a great time with my game!")

        proceed("Search a game? Press enter to continue.")
        println()
        val time = 200L
        for (i in 0..1) {
            sleep(time)
            print("Matchmaking... /\r")
            sleep(time)
            print("Matchmaking... -\r")
            sleep(time)
            print("Matchmaking... \\\r")
            sleep(time)
            print("Matchmaking... /\r")
            sleep(time)
            print("Matchmaking... -\r")
            sleep(time)
            print("Matchmaking... \\\r")
            sleep(time)
            print("Matchmaking... /\r")
        }
        print(RED + "\rMatchmaking service is currently disabled.\n" + RESET)
        println(RED + "Please enter the server IP below." + RESET)

        val choice = timedInput("Timed read", 5)
        println()
        if (choice == null) {
            warn("You did not answer in time!")
        } else  println("Your choice is $choice")
        proceed()
    }

    /**
     * @return last string that was printed to the console
     */
    fun lastOutput(): String {
        return this.outputThread.textStream.history.last()
    }

    /**
     * Prints a message using JNA console
     */
    fun printlnNative(text: String = "") {
        NativeConsole.writeNativeText(outputThread, text)
    }

    /**
     * It timeouts and cancells the input reading after some time
     * It does that without blocking the output thread
     *
     * @return input or null if timeouted
     */
    private fun timedInput(text: String, time: Long): String? {
        println()
        println()
        colored("$WHITE_UNDERLINED$text (You have approx. $time seconds): ")
        print("> ")
        val timedInput = TimedInput(1, time, TimeUnit.SECONDS)
        val line = timedInput.readLine()
        if (line == "") {
            return null
        }

        return line
    }

    /**
     * Requires user to press enter (or any key + enter) to unlock the thread
     * It's not recommended to use this method after connecting to a server
     * because that may result in keep alive packet loss (and disconnection)
     *
     * @param text Message that is shown to the player
     * @param delay Delay (to allow the continuation) in milliseconds
     */
    private fun proceed(text: String = "Press enter to continue...", delay: Long = 0) {
        println()
        println()
        sleep(delay)
        colored(WHITE_UNDERLINED + text)
        val scanner = Scanner(System.`in`)
        scanner.nextLine()
    }

    /**
     * The same stuff as [proceed] method, but also returns the input.
     *
     * @param text Message that is shown to the player
     * @param delay Delay (to allow the continuation) in milliseconds
     */
    private fun proceedWithInput(text: String, delay: Long = 0): String {
        println()
        println()
        sleep(delay)
        colored(WHITE_UNDERLINED + text)
        print("> ")
        val scanner = Scanner(System.`in`)
        return scanner.nextLine()
    }
}