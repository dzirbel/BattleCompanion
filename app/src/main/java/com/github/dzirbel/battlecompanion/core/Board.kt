package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents the state of the combat board at a specific time, which simply contains the [attackers] and [defenders].
 * Note that [Board]s are immutable.
 */
data class Board(
    val attackers: Army,
    val defenders: Army
) {

    val outcome: Outcome? by lazy {
        when {
            attackers.totalUnits > 0 && defenders.totalUnits > 0 -> null
            attackers.totalUnits > 0 && defenders.totalUnits == 0 -> Outcome.AttackersWon(attackers)
            attackers.totalUnits == 0 && defenders.totalUnits > 0 -> Outcome.DefendersWon(defenders)
            else -> Outcome.Tie
        }
    }

    fun roll(rand: Random): Board {
        if (outcome != null) {
            throw IllegalStateException()
        }

        var remainingAttackers = attackers
        var remainingDefenders = defenders

        // TODO subs opening fire (if no destroyer, then remove them from main combat, etc)

        if (remainingAttackers.units.any { it.key.terrain == UnitTerrain.AIR } &&
            remainingDefenders.units.any { it.key == UnitType.ANTIAIRCRAFT_GUN }
        ) {
            // TODO this technically allows multiple aa guns
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
