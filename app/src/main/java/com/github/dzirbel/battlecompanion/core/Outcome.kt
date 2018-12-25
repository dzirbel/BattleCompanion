package com.github.dzirbel.battlecompanion.core

/**
 * The outcome of a battle.
 * [Outcome]s are typically nullable with null denoting a battle that is still in progress (i.e. has no [Outcome] yet).
 */
sealed class Outcome {

    object Tie : Outcome()
    class AttackerWon(val remaining: Army) : Outcome()
    class DefenderWon(val remaining: Army) : Outcome()
}
