package com.github.dzirbel.battlecompanion.core

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
        attackers.totalUnits == 0 && defenders.totalUnits > 0 -> Result.DefendersWon(defenders)
        else -> Result.Tie
    }

    fun roll(rand: Random): Board {
        if (result != null) {
            throw IllegalStateException()
        }

        var remainingAttackers = attackers
        var remainingDefenders = defenders

        // TODO subs opening fire

        if (remainingAttackers.units.any { it.key.terrain == UnitTerrain.AIR } &&
            remainingDefenders.units.any { it.key == UnitType.ANTIAIRCRAFT_GUN }
        ) {
            // TODO this allows multiple aa guns
            val aaHits = remainingDefenders.ofType(UnitType.ANTIAIRCRAFT_GUN).computeHits(rand, isAttacking = false)
            remainingAttackers = remainingAttackers.takeHits(aaHits)
            remainingDefenders = remainingDefenders.withoutType(UnitType.ANTIAIRCRAFT_GUN)
        }

        if (remainingAttackers.units.any { it.key == UnitType.BATTLESHIP } &&
            remainingDefenders.units.any { it.key.terrain == UnitTerrain.LAND }
        ) {
            val bombardmentHits = remainingAttackers.ofType(UnitType.BATTLESHIP).computeHits(rand, isAttacking = true)
            remainingDefenders = remainingDefenders.takeHits(bombardmentHits)
            remainingAttackers = remainingAttackers.withoutType(UnitType.BATTLESHIP)
        }

        val attackerHits = remainingAttackers.computeHits(rand, isAttacking = true)
        val defenderHits = remainingDefenders.computeHits(rand, isAttacking = false)

        remainingAttackers = remainingAttackers.takeHits(defenderHits)
        remainingDefenders = remainingDefenders.takeHits(attackerHits)

        return Board(
            attackers = remainingAttackers,
            defenders = remainingDefenders
        )
    }
}
