package pl.socketbyte.thousand.server.core

import io.netty.channel.Channel
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import org.random.api.RandomOrgClient
import pl.socketbyte.thousand.server.core.data.GameRound
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.server.settings
import pl.socketbyte.thousand.shared.*
import pl.socketbyte.thousand.shared.packet.PacketConsoleClear
import pl.socketbyte.thousand.shared.packet.PacketPlayerChoice
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

/**
 * Main game class, it has most of game-specific methods
 */
class Game(val server: NettyServer) {

    var status: GameStatus = GameStatus.WAITING
        private set
    var currentPlayerRound: Player? = null
    var currentRound: GameRound? = null

    val rules = RuleManager()
    private val roc = RandomOrgClient.getRandomOrgClient(
            settings.node("Keys").get("randomOrgKey", ""))
    private val cache = roc.createIntegerCache(5, 1, 6)

    init {
        rules.initializeRules()
    }

    private val players = mutableMapOf<Int, Player>()

    fun addPlayer(player: Player): Player {
        players[player.id] = player
        return player
    }

    fun removePlayer(channel: Channel) {
        val player = getPlayer(channel) ?: return

        players.remove(player.id)
    }

    fun getPlayers(): List<Player> {
        return players.values.toList()
    }

    fun getPlayer(id: Int): Player? {
        return players[id]
    }

    fun getPlayer(channel: Channel): Player? {
        for ((_, player) in players) {
            if (player.channelId == channel.id().asShortText())
                return player
        }
        return null
    }

    fun getPlayer(name: String): Player? {
        for ((_, value) in players) {
            if (value.name == name)
                return value
        }
        return null
    }

    fun broadcastClearScreen() {
        for (player in getPlayers())
            player.sendPacket(PacketConsoleClear())
    }

    fun clearScreen(player: Player) {
        player.sendPacket(PacketConsoleClear())
    }

    /**
     * Broadcasts a message as a `print`
     * Sleeps 30 ms between each player, because otherwise the packet can crash (and will crash)
     */
    fun broadcastPrint(text: String = "") {
        for (player in getPlayers()) {
            player.print(text)
        }
    }

    fun broadcastPrintln(text: String = "") {
        for (player in getPlayers()) {
            player.println(text)
        }
    }

    fun broadcastPrintlnNative(text: String = "") {
        for (player in getPlayers()) {
            player.printlnNative(text)
        }
    }

    fun broadcastPrintExcept(except: Player, text: String = "") {
        for (player in getPlayers()) {
            if (player == except)
                continue
            player.print(text)
        }
    }

    fun broadcastPrintlnExcept(except: Player, text: String = "") {
        for (player in getPlayers()) {
            if (player == except)
                continue
            player.println(text)
        }
    }

    fun broadcastPrintlnNative(except: Player, text: String = "") {
        for (player in getPlayers()) {
            if (player == except)
                continue
            player.printlnNative(text)
        }
    }

    fun getPlayersCount(): Int {
        return players.size
    }

    suspend fun start() {
        status = GameStatus.PLAYING

        broadcastClearScreen()

        showScores()

        currentPlayerRound = getPlayer(0)
        if (currentPlayerRound == null)
            return

        startRollingRound()
    }

