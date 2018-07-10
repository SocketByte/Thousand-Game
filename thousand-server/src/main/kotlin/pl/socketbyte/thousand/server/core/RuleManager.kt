package pl.socketbyte.thousand.server.core

import pl.socketbyte.thousand.server.core.data.Choice
import pl.socketbyte.thousand.server.core.data.Round
import pl.socketbyte.thousand.server.core.data.Rule

/**
 * Class for managing rules and checking user roll for points
 */
class RuleManager {

    val rules = mutableListOf<Rule>()

    /**
     * Initializes all possible game rules
     *
     * Warning: Order is important! 3/4/5 * X dice rolls
     * are prioritized over single 1 or 5 rolls.
     */
    fun initializeRules() {
        rules.add(Rule(3, 1, 100))
        rules.add(Rule(4, 1, 200))
        rules.add(Rule(5, 1, 400))

        rules.add(Rule(5, 2, 80))
        rules.add(Rule(4, 2, 40))
        rules.add(Rule(3, 2, 20))

        rules.add(Rule(5, 3, 120))
        rules.add(Rule(4, 3, 60))
        rules.add(Rule(3, 3, 30))

        rules.add(Rule(5, 4, 160))
        rules.add(Rule(4, 4, 80))
        rules.add(Rule(3, 4, 40))

        rules.add(Rule(5, 5, 200))
        rules.add(Rule(4, 5, 100))
        rules.add(Rule(3, 5, 50))

        rules.add(Rule(5, 6, 240))
        rules.add(Rule(4, 6, 120))
        rules.add(Rule(3, 6, 60))

        rules.add(Rule(1, 1, 10))
        rules.add(Rule(1, 5, 5))
    }

    /**
     * Main method for calculating and generating user round (his points and available options)
     */
    fun calculateRound(rollOriginal: MutableList<Int>): Round {
        var rolled = mutableListOf(*rollOriginal.toTypedArray())
        var points = 0
        if (rolled.containsAll(arrayListOf(1, 2, 3, 4, 5)))
            return Round(100, emptyList())
        else if (rolled.containsAll(arrayListOf(2, 3, 4, 5, 6)))
            return Round(100, emptyList())

        val choices = mutableListOf<Choice>()
        for (rule in rules) {
            if (containsRule(rolled, rule)) {
                val amount = getAmount(rolled, rule)
                val modifiedPoints = rule.points * amount

                rolled = removePriorityRule(rolled, rule)
                if (getAmount(rolled, rule) == 0) {
                    if ((rule.amount == 1 && rule.dice == 1) || (rule.amount == 1 && rule.dice == 5)) {
                        points += modifiedPoints

                        choices.add(Choice(amount, rule.dice, modifiedPoints))
                        continue
                    }
                    points += rule.points
                    choices.add(Choice(amount, rule.dice, rule.points))
                    continue
                }
                points += modifiedPoints
                choices.add(Choice(amount, rule.dice, modifiedPoints))
            }
        }

        return Round(points, choices)
    }

    private fun containsRule(rolled: MutableList<Int>, rule: Rule): Boolean {
        return getAmount(rolled, rule) >= rule.amount
    }

    private fun removePriorityRule(rolled: MutableList<Int>, rule: Rule): MutableList<Int> {
        val copy = mutableListOf(*rolled.toTypedArray())

        while (getIndexes(copy, rule).isNotEmpty()) {
            if (copy.isEmpty())
                return copy
            copy.removeAt(getIndexes(copy, rule)[0])
        }
        return copy
    }

    private fun getIndexes(rolled: MutableList<Int>, rule: Rule): List<Int> {
        val indexes = mutableListOf<Int>()
        for ((index, value) in rolled.withIndex())
            if (value == rule.dice) {
                indexes.add(index)
            }
        return indexes
    }

    private fun getAmount(rolled: MutableList<Int>, rule: Rule): Int {
        var amount = 0
        for (value in rolled)
            if (value == rule.dice) {
                amount++
            }
        return amount
    }
}