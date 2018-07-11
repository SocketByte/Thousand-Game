package pl.socketbyte.thousand.server

import kotlinx.coroutines.experimental.async
import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.server.core.GameStatus
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
        info("Starting the server...")
        server.start()

        info("Creating game object...")
        game = Game(server)

        coloredInfo(GREEN_BOLD_BRIGHT + "Success! Server is ready.")
        println()
        println()
        val roll = game.roll()
        val round = game.rules.calculateRound(roll)

        while (game.status == GameStatus.WAITING) {
            if (game.getPlayersCount() >= 2) {
                // Yes, I wanted to do it better, but this was just easier xD
                var time = 15
                game.broadcastPrintln(BLUE_BRIGHT + "Game is starting in $time seconds...")
                time -= 5
                while (time > 0) {
                    sleep(5000)
                    game.broadcastPrintln(BLUE_BRIGHT + "Game is starting in $time seconds...")
                    time -= 5
                }
                sleep(1000)
                game.broadcastPrintln(YELLOW_BRIGHT + "Game is starting in 3...")
                sleep(1000)
                game.broadcastPrintln(YELLOW_BRIGHT + "Game is starting in 2...")
                sleep(1000)
                game.broadcastPrintln(YELLOW_BOLD_BRIGHT + "Game is starting in 1...")
                sleep(1000)
                game.status == GameStatus.PLAYING
                async { game.start() }
                return
            }
            sleep(5000)
            game.broadcastPrintln(BLUE_BRIGHT + "Waiting for player(s)...")
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