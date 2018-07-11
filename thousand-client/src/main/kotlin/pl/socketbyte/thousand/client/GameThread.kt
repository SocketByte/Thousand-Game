package pl.socketbyte.thousand.client

import pl.socketbyte.thousand.client.jna.NativeConsole
import pl.socketbyte.thousand.client.netty.NettyClient
import pl.socketbyte.thousand.shared.*
import pl.socketbyte.thousand.shared.netty.kryo.KryoSharedRegister
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import pl.socketbyte.thousand.shared.packet.PacketPlayerLogin
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.terminal.JobThread
import pl.socketbyte.thousand.shared.terminal.OutputThread
import pl.socketbyte.thousand.shared.terminal.TimedInput
import java.util.*
import java.util.concurrent.TimeUnit

// Current connection, null if not connected.
var client: NettyClient? = null

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
        print("$RED\rMatchmaking service is currently disabled.\n$RESET")
        println(RED + "Please enter the server IP below." + RESET)

        client = connectFromInput()
        help("Successfully joined the server.")
        println()
        val name = proceedWithInput("Please enter your name or leave empty:")
        val packet = PacketPlayerLogin(name)
        if (client == null) {
            return
        }
        client?.write(packet)
    }

    private fun connectFromInput(lastInvalid: Boolean = false): NettyClient {
        if (lastInvalid) {
            severe("Invalid server address or server does not respond. Please try again.")
            println()
        }

        val server = proceedWithInput("Server address to connect:", 200)
        if (server.isEmpty())
            return connectFromInput(true)

        val split = server.split(":")
        if (split.isEmpty())
            return connectFromInput(true)

        val client: NettyClient
        try {
            val address = split[0]
            val port = split[1].toInt()

            client = NettyClient(address, port)
            client.start()
        } catch (e: Exception) {
            return connectFromInput(true)
        }

        return client
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
    fun timedInput(text: String, time: Long): String? {
        println()
        println()
        colored("$WHITE_UNDERLINED$text (You have approx. $time seconds): ")
        print("> ")
        val timedInput = TimedInput(time, TimeUnit.SECONDS)
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