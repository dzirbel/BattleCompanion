package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

object Analyzer {

    private val memo = mutableMapOf<Board, Map<Outcome, Rational>>()

    fun analyze(board: Board, recursions: Int = 0): Map<Outcome, Rational> {
        memo[board]?.let { return it }

        board.getOutcome()?.let { outcome ->
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

        val outcomes = cross(
            attackerOpeningFireHitDistribution,
            defenderOpeningFireHitDistribution
        ).flatMapAndReduce(Rational::plus) { openingFireHitProfiles, openingFireChances ->
            val (attackerOpeningFireHits, defenderOpeningFireHits) = openingFireHitProfiles
            val openingFireChance = openingFireChances.first * openingFireChances.second

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

            cross(
                attackerHitDistribution,
                defenderHitDistribution
            ).flatMapAndReduce(Rational::plus) { hitProfiles, chances ->
                val (attackerHits, defenderHits) = hitProfiles

                if (attackerHits.isEmpty() && defenderHits.isEmpty() &&
                    attackerOpeningFireHits.isEmpty() && defenderOpeningFireHits.isEmpty()
                ) {
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

                    analyze(afterHits, recursions = recursions + 1)
                        .mapValues { (_, chance) -> chance * totalChance }
                }
            }
        }

        val total = outcomes.values.reduce(Rational::plus)
        return outcomes.mapValues { it.value / total }.also { memo[board] = it }
    }
}
