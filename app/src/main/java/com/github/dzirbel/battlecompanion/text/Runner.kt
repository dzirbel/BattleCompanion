package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Analyzer
import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.CasualtyPicker
import com.github.dzirbel.battlecompanion.core.Outcome
import com.github.dzirbel.battlecompanion.core.UnitType
import com.github.dzirbel.battlecompanion.util.Rational
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.random.Random

private val attackers = Army.fromMap(
    units = mapOf(
        UnitType.INFANTRY to 5,
        UnitType.TANK to 5,
        UnitType.BOMBER to 2,
        UnitType.BOMBARDING_BATTLESHIP to 1
    ),
    isAttacking = true,
    casualtyPicker = CasualtyPicker.ByCost(),
    weaponDevelopments = setOf()
)

private val defenders = Army.fromMap(
    units = mapOf(
        UnitType.INFANTRY to 10,
        UnitType.TANK to 3,
        UnitType.ANTIAIRCRAFT_GUN to 1
    ),
    isAttacking = false,
    casualtyPicker = CasualtyPicker.ByCost(),
    weaponDevelopments = setOf()
)

private const val N = 1_000_000

fun main() {
    println("Attackers:")
    attackers.units.forEach { unitType, hps ->
        println("  ${unitType.fullName} : ${hps.count()} | ${hps.toString { "${it}hp" }}")
    }

    println("Defenders:")
    defenders.units.forEach { unitType, hps ->
        println("  ${unitType.fullName} : ${hps.count()} | ${hps.toString { "${it}hp" }}")
    }

    val board = Board(
        attackers = attackers,
        defenders = defenders
    )

    runAnalysis(board)
    runSimulations(board)
}

private fun runAnalysis(startingBoard: Board) {
    println("Analyzing...")
    val start = System.nanoTime()
    val analysis = Analyzer.analyze(startingBoard)
    val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
    println("Done in ${duration}ms:")

    var wins = Rational.ZERO
    var losses = Rational.ZERO
    var ties = Rational.ZERO

    analysis.forEach { outcome, chance ->
        when (outcome) {
            is Outcome.AttackerWon -> wins += chance
            is Outcome.DefenderWon -> losses += chance
            is Outcome.Tie -> ties += chance
        }
    }

    println("  Wins:   ${wins.toDouble().formatPercent()} [$wins]")
    println("  Losses: ${losses.toDouble().formatPercent()} [$losses]")
    println("  Ties:   ${ties.toDouble().formatPercent()} [$ties]")

    println("Resulting board frequencies:")
    analysis.entries.sortedByDescending { it.value.toDouble() }.forEach { (outcome, chance) ->
        println("  ${chance.toDouble().formatPercent()} [$chance]: $outcome")
    }
    println()
}

private fun runSimulations(startingBoard: Board) {
    val rand = Random
    val outcomes = mutableMapOf<Outcome, Int>()

    println("Running ${N.format()} simulations...")
    val start = System.nanoTime()

    repeat(N) {
        var board = startingBoard
        while (board.outcome == null) {
            board = board.roll(rand)
        }

        board.outcome?.let { outcome -> outcomes[outcome] = (outcomes[outcome] ?: 0) + 1 }
    }

    val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
    println("Done in ${duration}ms:")

    val chars = Math.floor(log10(N.toDouble())).toInt() + 1
    val wins = outcomes.filterKeys { it is Outcome.AttackerWon }.values.sum()
    val losses = outcomes.filterKeys { it is Outcome.DefenderWon }.values.sum()
    val ties = outcomes.filterKeys { it is Outcome.Tie }.values.sum()

    println(
        "  Wins:   ${(wins.toDouble() / N).formatPercent()} " +
                "[${wins.format().padStart(chars)} / ${N.format()}]"
    )
    println(
        "  Losses: ${(losses.toDouble() / N).formatPercent()} " +
                "[${losses.format().padStart(chars)} / ${N.format()}]"
    )
    println(
        "  Ties:   ${(ties.toDouble() / N).formatPercent()} " +
                "[${ties.format().padStart(chars)} / ${N.format()}]"
    )

    println("Resulting board frequencies:")
    outcomes.entries.sortedByDescending { it.value }.forEach { (outcome, count) ->
        println(
            "  ${(count.toDouble() / N).formatPercent()} " +
                    "[${count.format().padStart(chars)} / ${N.format()}]: $outcome"
        )
    }
    println()
}