    suspend fun startRollingRound(roll: List<Int> = roll()) {
        broadcastClearScreen()
        broadcastPrintln("$YELLOW_BRIGHT<<<   " +
                "$CYAN_BRIGHT Player ${currentPlayerRound?.name} " +
                "is now rolling...    $YELLOW_BRIGHT>>>")
        if (currentRound == null)
            currentRound = GameRound(currentPlayerRound!!, 0)
        broadcastPrintln()
        broadcastPrintln()
        Thread.sleep(1000)
        val round = rules.calculateRound(roll.toMutableList())

        representDices(roll)

        var anyValidChoice = true
        for (choice in round.choices) {
            val left = roll.size - choice.amount
            if (left == 0)
                anyValidChoice = false
        }
        Thread.sleep(1000)
        when {
            anyValidChoice -> {
                broadcastPrintlnExcept(currentPlayerRound!!, CYAN_BRIGHT + "Player ${currentPlayerRound?.name} is now choosing what to do...")
                if (round.choices.isEmpty()) {
                    if (round.points == 0) {
                        broadcastPrintln(RED_BRIGHT + "Player ${currentPlayerRound!!.name} lost the round!")
                        runNextRoll(true)
                    }
                    else {
                        broadcastPrintln(PURPLE_BRIGHT + "Player ${currentPlayerRound!!.name} rolled the penta! (all dices score)")
                        broadcastPrintln(GREEN_BOLD_BRIGHT + "  Total $GREEN${round.points} points!")
                        runNextRoll()
                    }
                    return
                }

                currentPlayerRound!!.println()
                currentPlayerRound!!.println("You rolled $CYAN_BRIGHT$roll$RESET what gives you these options:")
                currentPlayerRound!!.println(CYAN_BRIGHT + "1.$RESET Pass and take ${round.points + currentRound!!.points} points")
                for ((index, choice) in round.choices.withIndex()) {
                    val left = roll.size - choice.amount

                    currentPlayerRound!!.println(CYAN_BRIGHT + "${index+2}.$RESET Play with $left dices and take ${choice.points} points")
                }

                try {
                    server.writeAndRequest<PacketPlayerChoice>(currentPlayerRound!!.channel, PacketPlayerChoice()) {
                        if (it.choice == -1) {
                            broadcastPrintlnExcept(currentPlayerRound!!, RED_BRIGHT +
                                    "Player ${currentPlayerRound?.name} did not choose anything, so that means he passed!")
                            currentRound!!.points += round.points
                            broadcastPrintln(GREEN_BOLD_BRIGHT + "  Total $GREEN${currentRound!!.points} points!")
                            runNextRoll()
                            return
                        }
                        if (it.choice == 1) {
                            broadcastPrintlnExcept(currentPlayerRound!!, RED_BRIGHT +
                                    "Player ${currentPlayerRound?.name} chose to pass!")
                            currentRound!!.points += round.points
                            broadcastPrintln(GREEN_BOLD_BRIGHT + "  Total $GREEN${currentRound!!.points} points!")
                            runNextRoll()
                            return
                        }
                        val choiceId = it.choice - 2

                        val choice = round.choices[choiceId]

                        val left = roll.size - choice.amount
                        val rollSecond = roll(left)

                        broadcastPrintlnExcept(currentPlayerRound!!, CYAN_BRIGHT +
                                "Player ${currentPlayerRound?.name} chose to play with $left dices again...")
                        broadcastPrintlnExcept(currentPlayerRound!!, CYAN_BOLD_BRIGHT +
                                "Player ${currentPlayerRound?.name} rolled ${choice.points} this round.")
                        currentRound!!.points += choice.points
                        broadcastPrintln(GREEN_BOLD_BRIGHT + "  Total $GREEN${currentRound!!.points} points!")
                        Thread.sleep(4000)
                        startRollingRound(rollSecond)
                    }
                } catch (e: TimeoutCancellationException) {
                    broadcastPrintlnExcept(currentPlayerRound!!, RED_BRIGHT +
                            "Player ${currentPlayerRound?.name} did not choose anything, so that means he passed!")
                    currentRound!!.points += round.points
                    broadcastPrintln(GREEN_BOLD_BRIGHT + "  Total $GREEN${currentRound!!.points} points!")
                    runNextRoll()
                    return
                }
                return
            }
            else -> {
                broadcastPrintln(RED_BRIGHT + "Player ${currentPlayerRound!!.name} lost the round!")
                runNextRoll(true)
            }
        }
    }

    suspend fun runNextRoll(lost: Boolean = false) {
        if (!lost)
            currentPlayerRound!!.score += currentRound!!.points

        Thread.sleep(4000)
        showScores()
        Thread.sleep(1000)
        checkWinners()
        Thread.sleep(7000)

        currentPlayerRound = if ((currentPlayerRound!!.id + 1) == getPlayersCount()) {
            getPlayer(0)
        } else getPlayer(currentPlayerRound!!.id + 1)
        currentRound = null
        startRollingRound()
    }

    fun checkWinners() {
        var winning = false
        for ((_, score) in getScores()) {
            if (score >= 1000) {
                winning = true
                break
            }
        }
        if (!winning)
            return

        val first = getFirstPlace() ?: return

        broadcastPrintln(YELLOW_BRIGHT + "Player ${first.name} won with score ${first.score}!")
        broadcastPrintln(YELLOW_BRIGHT + "The game is ending...")
        for (player in getPlayers()) {
            player.channel.disconnect()
        }
    }

    fun representDices(roll: List<Int>) {
        for (i in roll) {
            broadcastPrintlnNative(DiceFace.getByNumber(i).face)
            Thread.sleep(1000)
        }
    }

    fun showScores() {
        broadcastPrintln()
        broadcastPrintln()
        broadcastPrintln(RED_UNDERLINED + "Scores:")
        for (player in getPlayers()) {
            broadcastPrint(RED_BRIGHT + "Player ${player.name}:  " +
                    "${player.score}   ")
            val place = getFirstPlace() ?: continue

            if (place == player)
                broadcastPrint(YELLOW_BRIGHT + "1st\n")
            else broadcastPrint("\n")

            Thread.sleep(500)
        }
        broadcastPrintln()
        broadcastPrintln()
    }

    fun end() {
        // Send packet PacketEndGame or something to show winners and overall stats etc
        for (player in getPlayers()) {
            player.channel.disconnect()
        }
    }

    fun getFirstPlace(): Player? {
        val biggest = getBiggest(getScores().values.toList())

        for ((player, score) in getScores()) {
            if (score == biggest)
                return player
        }
        return null
    }

    fun getScores(): MutableMap<Player, Int> {
        val map = mutableMapOf<Player, Int>()
        for (player in getPlayers())
            map[player] = player.score
        return map
    }

    fun getBiggest(list: List<Int>): Int {
        var biggest = 0
        for (i in list) {
            if (i > biggest)
                biggest = i
        }
        return biggest
    }

    /**
     * Uses access to RandomOrg API to generate fully random dice roll
     */
    fun roll(amount: Int = 5) = roc.generateIntegers(amount, 1, 6).toMutableList()
}