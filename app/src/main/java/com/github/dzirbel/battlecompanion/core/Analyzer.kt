package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

/**
 * Runs exact analysis of a [Board] to determine the distribution of [Outcome]s.
 *
 * TODO tests
 */
object Analyzer {

    private val memo = mutableMapOf<Board, Map<Outcome, Rational>>()

    /**
     * Determines the distribution of [Outcome]s for the given [Board] as a map from each possible
     *  [Outcome] to its probability.
     */
    fun analyze(board: Board): Map<Outcome, Rational> {
        memo[board]?.let { return it }

        board.outcome?.let { outcome ->
            return mapOf(outcome to Rational.ONE).also { memo[board] = it }
        }

        val attackerOpeningFireHitDistribution = board.attackers.getHitDistribution(
            enemies = board.defenders,
            isAttacking = true,
            isOpeningFire = true
        )
        val defenderOpeningFireHitDistribution = board.defenders.getHitDistribution(
            enemies = board.attackers,
            isAttacking = false,
            isOpeningFire = true
        )

        // consider all combinations of opening fire hits; flat map each to an outcome distribution
        //  and reduce, summing likelihoods for overlapping outcomes
        val outcomes = cross(
            attackerOpeningFireHitDistribution,
            defenderOpeningFireHitDistribution
        ).flatMapAndReduce(Rational::plus) { openingFireHitProfiles, openingFireChances ->
            val (attackerOpeningFireHits, defenderOpeningFireHits) = openingFireHitProfiles
            val openingFireChance = openingFireChances.first * openingFireChances.second
            val openingFireIsEmpty = attackerOpeningFireHits.isEmpty() &&
                    defenderOpeningFireHits.isEmpty()

            val remainingAttackers = board.attackers.takeHits(defenderOpeningFireHits)
            val remainingDefenders = board.defenders.takeHits(attackerOpeningFireHits)

            val attackerHitDistribution = remainingAttackers.getHitDistribution(
                enemies = remainingDefenders,
                isAttacking = true,
                isOpeningFire = false
            )
            val defenderHitDistribution = remainingDefenders.getHitDistribution(
                enemies = remainingAttackers,
                isAttacking = false,
                isOpeningFire = false
            )

            // similarly, consider all combinations of regular fire hits; flat map and reduce again
            cross(
                attackerHitDistribution,
                defenderHitDistribution
            ).flatMapAndReduce(Rational::plus) { hitProfiles, chances ->
                val (attackerHits, defenderHits) = hitProfiles

                if (openingFireIsEmpty && attackerHits.isEmpty() && defenderHits.isEmpty()) {
                    // if no hits were landed we have an identical board; to prevent infinite
                    //  recursion we simply skip this possibility and normalize the probabilities at
                    //  the end
                    emptyMap()
                } else {
                    val totalChance = openingFireChance * chances.first * chances.second

                    val afterHits = Board(
                        attackers = remainingAttackers
                            .takeHits(defenderHits)
                            .withoutFirstRoundOnlyUnits(),
                        defenders = remainingDefenders
                            .takeHits(attackerHits)
                            .withoutFirstRoundOnlyUnits()
                    )

                    analyze(afterHits).mapValues { (_, chance) -> chance * totalChance }
                }
            }
        }

        // normalize the probabilities since hit distributions with zero total hits were excluded
        val total = outcomes.values.reduce(Rational::plus)
        return outcomes.mapValues { it.value / total }.also { memo[board] = it }
    }
}
