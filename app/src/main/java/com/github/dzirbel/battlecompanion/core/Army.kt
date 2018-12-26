package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.MultiSet
import java.util.EnumMap
import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (a map from each [UnitType] to a [MultiSet] of the hp's of each
 *  unit of that type; zero hp is not allowed) and [unitPriority] which determines which units to lose as casualties
 *  first.
 * [Army] has no knowledge of its state in the combat sequence (e.g. whether it is performing opening fire).
 * Note that [Army]s are immutable.
 *
 * TODO allow [unitPriority] to be more generic to support user input (i.e. just return a list of casualties from hits)
 * TODO add default unit priorities that first compare on cost and then on attack/defense and vice versa
 * TODO try to instantiate all instances of [units] as EnumMaps for performance?
 */
data class Army(
    val units: Map<UnitType, MultiSet<Int>>,
    val unitPriority: Comparator<UnitType>
) {

    companion object {

        /**
         * Returns an [Army] with the given [unitPriority] and [units] as a map from the [UnitType] to the number of
         *  units, all initialized to their respective [UnitType.maxHp].
         */
        fun fromMap(unitPriority: Comparator<UnitType>, units: Map<UnitType, Int>): Army {
            return Army(
                units = units.mapValues { (unitType, count) -> MultiSet(mapOf(unitType.maxHp to count)) },
                unitPriority = unitPriority
            )
        }
    }

    /**
     * Returns the total number of [UnitType]s (of any hp) in this [Army] satisfying the given [predicate].
     */
    fun count(predicate: (UnitType) -> Boolean): Int {
        return units.filterKeys(predicate).values.sumBy { it.size }
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
        var generalHits = 0
        val domainHits = EnumMap<Domain, Int>(Domain::class.java)

        val supportingArtillery = if (isAttacking) count { it == UnitType.ARTILLERY } else 0

        units.forEach { (unitType, hps) ->
            if (unitType.hasOpeningFire(enemies = enemies) == isOpeningFire) {
                fun roll(count: Int, rollLimit: Int) {
                    val rolls = count * unitType.numberOfRolls(enemies = enemies)
                    val hits = rand.rollDice(rolls).count { it <= rollLimit }
                    if (unitType.targetDomain == null) {
                        generalHits += hits
                    } else {
                        domainHits[unitType.targetDomain] = (domainHits[unitType.targetDomain] ?: 0) + hits
                    }
                }

                var remainingCount = hps.size

                if (unitType == UnitType.INFANTRY && supportingArtillery > 0) {
                    val supportedInfantry = Math.min(remainingCount, supportingArtillery)
                    remainingCount -= supportedInfantry

                    // TODO replace the constant 2
                    roll(count = supportedInfantry, rollLimit = 2)
                }

                roll(count = remainingCount, rollLimit = if (isAttacking) unitType.attack else unitType.defense)
            }
        }

        return HitProfile(generalHits = generalHits, domainHits = domainHits)
    }

    /**
     * Returns a copy of this [Army] with the given [HitProfile] inflicted.
     */
    fun takeHits(hits: HitProfile): Army {
        var remainingArmy = this
        hits.domainHits.forEach { (domain, count) ->
            remainingArmy = remainingArmy.takeHits(count, domain)
        }

        remainingArmy = remainingArmy.takeHits(hits.generalHits)

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
                .mapValues { (unitType, hps) ->
                    when {
                        unitType.firstRoundOnly -> hps
                        domain != null && unitType.domain != domain -> hps
                        remainingHits == 0 -> hps
                        hps.hasOnly(1) -> hps
                        else -> {
                            hps.map { hp ->
                                val hitsTaken = Math.min(hp - 1, remainingHits)
                                remainingHits -= hitsTaken
                                hp - hitsTaken
                            }
                        }
                    }
                }
                .toSortedMap(unitPriority)  // TODO always keep units sorted by unitPriority?
                .mapValues { (unitType, hps) ->
                    when {
                        unitType.firstRoundOnly -> hps
                        domain != null && unitType.domain != domain -> hps
                        remainingHits == 0 -> hps
                        else -> {
                            val casualties = Math.min(hps.size, remainingHits)
                            remainingHits -= casualties
                            // only remove units with 1 hp (which should be all units at this point)
                            hps.minus(element = 1, n = casualties)
                        }
                    }
                }
                .filterValues { it.isNotEmpty() }
        )
    }
}
