package com.github.dzirbel.battlecompanion.core

/**
 * The outcome of a combat.
 * [Outcome]s are typically nullable with null denoting a combat that is still in progress.
 */
sealed class Outcome {

    object Tie : Outcome()
    class AttackersWon(val remaining: Army) : Outcome()
    class DefendersWon(val remaining: Army) : Outcome()
}
