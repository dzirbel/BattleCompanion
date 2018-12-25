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
        UnitType.INFANTRY to 1,
        UnitType.TANK to 1
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private val defenders = Army(
    units = mapOf(
        UnitType.INFANTRY to 2
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private const val N = 100_000
private const val PRINT_BOARD_FREQUENCIES = true
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

    val finalBoards = mutableMapOf<Board, Int>()

    repeat(N) {
        var board = startingBoard
        var round = 1
        while (board.getOutcome() == null) {
            if (PRINT_EACH_ROUND) {
                println("Round $round:")
                board.print()
                println()
                round++
            }

            board = board.roll(rand)
        }

        if (PRINT_REMAINING) {
            val outcome = board.getOutcome()
            when (outcome) {
                is Outcome.AttackerWon -> {
                    println("Attacker won! Remaining units:")
                    outcome.remaining.print()
                }
                is Outcome.DefenderWon -> {
                    println("Defender won! Remaining units:")
                    outcome.remaining.print()
                }
                is Outcome.Tie -> println("Tie! (all units dead)")
            }
            println()
        }

        if (PRINT_BOARD_FREQUENCIES) {
            val boardCount = finalBoards[board] ?: 0
            finalBoards[board] = boardCount + 1
        }

        when (board.getOutcome()) {
            is Outcome.AttackerWon -> wins++
            is Outcome.DefenderWon -> losses++
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
    println()

    if (PRINT_BOARD_FREQUENCIES) {
        println("Resulting board frequencies:")
        finalBoards.entries.sortedByDescending { it.value }.forEach { (board, count) ->
            println("${(count.toDouble() / N).formatPercent()}:")
            board.print()
            println()
        }
    }

    val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
    println("Took ${duration}ms")
}
