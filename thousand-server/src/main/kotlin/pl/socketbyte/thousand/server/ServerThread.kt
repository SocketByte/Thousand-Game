package pl.socketbyte.thousand.server

import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.shared.BLUE
import pl.socketbyte.thousand.shared.BLUE_BOLD_BRIGHT
import pl.socketbyte.thousand.shared.GREEN_BOLD_BRIGHT
import pl.socketbyte.thousand.shared.clearScreen
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import pl.socketbyte.thousand.shared.terminal.JobThread
import pl.socketbyte.thousand.shared.terminal.OutputThread
import java.text.SimpleDateFormat

class ServerThread : JobThread(OutputThread()) {
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    init {
        outputThread.start()
    }

    override fun run() {
        clearScreen()

        sleep(200)
        colored(BLUE_BOLD_BRIGHT + "  _____   _                                                 _ \n" +
                " |_   _| | |__     ___    _   _   ___    __ _   _ __     __| |\n" +
                "   | |   | '_ \\   / _ \\  | | | | / __|  / _` | | '_ \\   / _` |\n" +
                "   | |   | | | | | (_) | | |_| | \\__ \\ | (_| | | | | | | (_| |\n" +
                "   |_|   |_| |_|  \\___/   \\__,_| |___/  \\__,_| |_| |_|  \\__,_|")
        sleep(200)
        colored(BLUE + "                              ___                       \n" +
                "                            / __| ___ _ ___ _____ _ _  \n" +
                "                            \\__ \\/ -_) '_\\ V / -_) '_| \n" +
                "                            |___/\\___|_|  \\_/\\___|_|   \n")
        sleep(400)
        println()
        info("Running server at port 25890...")
        server = NettyServer(25890)
        info("Registering kryo classes...")
        server.kryo.register(Packet::class.java)
        server.kryo.register(PacketKeepAlive::class.java)
        info("Starting the server...")
        server.start()

        info("Creating game object...")
        game = Game(server)

        coloredInfo(GREEN_BOLD_BRIGHT + "Success! Server is ready.")
        println()
        println()
        val roll = game.roll()
        val round = game.rules.calculateRound(roll)
    }

    fun coloredInfo(text: String = "") {
        colored("[INFO ${dateFormat.format(System.currentTimeMillis())}] $text")
    }

    fun info(text: String = "") {
        println("[INFO ${dateFormat.format(System.currentTimeMillis())}] $text")
    }

    fun problem(text: String = "") {
        severe("[ERROR ${dateFormat.format(System.currentTimeMillis())}] $text")
    }

}