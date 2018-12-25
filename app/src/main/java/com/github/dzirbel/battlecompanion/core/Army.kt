package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (a map from each [UnitType] to a list of the hp's of each unit of
 *  that type; zero is not allowed) and [unitPriority] which determines which units to lose as casualties first.
 * [Army] has no knowledge of its state in the combat sequence (e.g. whether it is performing opening fire).
 * Note that [Army]s are immutable.
 *
 * TODO guarantee that the hp list stays sorted so that identical boards have identical representations
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
     * Returns the total number of [UnitType]s (of any hp) in this [Army] satisfying the given [predicate].
     */
    fun countBy(predicate: (UnitType) -> Boolean): Int {
        return units.filterKeys(predicate).values.sumBy { it.size }
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
     * Computes the [HitProfile] that this [Army] inflicts in a single round, rolling with the given [Random].
     */
    fun rollHits(rand: Random, enemies: Army, isAttacking: Boolean, isOpeningFire: Boolean): HitProfile {
        var hits: HitProfile = mapOf()
        val supportingArtillery = if (isAttacking) units.count { it.key == UnitType.ARTILLERY } else 0

        units
            .filterKeys { it.hasOpeningFire(enemies = enemies) == isOpeningFire }
            .forEach { (unitType, hpList) ->
                var remainingCount = hpList.count()
                if (unitType == UnitType.INFANTRY && supportingArtillery > 0) {
                    val supportedInfantry = Math.min(remainingCount, supportingArtillery)
                    remainingCount -= supportedInfantry

                    // TODO replace the constant 2
                    val rollLimit = 2
                    val rolls = supportedInfantry * unitType.numberOfRolls(enemies = enemies)
                    hits = hits.plusHits(unitType.targetDomain, rand.rollDice(rolls).count { it <= rollLimit })
                }

                // TODO generalize to a function?
                val rollLimit = if (isAttacking) unitType.attack else unitType.defense
                val rolls = remainingCount * unitType.numberOfRolls(enemies = enemies)
                hits = hits.plusHits(unitType.targetDomain, rand.rollDice(rolls).count { it <= rollLimit })
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
                    when {
                        unitType == UnitType.ANTIAIRCRAFT_GUN || unitType == UnitType.BOMBARDING_BATTLESHIP -> hpList
                        domain != null && unitType.domain != domain -> hpList
                        remainingHits == 0 -> hpList
                        hpList.all { it == 1 } -> hpList
                        else -> {
                            hpList.map { hp ->
                                val hitsTaken = Math.min(hp - 1, remainingHits)
                                remainingHits -= hitsTaken
                                hp - hitsTaken
                            }
                        }
                    }
                }
                .toSortedMap(unitPriority)  // TODO always have units sorted by unitPriority?
                .mapValues { (unitType, hpList) ->
                    when {
                        // TODO generalize
                        unitType == UnitType.ANTIAIRCRAFT_GUN || unitType == UnitType.BOMBARDING_BATTLESHIP -> hpList
                        domain != null && unitType.domain != domain -> hpList
                        remainingHits == 0 -> hpList
                        else -> {
                            // TODO assert that hpList only has 1s?
                            val casualties = Math.min(hpList.size, remainingHits)
                            remainingHits -= casualties
                            hpList.drop(casualties)
                        }
                    }
                }
                .filterValues { it.isNotEmpty() }
        )
    }
}
