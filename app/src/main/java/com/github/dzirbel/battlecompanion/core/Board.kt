package com.github.dzirbel.battlecompanion.core

import java.lang.IllegalStateException
import kotlin.random.Random

data class Board(
    val attackers: Army,
    val defenders: Army
) {

    sealed class Result {

        object Tie : Result()
        class AttackersWon(val remaining: Army) : Result()
        class DefendersWon(val remaining: Army) : Result()
    }

    val result = when {
        attackers.totalUnits > 0 && defenders.totalUnits > 0 -> null
        attackers.totalUnits > 0 && defenders.totalUnits == 0 -> Result.AttackersWon(attackers)
        defenders.totalUnits > 0 && attackers.totalUnits == 0 -> Result.DefendersWon(defenders)
        else -> Result.Tie
    }

    fun roll(rand: Random): Board {
        if (result != null) {
            throw IllegalStateException()
        }

        var remainingAttackers = attackers
        var remainingDefenders = defenders

        // TODO subs opening fire

        val attackingPlanes = remainingAttackers.units.count { it.key.terrain == UnitTerrain.AIR }
        if (attackingPlanes > 0 && remainingDefenders.units.any { it.key == UnitType.ANTIAIRCRAFT_GUN }) {
            val aaHits = rand.rollDice(attackingPlanes).count { it <= 1 }
            remainingAttackers = remainingAttackers.takeHits(
                hits = aaHits,
                filter = { it.terrain == UnitTerrain.AIR }
            )
            remainingDefenders = remainingDefenders.filter { it != UnitType.ANTIAIRCRAFT_GUN }
        }

        val attackingBattleships = remainingAttackers.units.count { it.key == UnitType.BATTLESHIP }
        if (attackingBattleships > 0 && remainingDefenders.units.any { it.key.terrain == UnitTerrain.LAND }) {
            val bombardmentHits = rand.rollDice(attackingBattleships).count { it <= UnitType.BATTLESHIP.attack }
            remainingDefenders = remainingDefenders.takeHits(bombardmentHits)
            remainingAttackers = remainingAttackers.filter { it != UnitType.BATTLESHIP }
        }

        val attackerHits = remainingAttackers.units.map { (unit, count) ->
            rand.rollDice(count).count { it <= unit.attack }
        }.sum()

        val defenderHits = remainingDefenders.units.map { (unit, count) ->
            rand.rollDice(count).count { it <= unit.defense }
        }.sum()

        remainingAttackers = remainingAttackers.takeHits(defenderHits)
        remainingDefenders = remainingDefenders.takeHits(attackerHits)

        return Board(
            attackers = remainingAttackers,
            defenders = remainingDefenders
        )
    }
}