package pl.socketbyte.thousand.server.core

import org.random.api.RandomOrgClient
import pl.socketbyte.thousand.server.settings

/**
 * Main game class, it has most of game-specific methods
 */
class Game {

    val rules = RuleManager()
    private val roc = RandomOrgClient.getRandomOrgClient(
            settings.node("Keys").get("randomOrgKey", ""))

    init {
        rules.initializeRules()
    }

    /**
     * Uses access to RandomOrg API to generate fully random dice roll
     */
    fun roll(): MutableList<Int> {
        return roc.generateIntegers(5, 1, 6).toMutableList()
    }
}