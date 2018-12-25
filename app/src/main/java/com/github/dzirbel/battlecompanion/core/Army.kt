package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (as a map from each [UnitType] to the number of those units in the
 *  army) and [unitPriority] which determines which units to lose as casualties first.
 * [Army] has no knowledge of its state in the combat sequence (e.g. whether it is performing opening fire).
 * Note that [Army]s are immutable.
 *
 * TODO allow [unitPriority] to be more generic to support user input (i.e. just return a list of casualties from hits)
 * TODO add default unit priorities that first compare on cost and then on attack/defense and vice versa
 * TODO try to instantiate all instances of [units] as EnumMaps for performance?
 */
data class Army(
    val units: Map<UnitType, Int>,
    val unitPriority: Comparator<UnitType>
) {

    fun isEmpty(): Boolean {
        return units.none { it.value > 0 }
    }

    /**
     * Returns a copy of this [Army] without units of the given [UnitType].
     */
    fun withoutType(unitType: UnitType): Army {
        return copy(units = units.filterKeys { it != unitType })
    }

    /**
     * Returns a copy of this [Army] including only the [UnitType]s which have opening fire on the given [Board].
     */
    fun withOpeningFire(board: Board, isAttacking: Boolean): Army {
        return copy(units = units.filterKeys { it.hasOpeningFire(board = board, isAttacking = isAttacking) })
    }

    /**
     * Returns a copy of this [Army] including only the [UnitType]s which do not have opening fire on the given [Board].
     */
    fun withoutOpeningFire(board: Board, isAttacking: Boolean): Army {
        return copy(units = units.filterKeys { !it.hasOpeningFire(board = board, isAttacking = isAttacking) })
    }

    /**
     * Computes the [HitProfile] that this [Army] inflicts in a single round, rolling with the given [Random].
     */
    fun rollHits(rand: Random, isAttacking: Boolean): HitProfile {
        var hits: HitProfile = mapOf()
        val supportingArtillery = if (isAttacking) units.count { it.key == UnitType.ARTILLERY } else 0

        units.forEach { (unitType, count) ->
            var remainingCount = count
            if (unitType == UnitType.INFANTRY && supportingArtillery > 0) {
                val supportedInfantry = Math.min(remainingCount, supportingArtillery)
                remainingCount -= supportedInfantry

                // TODO replace the constant 2
                hits = hits.plusHits(unitType.targetDomain, rand.rollDice(supportedInfantry).count { it <= 2 })
            }

            val rollLimit = if (isAttacking) unitType.attack else unitType.defense
            hits = hits.plusHits(unitType.targetDomain, rand.rollDice(remainingCount).count { it <= rollLimit })
        }

        return hits
    }

    /**
     * Returns a copy of this [Army] with the given [HitProfile] inflicted.
     * TODO doing this all in one step might be better for performance/cleaner
     */
    fun takeHits(hits: HitProfile): Army {
        var remainingArmy = this
        hits.filterKeys { it != null }.forEach { (domain, count) ->
            remainingArmy = remainingArmy.takeHits(count, domain)
        }

        remainingArmy = remainingArmy.takeHits(hits[null] ?: 0)

        return remainingArmy
    }

    private fun takeHits(hits: Int, domain: Domain? = null): Army {
        if (hits == 0) {
            return this
        }

        var remainingHits = hits
        return copy(
            units = units.keys
                .sortedWith(unitPriority)   // TODO keep the units sorted by unitPriority always?
                .map { unitType: UnitType ->
                    val count = units[unitType] ?: throw IllegalStateException()
                    val casualties = Math.min(count, remainingHits).takeIf {
                        // TODO generalize immune units?
                        unitType != UnitType.ANTIAIRCRAFT_GUN && (domain == null || unitType.domain == domain)
                    } ?: 0
                    remainingHits -= casualties
                    Pair(unitType, count - casualties)
                }
                .toMap()
                .filter { it.value > 0 }
        )
    }
}
