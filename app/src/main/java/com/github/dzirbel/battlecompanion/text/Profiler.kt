package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.CasualtyPicker
import com.github.dzirbel.battlecompanion.core.UnitType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
private val log = StringBuilder()

private const val N = 1_000_000
private const val printArmies = true
private const val saveLog = true
private val runFilenameDateFormat = SimpleDateFormat("yyyy-MM-dd_kk-mm-ss", Locale.US)

fun main() {
    val start = System.nanoTime()

    if (printArmies) {
        logln("Armies:")
        armies.forEach { name, army ->
            logln("  $name")
            army.forEach { (unitType, count) ->
                logln("    $count of ${unitType.prettyName}")
            }
            logln()
        }
        logln()
    }

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

        logln("Score: ${averageScore.format()}")
        logln()
        scores.add(averageScore)
    }

    val total = scores.average().toInt()
    val totalDuration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start)

    logln()
    logln("Done in $totalDuration seconds!")
    logln("Overall profiling score: ${total.format()} (lower is better)")
    armies.toSortedMap().forEach { (name, _) ->
        logln("  ${name.capitalize()} battle : ${armyScores[name]?.average()?.toInt()}")
    }

    if (saveLog) {
        saveLog()
    }
}

private fun profileBattle(units: Map<UnitType, Int>, name: String): Long {
    log("Profiling $name battle with ${N.format()} runs...")
    val start = System.nanoTime()

    val startingBoard = Board(
        attackers = Army.fromMap(units = units, casualtyPicker = attackerCasualtyPicker),
        defenders = Army.fromMap(units = units, casualtyPicker = defenderCasualtyPicker)
    )
    repeat(N) {
        var board = startingBoard
        while (board.outcome == null) {
            board = board.roll(rand)
        }
    }
    val duration = System.nanoTime() - start
    logln(" done in ${TimeUnit.NANOSECONDS.toMillis(duration)}ms.")

    return duration / N
}

private fun log(message: String = "") {
    print(message)
    log.append(message)
}

private fun logln(message: String = "") {
    println(message)
    log.appendln(message)
}

private fun saveLog() {
    val dir = File("profiler-runs")
    dir.mkdir()

    val logFile = dir.resolve("run_${runFilenameDateFormat.format(Date())}.txt")
    logFile.writeText(log.toString())

    println()
    println("Run saved as ${logFile.relativeTo(File("")).path}")
}
