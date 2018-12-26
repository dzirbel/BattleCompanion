package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents the state of the combat board at a specific time, which simply contains the [attackers] and [defenders].
 * Note that since [Army] is immutable, [Board] is as well.
 * TODO submarine submerging
 * TODO attackers retreating
 * TODO listener system
 * TODO validity check (maybe only used in tests?) that land units aren't against sea units, etc
 * TODO industrial complex bombing?
 */
data class Board(
    val attackers: Army,
    val defenders: Army
) {

    /**
     * Gets the outcome of the combat on this [Board], or null if the battle is not decided.
     */
    fun getOutcome(): Outcome? {
        return when {
            attackers.units.isEmpty() && defenders.units.isEmpty() -> Outcome.Tie
            attackers.units.isEmpty() && defenders.units.isNotEmpty() -> Outcome.DefenderWon(defenders)
            attackers.units.isNotEmpty() && defenders.units.isEmpty() -> Outcome.AttackerWon(attackers)
            else -> null
        }
    }

    /**
     * Conducts a round of combat (including both opening and regular fire) based on the rolls generated by the given
     *  [Random] and returns a [Board] with the result.
     * Units which only fire once (i.e. bombarding battleships and defending antiaircraft guns) are also removed from
     *  the returned [Board] after firing.
     */
    fun roll(rand: Random): Board {
        // TODO does this work? can first round only units take hits during non-opening fire?
        return runRound(rand, isOpeningFire = true).runRound(rand, isOpeningFire = false).withoutFirstRoundOnlyUnits()
    }

    /**
     * Returns a copy of this [Board] without [UnitType]s that should only fire during the first round of the battle,
     *  i.e. bombarding battleships and defending antiaircraft guns.
     */
    private fun withoutFirstRoundOnlyUnits(): Board {
        return Board(
            attackers = attackers.withoutFirstRoundOnlyUnits(),
            defenders = defenders.withoutFirstRoundOnlyUnits()
        )
    }

    /**
     * Runs a round of combat on this [Board] in which the attackers and defenders exchange hits based on the rolls
     *  generated by the given [Random] and returns a [Board] with the result.
     */
    private fun runRound(rand: Random, isOpeningFire: Boolean): Board {
        // note that both attacker and defender hits must be rolled before taking casualties on either side to ensure
        // they are done in parallel
        val attackerHits = attackers.rollHits(
            rand = rand,
            enemies = defenders,
            isAttacking = true,
            isOpeningFire = isOpeningFire
        )
        val defenderHits = defenders.rollHits(
            rand = rand,
            enemies = attackers,
            isAttacking = false,
            isOpeningFire = isOpeningFire
        )

        return Board(
            attackers = attackers.takeHits(defenderHits),
            defenders = defenders.takeHits(attackerHits)
        )
    }
}
