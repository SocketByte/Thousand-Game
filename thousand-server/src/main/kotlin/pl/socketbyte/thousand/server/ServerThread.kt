package pl.socketbyte.thousand.server

import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.shared.*
import pl.socketbyte.thousand.shared.netty.kryo.KryoSharedRegister
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import pl.socketbyte.thousand.shared.packet.PacketPlayerChoice
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.terminal.JobThread
import pl.socketbyte.thousand.shared.terminal.OutputThread
import java.text.SimpleDateFormat
import java.util.concurrent.ThreadLocalRandom

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
        KryoSharedRegister.registerAll(server.kryo)
        info("Starting the server...")
        server.start()

        info("Creating game object...")
        game = Game(server)

        coloredInfo(GREEN_BOLD_BRIGHT + "Success! Server is ready.")
        println()
        println()
        val roll = game.roll()
        val round = game.rules.calculateRound(roll)

        while (true) {
            if (game.getPlayers().isEmpty())
                continue

            sleep(500)

            val random = ThreadLocalRandom.current()
                    .nextInt(0, game.getPlayers().size)
            val randomPlayer = game.getPlayer(random)
            randomPlayer.println(YELLOW + "You were randomly chosen player by the server!")

            game.broadcastPrintln(RED + "This message was broadcasted to all of the players.")
        }
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