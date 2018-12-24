package com.github.dzirbel.battlecompanion.core

/**
 * The outcome of a combat.
 * [Outcome]s are typically nullable with null denoting a combat that is still in progress (i.e. has no [Outcome] yet).
 */
sealed class Outcome {

    object Tie : Outcome()
    class AttackerWon(val remaining: Army) : Outcome()
    class DefenderWon(val remaining: Army) : Outcome()
}
