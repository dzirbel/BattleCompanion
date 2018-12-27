package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.CasualtyPicker
import com.github.dzirbel.battlecompanion.core.UnitType
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val attackerCasualtyPicker = CasualtyPicker.ByCost(isAttacking = true)
private val defenderCasualtyPicker = CasualtyPicker.ByCost(isAttacking = false)

private val smallArmy = mapOf(
    UnitType.INFANTRY to 1,
    UnitType.TANK to 1
)

private val mediumArmy = mapOf(
    UnitType.INFANTRY to 4,
    UnitType.ARTILLERY to 1,
    UnitType.TANK to 2,
    UnitType.FIGHTER to 1
)

private val largeArmy = mapOf(
    UnitType.INFANTRY to 12,
    UnitType.ARTILLERY to 5,
    UnitType.TANK to 8,
    UnitType.ANTIAIRCRAFT_GUN to 1,
    UnitType.FIGHTER to 7,
    UnitType.BOMBER to 2
)

private val seaArmy = mapOf(
    UnitType.BATTLESHIP to 1,
    UnitType.DESTROYER to 1,
    UnitType.AIRCRAFT_CARRIER to 1,
    UnitType.FIGHTER to 3,
    UnitType.SUBMARINE to 2
)

private val armies = mapOf(
    "small" to smallArmy,
    "medium" to mediumArmy,
    "large" to largeArmy,
    "sea" to seaArmy
)

private val rand = Random(42)
private const val N = 1_000_000

// TODO output profiling results to a text file
fun main() {
    val start = System.nanoTime()
    val scores = mutableListOf<Int>()
    val armyScores = mutableMapOf<String, List<Int>>()

    repeat(3) {
        val averageScore = armies
            .toList()
            .shuffled()
            .map { (name, army) ->
                profileBattle(army, name).also { score ->
                    armyScores[name] = (armyScores[name] ?: emptyList()).plus(score.toInt())
                }
            }
            .average()
            .toInt()

        println("Score: ${averageScore.format()}")
        println()
        scores.add(averageScore)
    }

    val total = scores.average().toInt()
    val totalDuration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start)
    println()
    println("Done in $totalDuration seconds!")
    println("Overall profiling score: ${total.format()} (lower is better)")
    armies.toSortedMap().forEach { (name, _) ->
        println("  ${name.capitalize()} battle : ${armyScores[name]?.average()?.toInt()}")
    }
}

private fun profileBattle(units: Map<UnitType, Int>, name: String): Long {
    print("Profiling $name battle with ${N.format()} runs...")
    val start = System.nanoTime()

    val startingBoard = Board(
        attackers = Army.fromMap(units = units, casualtyPicker = attackerCasualtyPicker),
        defenders = Army.fromMap(units = units, casualtyPicker = defenderCasualtyPicker)
    )
    repeat(N) {
        var board = startingBoard
        var firstRound = true
        while (board.getOutcome() == null) {
            board = board.roll(rand)
            if (firstRound) {
                board = board.withoutFirstRoundOnlyUnits()
                firstRound = false
            }
        }
    }
    val duration = System.nanoTime() - start
    println(" done in ${TimeUnit.NANOSECONDS.toMillis(duration)}ms.")

    return duration / N
}
