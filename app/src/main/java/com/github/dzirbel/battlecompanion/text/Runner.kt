package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.Outcome
import com.github.dzirbel.battlecompanion.core.UnitType
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.random.Random

private val attackers = Army(
    units = mapOf(
        UnitType.INFANTRY to 3,
        UnitType.TANK to 1
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private val defenders = Army(
    units = mapOf(
        UnitType.INFANTRY to 4
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private const val N = 100_000
private const val PRINT_EACH_ROUND = false
private const val PRINT_REMAINING = false

fun main() {
    val rand = Random

    var wins = 0
    var losses = 0
    var ties = 0

    val start = System.nanoTime()

    val startingBoard = Board(
        attackers = attackers,
        defenders = defenders
    )
    println("Running $N simulations of battle:")
    println()
    startingBoard.print()
    println()

    repeat(N) {
        var board = startingBoard
        var round = 1
        while (board.outcome == null) {
            if (PRINT_EACH_ROUND) {
                println("Round $round:")
                board.print()
                println()
                round++
            }

            board = board.roll(rand)
        }

        if (PRINT_REMAINING) {
            val outcome = board.outcome
            when (outcome) {
                is Outcome.AttackersWon -> {
                    println("Attackers won!")
                    println("Remaining units:")
                    outcome.remaining.print()
                }
                is Outcome.DefendersWon -> {
                    println("Defenders won!")
                    println("Remaining units:")
                    outcome.remaining.print()
                }
                is Outcome.Tie -> println("Tie! (all units dead)")
            }
            println()
        }

        when (board.outcome) {
            is Outcome.AttackersWon -> wins++
            is Outcome.DefendersWon -> losses++
            is Outcome.Tie -> ties++
        }
    }

    val chars = Math.floor(log10(N.toDouble())).toInt() + 1
    val percentWin = wins.toDouble() / N
    val percentLosses = losses.toDouble() / N
    val percentTies = ties.toDouble() / N

    println("Wins:   ${wins.format(chars)} (${percentWin.formatPercent()})")
    println("Losses: ${losses.format(chars)} (${percentLosses.formatPercent()})")
    println("Ties:   ${ties.format(chars)} (${percentTies.formatPercent()})")

    val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
    println()
    println("Took ${duration}ms")
}
