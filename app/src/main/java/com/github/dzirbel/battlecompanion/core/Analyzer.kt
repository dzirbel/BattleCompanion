package com.github.dzirbel.battlecompanion.core

import android.util.Rational

object Analyzer {

    private val one = Rational(1, 1)

    private val memo = mutableMapOf<Board, Map<Outcome, Rational>>()

    fun analyze(board: Board): Map<Outcome, Rational> {
        memo[board]?.let { return it }

        board.getOutcome()?.let { outcome ->
            return mapOf(outcome to one).also { memo[board] = it }
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

        return cross(
            attackerOpeningFireHitDistribution,
            defenderOpeningFireHitDistribution
        ).flatMap { (openingFireHitProfiles, chances) ->
            val (attackerOpeningFireHits, defenderOpeningFireHits) = openingFireHitProfiles
            val openingFireChance = chances.first * chances.second

            val remainingAttackers = board.attackers.takeHits(defenderOpeningFireHits)
            val remainingDefenders = board.defenders.takeHits(attackerOpeningFireHits)

            val attackerHitDistribution = remainingAttackers.getHitDistribution(
                enemies = board.defenders,
                isAttacking = true,
                isOpeningFire = false
            )
            val defenderHitDistribution = remainingDefenders.getHitDistribution(
                enemies = board.attackers,
                isAttacking = false,
                isOpeningFire = false
            )

            cross(
                attackerHitDistribution,
                defenderHitDistribution
            ).flatMap { (hitProfiles, chances) ->
                val (attackerHits, defenderHits) = hitProfiles
                val totalChance = openingFireChance * chances.first * chances.second

                val afterHits = Board(
                    attackers = remainingAttackers.takeHits(defenderHits),
                    defenders = remainingDefenders.takeHits(attackerHits)
                )

                // TODO any way to keep this as a map and avoid the flatMaps?
                analyze(afterHits).map { (outcome, chance) -> outcome to chance * totalChance }
            }
        }
            .toMap()
            .also { memo[board] = it }
    }
}
