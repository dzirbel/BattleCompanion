package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (a map from each [UnitType] to a list of the hp's of each unit of
 *  that type; zero is not allowed) and [unitPriority] which determines which units to lose as casualties first.
 * [Army] has no knowledge of its state in the combat sequence (e.g. whether it is performing opening fire).
 * Note that [Army]s are immutable.
 *
 * TODO guarantee that [units] has no [UnitType]s with no units (i.e. empty hp list) and remove [isEmpty]
 * TODO allow [unitPriority] to be more generic to support user input (i.e. just return a list of casualties from hits)
 * TODO add default unit priorities that first compare on cost and then on attack/defense and vice versa
 * TODO try to instantiate all instances of [units] as EnumMaps for performance?
 */
data class Army(
    val units: Map<UnitType, List<Int>>,
    val unitPriority: Comparator<UnitType>
) {

    companion object {

        /**
         * Returns an [Army] with the given [unitPriority] and [units] as a map from the [UnitType] to the number of
         *  units, all initialized to their respective [UnitType.maxHp].
         */
        fun fromMap(unitPriority: Comparator<UnitType>, units: Map<UnitType, Int>): Army {
            return Army(
                units = units.mapValues { (unitType, count) -> List(count) { unitType.maxHp } },
                unitPriority = unitPriority
            )
        }
    }

    /**
     * Computes the total number of units of the given [UnitType], at all health levels.
     */
    fun countOfType(unitType: UnitType): Int {
        return units[unitType]?.count() ?: 0
    }

    fun isEmpty(): Boolean {
        return units.all { it.value.isEmpty() }
    }

    /**
     * Returns a copy of this [Army] without units that only fire during the opening round, as per
     *  [UnitType.firstRoundOnly].
     */
    fun withoutFirstRoundOnlyUnits(): Army {
        return copy(units = units.filterKeys { !it.firstRoundOnly })
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

        units.forEach { (unitType, hpList) ->
            var remainingCount = hpList.count()
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
            units = units
                // first take hits on all units that have more than 1 hp
                .mapValues { (unitType, hpList) ->
                    if (unitType == UnitType.ANTIAIRCRAFT_GUN || (domain != null && unitType.domain != domain)) {
                        hpList
                    } else {
                        hpList.map { hp ->
                            var remainingHp = hp
                            // TODO use min here rather than a loop like below
                            while (remainingHp > 1) {
                                remainingHp--
                                remainingHits--
                            }
                            remainingHp
                        }
                    }
                }
                .toSortedMap(unitPriority)  // TODO always have units sorted by unitPriority?
                .mapValues { (unitType, hpList) ->
                    // TODO prevent bombarding battleships from being casualties and generalize
                    if (unitType == UnitType.ANTIAIRCRAFT_GUN || (domain != null && unitType.domain != domain)) {
                        hpList
                    } else {
                        // TODO assert that hpList only has 1s?
                        val casualties = Math.min(hpList.size, remainingHits)
                        remainingHits -= casualties
                        hpList.drop(casualties)
                    }
                }
                .filterValues { it.isNotEmpty() }
        )
    }
}
