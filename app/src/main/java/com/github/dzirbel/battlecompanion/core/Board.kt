package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents the state of the combat board at a specific time, which simply contains the [attackers] and [defenders].
 * Note that since [Army] is immutable, [Board] is as well.
 * TODO submarine submerging
 * TODO attackers retreating
 * TODO listener system
 */
data class Board(
    val attackers: Army,
    val defenders: Army
) {

    fun getOutcome(): Outcome? {
        return when {
            attackers.isEmpty() && defenders.isEmpty() -> Outcome.Tie
            attackers.isEmpty() && !defenders.isEmpty() -> Outcome.DefenderWon(defenders)
            !attackers.isEmpty() && defenders.isEmpty() -> Outcome.AttackerWon(attackers)
            else -> null
        }
    }

    fun roll(rand: Random): Board {
        var remainingAttackers = attackers
        var remainingDefenders = defenders

        // TODO subs opening fire (if no destroyer, then remove them from main combat, etc)

        if (remainingDefenders.units.any { it.key == UnitType.ANTIAIRCRAFT_GUN } &&
            remainingAttackers.units.any { it.key.domain == Domain.AIR }
        ) {
            // TODO this technically allows multiple aa guns
            val aaHits = remainingDefenders.ofType(UnitType.ANTIAIRCRAFT_GUN).rollHits(rand, isAttacking = false)
            remainingAttackers = remainingAttackers.takeHits(aaHits)
        }
        remainingDefenders = remainingDefenders.withoutType(UnitType.ANTIAIRCRAFT_GUN)

        if (remainingAttackers.units.any { it.key == UnitType.BATTLESHIP } &&
            remainingDefenders.units.any { it.key.domain == Domain.LAND }
        ) {
            // TODO bombardment can only hit land units
            val bombardmentHits = remainingAttackers.ofType(UnitType.BATTLESHIP).rollHits(rand, isAttacking = true)
            remainingDefenders = remainingDefenders.takeHits(bombardmentHits)
            remainingAttackers = remainingAttackers.withoutType(UnitType.BATTLESHIP)
        }

        val attackerHits = remainingAttackers.rollHits(rand, isAttacking = true)
        val defenderHits = remainingDefenders.rollHits(rand, isAttacking = false)

        remainingAttackers = remainingAttackers.takeHits(defenderHits)
        remainingDefenders = remainingDefenders.takeHits(attackerHits)

        return Board(
            attackers = remainingAttackers,
            defenders = remainingDefenders
        )
    }
}
