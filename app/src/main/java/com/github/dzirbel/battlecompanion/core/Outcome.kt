package com.github.dzirbel.battlecompanion.core

/**
 * The outcome of a battle.
 * [Outcome]s are typically nullable with null denoting a battle that is still in progress (i.e. has
 *  no [Outcome] yet).
 */
sealed class Outcome {

    object Tie : Outcome() {

        override fun toString() = "Tie"
    }

    data class AttackerWon(val remaining: Army) : Outcome() {

        override fun toString() = "Attackers win with ${remaining.units}"
    }

    data class DefenderWon(val remaining: Army) : Outcome() {

        override fun toString() = "Defenders win with ${remaining.units}"
    }
}
