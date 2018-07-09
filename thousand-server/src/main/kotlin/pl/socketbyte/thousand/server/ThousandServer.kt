package pl.socketbyte.thousand.server

import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.shared.settings.Settings

val settings = Settings.load("config.ini")
val game = Game()

/**
 * Main class of Thousand server
 */
fun main(args: Array<String>) {
    val roll = game.roll()
    val round = game.rules.calculateRound(roll)

    // Test rolls :D
    // Example output:
    //  Roll: [2, 1, 5, 5, 3]
    //
    //  Player points: 20  <- total points
    //  Player choices: 2
    //  Choices:
    //  1. 1 x 1 = 10
    //  2. 2 x 5 = 10
    println("Roll: " + roll.toString())
    println()
    println("Player points: " + round.points)
    println("Player choices: " + round.choices.size)
    println("Choices:")
    for ((index, choice) in round.choices.withIndex()) {
        println("${index+1}. ${choice.amount} x ${choice.dice} = ${choice.points}")
    }
}