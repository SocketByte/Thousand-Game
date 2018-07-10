package pl.socketbyte.thousand.server.core

import org.random.api.RandomOrgClient
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.server.settings

/**
 * Main game class, it has most of game-specific methods
 */
class Game(val server: NettyServer) {

    var status: GameStatus = GameStatus.WAITING

    val rules = RuleManager()
    private val roc = RandomOrgClient.getRandomOrgClient(
            settings.node("Keys").get("randomOrgKey", ""))
    private val cache = roc.createIntegerCache(5, 1, 6)

    init {
        rules.initializeRules()
    }

    fun getPlayers(): List<Player> {
        val list = mutableListOf<Player>()
        for ((index, channel) in server.clients.withIndex())
            list.add(Player(index, channel))
        return list
    }

    fun getPlayer(id: Int) = Player(id, server.clients[id])

    fun start() {
        for (player in getPlayers()) {
            // send welcome message etc
        }
        // to stuff
        status = GameStatus.PLAYING
    }

    /**
     * Uses access to RandomOrg API to generate fully random dice roll
     */
    fun roll() = cache.orWait.toMutableList()
}