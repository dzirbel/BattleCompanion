package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.UnitType
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.random.Random

private val attackers = Army(
    units = mapOf(
        UnitType.TANK to 1
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private val defenders = Army(
    units = mapOf(
        UnitType.TANK to 1
    ),
    unitPriority = Comparator { u1, u2 -> u1.cost.compareTo(u2.cost) }
)

private const val N = 100_000

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
    println("Running $N battles for board:")
    startingBoard.print()
    println()

    repeat(N) {
        var board = startingBoard
        while (board.result == null) {
            board = board.roll(rand)
        }
        val result = board.result ?: throw IllegalStateException()
        when (result) {
            is Board.Result.AttackersWon -> wins++
            is Board.Result.DefendersWon -> losses++
            is Board.Result.Tie -> ties++
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